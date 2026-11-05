package com.yunmo.gateway.model.entity;
import lombok.Data;
@Data public class User {
    private Long id;
    private String username;
    private String passwordHash;
    private String nickname;
    private java.time.LocalDateTime createdAt;
}
