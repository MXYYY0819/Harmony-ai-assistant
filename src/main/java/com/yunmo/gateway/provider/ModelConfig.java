package com.yunmo.gateway.provider;
import lombok.Data;
import lombok.ToString;
@Data public class ModelConfig  {
    private String code;
    private String provider;
    private String baseUrl;
    @ToString.Exclude
    private String apiKey;
    private String modelName;
    private boolean enabled;
}
