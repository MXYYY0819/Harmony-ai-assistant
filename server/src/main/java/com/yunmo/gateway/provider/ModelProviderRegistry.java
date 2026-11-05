package com.yunmo.gateway.provider;
import java.util.*;
import org.springframework.stereotype.Component;
import com.yunmo.gateway.common.BusinessException;
@Component public class ModelProviderRegistry  {
    private final Map<String,ModelProvider> providers=new HashMap<>();
    public ModelProviderRegistry(List<ModelProvider> list) {
        for(ModelProvider p:list) {
            if(providers.putIfAbsent(p.code(),p)!=null)throw new IllegalArgumentException("Duplicate provider");
        }
        // Configuration uses provider=openai while the public provider code is gpt.
        if(providers.containsKey("gpt"))providers.put("openai",providers.get("gpt"));
    }
    public ModelProvider get(String code) {
        ModelProvider p=providers.get(code);
        if(p==null)throw new BusinessException(400,"未知模型提供方");
        return p;
    }
}
