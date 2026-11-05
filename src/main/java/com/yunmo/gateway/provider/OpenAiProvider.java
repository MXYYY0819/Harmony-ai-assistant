package com.yunmo.gateway.provider;
import java.util.*;
import com.fasterxml.jackson.databind.*;
import org.springframework.stereotype.Component;
@Component public class OpenAiProvider implements ModelProvider  {
    protected final ObjectMapper json;
    public OpenAiProvider(ObjectMapper json) {
        this.json=json;
    }
    public String code() {
        return "gpt";
    }
    protected String defaultUrl() {
        return "https://api.openai.com";
    }
    protected String defaultModel() {
        return "gpt-4o-mini";
    }
    public String buildUrl(ModelConfig cfg) {
        String base=cfg.getBaseUrl()==null||cfg.getBaseUrl().isBlank()?defaultUrl():cfg.getBaseUrl();
        base=base.replaceAll("/+$","");
        return base+(base.endsWith("/v1")||base.endsWith("/v4")?"/chat/completions":"/v1/chat/completions");
    }
    public Map<String,String> buildHeaders(ModelConfig cfg) {
        return Map.of("Authorization","Bearer "+(cfg.getApiKey()==null?"":cfg.getApiKey()),"Content-Type","application/json","Accept","text/event-stream");
    }
    public String buildBody(ModelConfig cfg,List<ChatMsg> messages,boolean stream) {
        try {
            return json.writeValueAsString(Map.of("model",cfg.getModelName()==null?defaultModel():cfg.getModelName(),"messages",messages,"stream",stream));
        }
        catch(Exception e) {
            throw new IllegalArgumentException("Cannot serialize model request",e);
        }
    }
    public StreamEvent parseChunk(String line) {
        if(line==null||line.isBlank()||line.startsWith(":")||!line.startsWith("data:"))return null;
        String data=line.substring(5).trim();
        if(data.equals("[DONE]"))return StreamEvent.done();
        if(data.isEmpty())return null;
        try {
            JsonNode n=json.readTree(data);
            if(n.has("error"))return StreamEvent.error("上游模型返回错误");
            JsonNode delta=n.path("choices").path(0).path("delta").path("content");
            return delta.isTextual()&&!delta.asText().isEmpty()?StreamEvent.delta(delta.asText()):null;
        }
        catch(Exception e) {
            return StreamEvent.error("上游 OpenAI 流格式错误");
        }
    }
    public String extractText(String raw) {
        try {
            return json.readTree(raw).path("choices").path(0).path("message").path("content").asText("");
        }
        catch(Exception e) {
            throw new IllegalArgumentException("Invalid model response",e);
        }
    }
}
