package com.yunmo.gateway.service;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.yunmo.gateway.mapper.UserMapper;
import com.yunmo.gateway.auth.JwtUtil;
import com.yunmo.gateway.model.dto.LoginDTO;
import com.yunmo.gateway.model.vo.LoginVO;
import com.yunmo.gateway.model.entity.User;
import com.yunmo.gateway.common.BusinessException;
@Service public class AuthService  {
    private final UserMapper users;
    private final JwtUtil jwt;
    private final BCryptPasswordEncoder encoder=new BCryptPasswordEncoder();
    public AuthService(UserMapper users,JwtUtil jwt) {
        this.users=users;
        this.jwt=jwt;
    }
    public LoginVO login(LoginDTO dto) {
        User u=users.findByUsername(dto.username());
        if(u==null||!encoder.matches(dto.password(),u.getPasswordHash()))throw new BusinessException(400,"用户名或密码错误");
        return new LoginVO(jwt.generate(u.getId()),u.getNickname());
    }
}
