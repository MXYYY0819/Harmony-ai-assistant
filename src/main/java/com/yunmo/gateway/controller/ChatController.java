package com.yunmo.gateway.controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import jakarta.validation.Valid;
import com.yunmo.gateway.auth.UserContext;
import com.yunmo.gateway.model.dto.ChatDTO;
import com.yunmo.gateway.service.ChatService;
@RestController @RequestMapping("/chat") public class ChatController  {
    private final ChatService service;
    public ChatController(ChatService s) {
        service=s;
    }
    @PostMapping("/completions") public SseEmitter chatCompletions(@Valid @RequestBody ChatDTO dto) {
        return service.chat(UserContext.get(),dto);
    }
}
