package com.yunmo.gateway.provider;
import com.fasterxml.jackson.databind.*;
import org.springframework.stereotype.Component;
@Component public class DeepSeekProvider extends OpenAiProvider  {
    public DeepSeekProvider(ObjectMapper json) {
        super(json);
    }
    public String code() {
        return "deepseek";
    }
    protected String defaultUrl() {
        return "https://api.deepseek.com";
    }
    protected String defaultModel() {
        return "deepseek-chat";
    }
    @Override public StreamEvent parseChunk(String line) {
        // Deliberately read only delta.content; reasoning_content must never reach the client.
        if(line==null||line.isBlank()||line.startsWith(":")||!line.startsWith("data:"))return null;
        String data=line.substring(5).trim();
        if(data.equals("[DONE]"))return StreamEvent.done();
        if(data.isEmpty())return null;
        try {
            JsonNode n=json.readTree(data);
            if(n.has("error"))return StreamEvent.error("上游模型返回错误");
            JsonNode content=n.path("choices").path(0).path("delta").path("content");
            return content.isTextual()&&!content.asText().isEmpty()?StreamEvent.delta(content.asText()):null;
        }
        catch(Exception e) {
            return StreamEvent.error("上游 deepseek 流格式错误");
        }
    }
}
