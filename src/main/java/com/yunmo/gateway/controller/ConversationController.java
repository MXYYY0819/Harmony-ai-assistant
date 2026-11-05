package com.yunmo.gateway.controller;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.*;
import com.yunmo.gateway.auth.UserContext;
import com.yunmo.gateway.common.Result;
import com.yunmo.gateway.service.ConversationService;
import com.yunmo.gateway.model.dto.CreateConversationDTO;
import com.yunmo.gateway.model.vo.*;
@RestController @RequestMapping("/conversation") public class ConversationController  {
    private final ConversationService service;
    public ConversationController(ConversationService s) {
        service=s;
    }
    @GetMapping("/list") public Result<List<ConversationVO>> list() {
        return Result.success(service.list(UserContext.get()));
    }
    @PostMapping public Result<Long> create(@Valid @RequestBody CreateConversationDTO dto) {
        return Result.success(service.create(UserContext.get(),dto));
    }
    @GetMapping("/{id}/messages") public Result<List<MessageVO>> messages(@PathVariable Long id,@RequestParam(defaultValue="1")int page,@RequestParam(defaultValue="20")int size) {
        return Result.success(service.page(UserContext.get(),id,page,size));
    }
    @DeleteMapping("/{id}") public Result<Void> delete(@PathVariable Long id) {
        service.delete(UserContext.get(),id);
        return Result.success(null);
    }
}
