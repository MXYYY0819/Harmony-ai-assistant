package com.yunmo.gateway.mapper;
import org.apache.ibatis.annotations.*;
import com.yunmo.gateway.model.entity.Message;
import java.util.List;
public interface MessageMapper  {
    @Select("select * from t_message where conversation_id=#{id} order by id desc limit 20") List<Message> recent(Long id);
    @Select("select * from t_message where conversation_id=#{id} order by id asc limit #{size} offset #{offset}") List<Message> page(@Param("id")Long id,@Param("offset")long offset,@Param("size")int size);
    @Insert("insert into t_message(conversation_id,role,content,prompt_tokens,completion_tokens) values(#{conversationId},#{role},#{content},#{promptTokens},#{completionTokens})") @Options(useGeneratedKeys=true,keyProperty="id") int insert(Message m);
    @Delete("delete from t_message where conversation_id=#{id}") int deleteByConversation(Long id);
}
