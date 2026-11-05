package com.yunmo.gateway.model.entity;
import lombok.Data;
@Data public class Conversation {
    private Long id;
    private Long userId;
    private String title;
    private String modelCode;
    private java.time.LocalDateTime createdAt;
    private java.time.LocalDateTime updatedAt;
}
