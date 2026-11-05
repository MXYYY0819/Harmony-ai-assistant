package com.yunmo.gateway.service;
import java.util.*;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
import com.yunmo.gateway.model.entity.Message;
class ChatServiceTest  {
    private List<Message> history(int count,int chars) {
        List<Message> out=new ArrayList<>();
        for(int i=0;i<count;i++) {
            Message m=new Message();
            m.setId((long)i);
            m.setRole(i%2==0?"user":"assistant");
            m.setContent(String.valueOf((char)('A'+i%26)).repeat(chars));
            out.add(m);
        }
        return out;
    }
    @Test void thirtyMessagesKeepOnlyLatestWithin6000Characters() {
        List<Message> all=history(30,1000);
        var result=ChatService.context(all);
        assertThat(result).hasSize(6);
        assertThat(result.get(0).content()).isEqualTo(all.get(24).getContent());
        assertThat(result.get(5).content()).isEqualTo(all.get(29).getContent());
        assertThat(result.stream().mapToInt(m->m.content().length()).sum()).isEqualTo(6000);
    }
    @Test void atMostTwentyMessagesEvenWhenBudgetAllowsMore() {
        List<Message> all=history(30,10);
        var result=ChatService.context(all);
        assertThat(result).hasSize(20);
        assertThat(result.get(0).content()).isEqualTo(all.get(10).getContent());
    }
    @Test void doesNotSkipOverAnOversizedRecentMessage() {
        List<Message> all=history(3,10);
        all.get(1).setContent("x".repeat(6001));
        assertThat(ChatService.context(all)).hasSize(1);
    }
    @Test void emptyContextWorks() {
        assertThat(ChatService.context(List.of())).isEmpty();
    }
}
