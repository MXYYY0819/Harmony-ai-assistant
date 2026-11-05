package com.yunmo.gateway.provider;
import java.util.*;
public interface ModelProvider  {
    String code();
    String buildUrl(ModelConfig cfg);
    Map<String,String> buildHeaders(ModelConfig cfg);
    String buildBody(ModelConfig cfg,List<ChatMsg> messages,boolean stream);
    StreamEvent parseChunk(String rawLine);
    String extractText(String rawJson);
}
