package com.yunmo.gateway.provider;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
class OpenAiProviderTest  {
    private final ObjectMapper json=new ObjectMapper();
    private final OpenAiProvider provider=new OpenAiProvider(json);
    @Test void parsesDeltaDoneAndIgnoresComments() {
        assertThat(provider.parseChunk("data: {\"choices\":[{\"delta\":{\"content\":\"你好\"}}]}" )).isEqualTo(StreamEvent.delta("你好"));
        assertThat(provider.parseChunk("data: [DONE]")).isEqualTo(StreamEvent.done());
        assertThat(provider.parseChunk("")).isNull();
        assertThat(provider.parseChunk(": heartbeat")).isNull();
        assertThat(provider.parseChunk("event: message")).isNull();
        assertThat(provider.parseChunk("data: {\"choices\":[{\"delta\":{\"role\":\"assistant\"}}]}" )).isNull();
    }
    @Test void glmHasOwnParser() {
        assertThat(new GlmProvider(json).parseChunk("data: [DONE]")).isEqualTo(StreamEvent.done());
    }
    @Test void deepSeekDoesNotLeakReasoning() {
        DeepSeekProvider p=new DeepSeekProvider(json);
        assertThat(p.parseChunk("data: {\"choices\":[{\"delta\":{\"reasoning_content\":\"secret\"}}]}" )).isNull();
        assertThat(p.parseChunk("data: {\"choices\":[{\"delta\":{\"reasoning_content\":\"secret\",\"content\":\"answer\"}}]}" )).isEqualTo(StreamEvent.delta("answer"));
    }
    @Test void buildsProviderUrlsAndBody()throws Exception  {
        ModelConfig cfg=new ModelConfig();
        cfg.setModelName("test-model");
        assertThat(provider.buildUrl(cfg)).isEqualTo("https://api.openai.com/v1/chat/completions");
        assertThat(new GlmProvider(json).buildUrl(cfg)).isEqualTo("https://open.bigmodel.cn/api/paas/v4/chat/completions");
        assertThat(new DeepSeekProvider(json).buildUrl(cfg)).isEqualTo("https://api.deepseek.com/v1/chat/completions");
        assertThat(json.readTree(provider.buildBody(cfg,java.util.List.of(new ChatMsg("user","hi")),true)).path("stream").asBoolean()).isTrue();
    }
}
