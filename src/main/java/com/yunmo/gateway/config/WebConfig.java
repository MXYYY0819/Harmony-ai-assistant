package com.yunmo.gateway.config;
import com.yunmo.gateway.auth.JwtInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;
@Configuration public class WebConfig implements WebMvcConfigurer  {
    private final JwtInterceptor interceptor;
    public WebConfig(JwtInterceptor interceptor) {
        this.interceptor=interceptor;
    }
    @Override public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(interceptor).addPathPatterns("/**").excludePathPatterns("/auth/login");
    }
}
