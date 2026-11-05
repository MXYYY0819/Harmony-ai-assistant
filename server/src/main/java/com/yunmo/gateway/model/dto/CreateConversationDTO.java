package com.yunmo.gateway.model.dto;
import jakarta.validation.constraints.*;
public record CreateConversationDTO(@NotBlank @Size(max=200) String title,@NotBlank @Size(max=64) String modelCode) {
}
