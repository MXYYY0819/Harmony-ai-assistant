package com.yunmo.gateway.provider;
import java.util.*;
import org.springframework.stereotype.Component;
@Component public class MockProvider implements ModelProvider  {
    public String code() {
        return "mock";
    }
    public String buildUrl(ModelConfig c) {
        return "mock://local";
    }
    public Map<String,String> buildHeaders(ModelConfig c) {
        return Map.of();
    }
    public String buildBody(ModelConfig c,List<ChatMsg> m,boolean stream) {
        return "";
    }
    public StreamEvent parseChunk(String raw) {
        return "[DONE]".equals(raw)?StreamEvent.done():StreamEvent.delta(raw);
    }
    public String extractText(String raw) {
        return raw;
    }
    public List<String> chunks() {
        return List.of("你好，","我是","本地","模拟","助手。","本次","回答","无需","联网，","也不","需要","配置","任何","模型","密钥。","网关","正在","逐片","发送","消息。");
    }
}
