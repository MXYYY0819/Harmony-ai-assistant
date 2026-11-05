package com.yunmo.gateway.service;
import java.io.*;
import java.net.URI;
import java.net.http.*;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.stream.Stream;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import com.yunmo.gateway.common.BusinessException;
import com.yunmo.gateway.config.ModelProperties;
import com.yunmo.gateway.mapper.*;
import com.yunmo.gateway.model.dto.ChatDTO;
import com.yunmo.gateway.model.entity.Message;
import com.yunmo.gateway.provider.*;
@Service public class ChatService  {
    private final ConversationService conversations;
    private final MessageMapper messages;
    private final ConversationMapper conversationMapper;
    private final ModelProperties models;
    private final ModelProviderRegistry registry;
    private final ExecutorService executor;
    private final ObjectMapper json;
    private final TransactionTemplate transactions;
    private final HttpClient http=HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(20)).build();
    public ChatService(ConversationService c,MessageMapper m,ConversationMapper cm,ModelProperties p,ModelProviderRegistry r,ExecutorService e,ObjectMapper j,TransactionTemplate t) {
        conversations=c;
        messages=m;
        conversationMapper=cm;
        models=p;
        registry=r;
        executor=e;
        json=j;
        transactions=t;
    }
    /** Input is chronological. Retain a contiguous suffix of at most 20 messages and 6000 characters. */ public static List<ChatMsg> context(List<Message> history) {
        List<ChatMsg> kept=new ArrayList<>();
        int budget=6000;
        for(int i=history.size()-1;i>=Math.max(0,history.size()-20);i--) {
            Message m=history.get(i);
            String text=m.getContent()==null?"":m.getContent();
            if(text.length()>budget)break;
            kept.add(new ChatMsg(m.getRole(),text));
            budget-=text.length();
        }
        Collections.reverse(kept);
        return kept;
    }
    public SseEmitter chat(Long user,ChatDTO dto) {
        conversations.owned(dto.conversationId(),user);
        ModelConfig config=models.enabled(dto.modelCode());
        ModelProvider provider=registry.get(config.getProvider());
        List<ChatMsg> context=transactions.execute(status-> {
            conversations.lockOwned(dto.conversationId(),user);
            List<Message> recent=new ArrayList<>(messages.recent(dto.conversationId()));
            Collections.reverse(recent);
            List<ChatMsg> selected=new ArrayList<>(context(recent));
            int total=selected.stream().mapToInt(m->m.content().length()).sum()+dto.content().length();
            while(total>6000&&!selected.isEmpty()) {
                total-=selected.remove(0).content().length();
            }
            selected.add(new ChatMsg("user",dto.content()));
            messages.insert(message(dto.conversationId(),"user",dto.content(),dto.content().length(),0));
            conversationMapper.touch(dto.conversationId());
            return selected;
        });
        SseEmitter emitter=new SseEmitter(300000L);
        Session session=new Session();
        emitter.onCompletion(session::cancel);
        emitter.onTimeout(()-> {
            session.cancel();emitter.complete();
        });
        emitter.onError(error->session.cancel());
        FutureTask<Void> work=new FutureTask<>(()-> {
            run(session,emitter,provider,config,context,dto,user);return null;
        });
        session.bind(work);
        try {
            executor.execute(work);
        }
        catch(RejectedExecutionException e) {
            session.cancel();
            throw new BusinessException(500,"服务正在关闭");
        }
        return emitter;
    }
    private void run(Session session,SseEmitter emitter,ModelProvider provider,ModelConfig config,List<ChatMsg> context,ChatDTO dto,Long user) {
        StringBuilder answer=new StringBuilder();
        boolean done=false;
        try {
            if(provider instanceof MockProvider mock) {
                for(String chunk:mock.chunks()) {
                    Thread.sleep(500);
                    if(session.cancelled.get())return;
                    send(emitter,StreamEvent.delta(chunk));
                    answer.append(chunk);
                }
                done=true;
            }
            else {
                HttpRequest.Builder request=HttpRequest.newBuilder(URI.create(provider.buildUrl(config))).timeout(Duration.ofMinutes(5)).POST(HttpRequest.BodyPublishers.ofString(provider.buildBody(config,context,true)));
                provider.buildHeaders(config).forEach(request::header);
                HttpResponse<Stream<String>> response=http.send(request.build(),HttpResponse.BodyHandlers.ofLines());
                try(Stream<String> lines=response.body()) {
                    session.bind(lines);
                    if(session.cancelled.get())return;
                    if(response.statusCode()<200||response.statusCode()>=300)throw new IOException("Upstream HTTP "+response.statusCode());
                    Iterator<String> it=lines.iterator();
                    while(!session.cancelled.get()&&it.hasNext()) {
                        StreamEvent event=provider.parseChunk(it.next());
                        if(event==null)continue;
                        if("done".equals(event.type())) {
                            done=true;
                            break;
                        }
                        if("error".equals(event.type()))throw new IOException(event.content());
                        if("delta".equals(event.type())) {
                            send(emitter,event);
                            answer.append(event.content());
                        }
                    }
                }
                finally {
                    session.upstream.set(null);
                }
            }
            if(!done&&!session.cancelled.get())throw new IOException("Upstream ended before done");
            if(done&&!session.cancelled.get()) {
                transactions.executeWithoutResult(status-> {
                    if(session.cancelled.get()) {
                        status.setRollbackOnly();return;
                    }
                    conversations.lockOwned(dto.conversationId(),user);
                    messages.insert(message(dto.conversationId(),"assistant",answer.toString(),0,answer.length()));
                    conversationMapper.touch(dto.conversationId());
                });
                if(!session.cancelled.get()) {
                    send(emitter,StreamEvent.done());
                    emitter.complete();
                }
            }
        }
        catch(InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        catch(Exception e) {
            if(!session.cancelled.get()) {
                try {
                    send(emitter,StreamEvent.error("模型调用失败，请稍后重试"));
                    emitter.complete();
                }
                catch(Exception disconnected) {
                    emitter.completeWithError(disconnected);
                }
            }
        }
        finally {
            session.cancel();
        }
    }
    private void send(SseEmitter emitter,StreamEvent event)throws IOException {
        emitter.send(SseEmitter.event().data(json.writeValueAsString(event)));
    }
    private static Message message(Long id,String role,String content,int prompt,int completion) {
        Message m=new Message();
        m.setConversationId(id);
        m.setRole(role);
        m.setContent(content);
        m.setPromptTokens(prompt);
        m.setCompletionTokens(completion);
        return m;
    }
    private static final class Session  {
        final AtomicBoolean cancelled=new AtomicBoolean();
        final AtomicReference<Future<?>> future=new AtomicReference<>();
        final AtomicReference<Stream<String>> upstream=new AtomicReference<>();
        void bind(Future<?> task) {
            future.set(task);
            if(cancelled.get())task.cancel(true);
        }
        void bind(Stream<String> stream) {
            upstream.set(stream);
            if(cancelled.get())stream.close();
        }
        void cancel() {
            cancelled.set(true);
            Future<?> f=future.getAndSet(null);
            if(f!=null)f.cancel(true);
            Stream<String> stream=upstream.getAndSet(null);
            if(stream!=null) {
                try {
                    stream.close();
                }
                catch(RuntimeException ignored) {
                    /* Cancellation is best-effort after the Future is interrupted. */
                }
            }
        }
    }
}
