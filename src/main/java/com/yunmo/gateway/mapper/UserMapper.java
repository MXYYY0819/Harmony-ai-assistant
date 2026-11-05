package com.yunmo.gateway.mapper;
import org.apache.ibatis.annotations.*;
import com.yunmo.gateway.model.entity.User;
public interface UserMapper  {
    @Select("select * from t_user where username=#{username}") User findByUsername(String username);
}
