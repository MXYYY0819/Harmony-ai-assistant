package com.yunmo.gateway.provider;
import com.fasterxml.jackson.databind.*;
import org.springframework.stereotype.Component;
@Component public class GlmProvider extends OpenAiProvider  {
    public GlmProvider(ObjectMapper json) {
        super(json);
    }
    public String code() {
        return "glm";
    }
    protected String defaultUrl() {
        return "https://open.bigmodel.cn/api/paas/v4";
    }
    protected String defaultModel() {
        return "glm-4-flash";
    }
    @Override public StreamEvent parseChunk(String line) {
        // GLM currently follows the OpenAI delta format; keep this parser local for future variants.
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
            return StreamEvent.error("上游 glm 流格式错误");
        }
    }
}
