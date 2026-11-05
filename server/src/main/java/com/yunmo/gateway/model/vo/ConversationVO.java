package com.yunmo.gateway.model.vo;
import java.time.LocalDateTime;
import com.yunmo.gateway.model.entity.Conversation;
public record ConversationVO(Long id,String title,String modelCode,LocalDateTime createdAt,LocalDateTime updatedAt) {
    public static ConversationVO of(Conversation c) {
        return new ConversationVO(c.getId(),c.getTitle(),c.getModelCode(),c.getCreatedAt(),c.getUpdatedAt());
    }
}
