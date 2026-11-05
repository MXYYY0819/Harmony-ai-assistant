package com.yunmo.gateway.auth;
import com.yunmo.gateway.common.BusinessException;
public final class UserContext  {
    private static final ThreadLocal<Long> CURRENT=new ThreadLocal<>();
    private UserContext() {
    }
    public static void set(Long id) {
        CURRENT.set(id);
    }
    public static Long get() {
        Long id=CURRENT.get();
        if(id==null)throw new BusinessException(401,"请先登录");
        return id;
    }
    public static void clear() {
        CURRENT.remove();
    }
}
