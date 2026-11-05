package com.yunmo.gateway.config;
import java.util.*;
import com.yunmo.gateway.provider.ModelConfig;
import com.yunmo.gateway.common.BusinessException;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;
import org.slf4j.*;
import lombok.Data;
@Data @Component @ConfigurationProperties(prefix="ai") public class ModelProperties  {
    private List<ModelConfig> models=new ArrayList<>();
    @PostConstruct public void validate() {
        Set<String> codes=new HashSet<>();
        for(ModelConfig c:models) {
            if(c.getCode()==null||!codes.add(c.getCode()))throw new IllegalArgumentException("Duplicate/missing model code");
            if(c.isEnabled()&&!"mock".equals(c.getProvider())&&(c.getApiKey()==null||c.getApiKey().isBlank()))LoggerFactory.getLogger(getClass()).warn("Model {} enabled with empty API key",c.getCode());
        }
    }
    public ModelConfig get(String code) {
        return models.stream().filter(c->c.getCode().equals(code)).findFirst().orElseThrow(()->new BusinessException(400,"未知模型"));
    }
    public ModelConfig enabled(String code) {
        ModelConfig c=get(code);
        if(!c.isEnabled())throw new BusinessException(400,"模型未启用");
        return c;
    }
}
