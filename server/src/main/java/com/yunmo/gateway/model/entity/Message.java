package com.yunmo.gateway.model.entity;
import lombok.Data;
@Data public class Message {
    private Long id;
    private Long conversationId;
    private String role;
    private String content;
    private Integer promptTokens;
    private Integer completionTokens;
    private java.time.LocalDateTime createdAt;
}
