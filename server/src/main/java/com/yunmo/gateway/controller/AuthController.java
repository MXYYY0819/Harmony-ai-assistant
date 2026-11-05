package com.yunmo.gateway.controller;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import com.yunmo.gateway.common.Result;
import com.yunmo.gateway.service.AuthService;
import com.yunmo.gateway.model.dto.LoginDTO;
import com.yunmo.gateway.model.vo.LoginVO;
@RestController @RequestMapping("/auth") public class AuthController  {
    private final AuthService service;
    public AuthController(AuthService s) {
        service=s;
    }
    @PostMapping("/login") public Result<LoginVO> login(@Valid @RequestBody LoginDTO dto) {
        return Result.success(service.login(dto));
    }
}
