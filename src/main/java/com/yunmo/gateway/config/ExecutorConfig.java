package com.yunmo.gateway.config;
import org.springframework.context.annotation.*;
import jakarta.annotation.PreDestroy;
import java.util.concurrent.*;
@Configuration public class ExecutorConfig  {
    private final ExecutorService executor=Executors.newFixedThreadPool(4);
    @Bean public ExecutorService chatExecutor() {
        return executor;
    }
    @PreDestroy public void shutdown() {
        executor.shutdownNow();
    }
}
