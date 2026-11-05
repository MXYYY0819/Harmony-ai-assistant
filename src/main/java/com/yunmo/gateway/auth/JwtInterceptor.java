package com.yunmo.gateway.auth;
import com.yunmo.gateway.common.BusinessException;
import jakarta.servlet.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.AsyncHandlerInterceptor;
@Component public class JwtInterceptor implements AsyncHandlerInterceptor  {
    private final JwtUtil jwt;
    public JwtInterceptor(JwtUtil jwt) {
        this.jwt=jwt;
    }
    @Override public boolean preHandle(HttpServletRequest req,HttpServletResponse res,Object handler) {
        UserContext.clear();
        String auth=req.getHeader("Authorization");
        if(auth==null||!auth.startsWith("Bearer "))throw new BusinessException(401,"请先登录");
        try {
            Long id=jwt.parse(auth.substring(7));
            if(id<=0)throw new IllegalArgumentException();
            UserContext.set(id);
            return true;
        }
        catch(Exception e) {
            throw new BusinessException(401,"登录已失效");
        }
    }
    @Override public void afterCompletion(HttpServletRequest req,HttpServletResponse res,Object handler,Exception ex) {
        UserContext.clear();
    }
    @Override public void afterConcurrentHandlingStarted(HttpServletRequest req,HttpServletResponse res,Object handler) {
        UserContext.clear();
    }
}
