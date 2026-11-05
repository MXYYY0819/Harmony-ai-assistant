package com.yunmo.gateway.mapper;
import org.apache.ibatis.annotations.*;
import com.yunmo.gateway.model.entity.Conversation;
import java.util.List;
public interface ConversationMapper  {
    @Select("select * from t_conversation where id=#{id}") Conversation find(Long id);
    @Select("select * from t_conversation where id=#{id} for update") Conversation findForUpdate(Long id);
    @Select("select * from t_conversation where user_id=#{userId} order by updated_at desc,id desc") List<Conversation> list(Long userId);
    @Insert("insert into t_conversation(user_id,title,model_code) values(#{userId},#{title},#{modelCode})") @Options(useGeneratedKeys=true,keyProperty="id") int insert(Conversation c);
    @Delete("delete from t_conversation where id=#{id}") int delete(Long id);
    @Update("update t_conversation set updated_at=current_timestamp where id=#{id}") int touch(Long id);
}
