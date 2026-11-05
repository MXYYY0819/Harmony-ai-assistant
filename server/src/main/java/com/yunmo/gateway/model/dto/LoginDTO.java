package com.yunmo.gateway.model.dto;
import jakarta.validation.constraints.*;
public record LoginDTO(@NotBlank @Size(max=64) String username,@NotBlank @Size(max=100) String password) {
    @Override
    public String toString() {
        return "LoginDTO[username=" + username + ", password=***]";
    }
}
