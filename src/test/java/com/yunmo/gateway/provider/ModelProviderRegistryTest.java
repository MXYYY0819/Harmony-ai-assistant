package com.yunmo.gateway.provider;
import java.util.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
import com.yunmo.gateway.common.BusinessException;
class ModelProviderRegistryTest  {
    @Test void resolvesEachProviderAndConfigurationAlias() {
        ObjectMapper json=new ObjectMapper();
        OpenAiProvider gpt=new OpenAiProvider(json);
        GlmProvider glm=new GlmProvider(json);
        DeepSeekProvider deep=new DeepSeekProvider(json);
        MockProvider mock=new MockProvider();
        ModelProviderRegistry registry=new ModelProviderRegistry(List.of(gpt,glm,deep,mock));
        assertThat(registry.get("gpt")).isSameAs(gpt);
        assertThat(registry.get("openai")).isSameAs(gpt);
        assertThat(registry.get("glm")).isSameAs(glm);
        assertThat(registry.get("deepseek")).isSameAs(deep);
        assertThat(registry.get("mock")).isSameAs(mock);
        assertThatThrownBy(()->registry.get("unknown")).isInstanceOfSatisfying(BusinessException.class,e->assertThat(e.getStatus()).isEqualTo(400));
    }
}
