package com.yunmo.gateway.provider;
import com.fasterxml.jackson.annotation.JsonInclude;
@JsonInclude(JsonInclude.Include.NON_NULL) public record StreamEvent(String type,String content) {
    public static StreamEvent delta(String text) {
        return new StreamEvent("delta",text);
    }
    public static StreamEvent done() {
        return new StreamEvent("done",null);
    }
    public static StreamEvent error(String message) {
        return new StreamEvent("error",message);
    }
}
