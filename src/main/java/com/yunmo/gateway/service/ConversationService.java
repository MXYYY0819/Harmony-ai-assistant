package com.yunmo.gateway.service;
import java.util.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.yunmo.gateway.mapper.*;
import com.yunmo.gateway.model.dto.*;
import com.yunmo.gateway.model.entity.*;
import com.yunmo.gateway.model.vo.*;
import com.yunmo.gateway.common.BusinessException;
import com.yunmo.gateway.config.ModelProperties;
@Service public class ConversationService  {
    private final ConversationMapper conversations;
    private final MessageMapper messages;
    private final ModelProperties models;
    public ConversationService(ConversationMapper c,MessageMapper m,ModelProperties p) {
        conversations=c;
        messages=m;
        models=p;
    }
    public Conversation owned(Long id,Long user) {
        Conversation c=conversations.find(id);
        if(c==null||!c.getUserId().equals(user))throw new BusinessException(404,"会话不存在");
        return c;
    }
    /** Call only within a transaction; serialize message writes against deletion. */
    public Conversation lockOwned(Long id,Long user) {
        Conversation c=conversations.findForUpdate(id);
        if(c==null||!c.getUserId().equals(user))throw new BusinessException(404,"会话不存在");
        return c;
    }
    public List<ConversationVO> list(Long user) {
        return conversations.list(user).stream().map(ConversationVO::of).toList();
    }
    public Long create(Long user,CreateConversationDTO dto) {
        models.enabled(dto.modelCode());
        Conversation c=new Conversation();
        c.setUserId(user);
        c.setTitle(dto.title());
        c.setModelCode(dto.modelCode());
        conversations.insert(c);
        return c.getId();
    }
    public List<MessageVO> page(Long user,Long id,int page,int size) {
        owned(id,user);
        if(page<1||size<1||size>100)throw new BusinessException(400,"分页参数错误");
        return messages.page(id,((long)page-1)*size,size).stream().map(MessageVO::of).toList();
    }
    @Transactional public void delete(Long user,Long id) {
        lockOwned(id,user);
        messages.deleteByConversation(id);
        conversations.delete(id);
    }
}
