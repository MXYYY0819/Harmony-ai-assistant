package com.yunmo.gateway.model.vo;
import java.time.LocalDateTime;
import com.yunmo.gateway.model.entity.Message;
public record MessageVO(Long id,String role,String content,Integer promptTokens,Integer completionTokens,LocalDateTime createdAt) {
    public static MessageVO of(Message m) {
        return new MessageVO(m.getId(),m.getRole(),m.getContent(),m.getPromptTokens(),m.getCompletionTokens(),m.getCreatedAt());
    }
}
