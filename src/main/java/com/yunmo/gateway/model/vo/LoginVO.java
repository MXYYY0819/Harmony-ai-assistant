package com.yunmo.gateway.model.vo;
public record LoginVO(String token,String nickname) {
    @Override
    public String toString() {
        return "LoginVO[token=***, nickname=" + nickname + "]";
    }
}
