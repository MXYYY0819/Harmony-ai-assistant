package com.yunmo.gateway.common;
public record Result<T>(int status,String message,T data) {
    public static <T> Result<T> success(T data) {
        return new Result<>(200,"success",data);
    }
    public static Result<Void> failure(int code,String message) {
        return new Result<>(code,message,null);
    }
}
