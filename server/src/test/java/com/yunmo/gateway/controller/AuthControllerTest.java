package com.yunmo.gateway.controller;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@SpringBootTest @AutoConfigureMockMvc class AuthControllerTest  {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;
    @Autowired JdbcTemplate db;
    private String login()throws Exception  {
        String body=mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON).content("{\"username\":\"demo\",\"password\":\"demo123\"}")).andExpect(status().isOk()).andExpect(jsonPath("$.status").value(200)).andExpect(jsonPath("$.data.token").isNotEmpty()).andReturn().getResponse().getContentAsString();
        return json.readTree(body).path("data").path("token").asText();
    }
    @Test void loginReturnsToken()throws Exception  {
        login();
    }
    @Test void wrongPasswordReturnsBody400()throws Exception  {
        mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON).content("{\"username\":\"demo\",\"password\":\"wrong\"}")).andExpect(status().isOk()).andExpect(jsonPath("$.status").value(400));
    }
    @Test void missingTokenReturnsBody401()throws Exception  {
        mvc.perform(get("/conversation/list")).andExpect(status().isOk()).andExpect(jsonPath("$.status").value(401));
    }
    @Test void invalidTokenReturnsBody401()throws Exception  {
        mvc.perform(get("/conversation/list").header("Authorization","Bearer invalid")).andExpect(status().isOk()).andExpect(jsonPath("$.status").value(401));
    }
    @Test void malformedInputReturnsBody400()throws Exception  {
        mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON).content("{}")).andExpect(status().isOk()).andExpect(jsonPath("$.status").value(400));
    }
    @Test void conversationLifecycleAndOwnership()throws Exception  {
        String token=login();
        String created=mvc.perform(post("/conversation").header("Authorization","Bearer "+token).contentType(MediaType.APPLICATION_JSON).content("{\"title\":\"test\",\"modelCode\":\"mock\"}")).andExpect(status().isOk()).andExpect(jsonPath("$.status").value(200)).andReturn().getResponse().getContentAsString();
        long id=json.readTree(created).path("data").asLong();
        mvc.perform(get("/conversation/"+id+"/messages").header("Authorization","Bearer "+token)).andExpect(jsonPath("$.status").value(200)).andExpect(jsonPath("$.data").isArray());
        mvc.perform(get("/conversation/"+id+"/messages?page=0").header("Authorization","Bearer "+token)).andExpect(jsonPath("$.status").value(400));
        db.update("update t_conversation set user_id=99999 where id=?",id);
        mvc.perform(delete("/conversation/"+id).header("Authorization","Bearer "+token)).andExpect(jsonPath("$.status").value(404));
        db.update("update t_conversation set user_id=1 where id=?",id);
        mvc.perform(delete("/conversation/"+id).header("Authorization","Bearer "+token)).andExpect(jsonPath("$.status").value(200));
        mvc.perform(get("/conversation/"+id+"/messages").header("Authorization","Bearer "+token)).andExpect(jsonPath("$.status").value(404));
    }
    @Test void disabledModelReturnsBody400()throws Exception  {
        mvc.perform(post("/conversation").header("Authorization","Bearer "+login()).contentType(MediaType.APPLICATION_JSON).content("{\"title\":\"test\",\"modelCode\":\"gpt\"}")).andExpect(status().isOk()).andExpect(jsonPath("$.status").value(400)).andExpect(jsonPath("$.message").value("模型未启用"));
    }
    @Test void ssePreflightErrorsStayJson()throws Exception  {
        mvc.perform(post("/chat/completions").accept(MediaType.TEXT_EVENT_STREAM).contentType(MediaType.APPLICATION_JSON).content("{\"conversationId\":999999,\"modelCode\":\"mock\",\"content\":\"hello\"}")).andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)).andExpect(jsonPath("$.status").value(401));
        mvc.perform(post("/chat/completions").header("Authorization","Bearer "+login()).accept(MediaType.TEXT_EVENT_STREAM).contentType(MediaType.APPLICATION_JSON).content("{\"conversationId\":999999,\"modelCode\":\"mock\",\"content\":\"hello\"}")).andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)).andExpect(jsonPath("$.status").value(404));
    }
}
