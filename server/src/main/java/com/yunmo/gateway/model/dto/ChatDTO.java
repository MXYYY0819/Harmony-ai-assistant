package com.yunmo.gateway.model.dto;
import jakarta.validation.constraints.*;
public record ChatDTO(@NotNull @Positive Long conversationId,@NotBlank @Size(max=64) String modelCode,@NotBlank @Size(max=6000) String content) {
}
