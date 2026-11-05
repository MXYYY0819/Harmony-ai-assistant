# API 明细

BASE_URL：`http://localhost:8080`。所有时间使用服务器本地时间 ISO 格式。普通响应统一 HTTP 200 + Result；401/404 等均指 body.status。除登录外请求均需 `Authorization: Bearer <token>`。

| 方法 | 路径 | 请求 | 成功 data |
|---|---|---|---|
| POST | /auth/login | username、password | token、nickname |
| GET | /conversation/list | 无 | 当前用户会话数组，updatedAt 倒序 |
| POST | /conversation | title、modelCode | 新会话 id（数字） |
| GET | /conversation/{id}/messages?page=1&size=20 | page≥1，size=1..100 | 消息数组，id 升序分页 |
| DELETE | /conversation/{id} | 无 | null |
| POST | /chat/completions | conversationId、modelCode、content | SSE 事件流 |

## 登录

```http
POST /auth/login
Content-Type: application/json

{"username":"demo","password":"demo123"}
```

```json
{"status":200,"message":"success","data":{"token":"<JWT>","nickname":"演示用户"}}
```

错误密码：`{"status":400,"message":"用户名或密码错误","data":null}`。

## 创建与列表

创建请求：`{"title":"演示会话","modelCode":"mock"}`，title 非空且≤200，modelCode 非空且≤64。返回：`{"status":200,"message":"success","data":1}`。

列表响应：

```json
{"status":200,"message":"success","data":[{"id":1,"title":"演示会话","modelCode":"mock","createdAt":"2026-11-04T12:00:00","updatedAt":"2026-11-04T12:00:10"}]}
```

未知模型或未启用模型返回 400；未启用模型 message 为“模型未启用”。

## 历史消息与删除

`GET /conversation/1/messages?page=1&size=20`：

```json
{"status":200,"message":"success","data":[{"id":1,"role":"user","content":"你好","promptTokens":2,"completionTokens":0,"createdAt":"2026-11-04T12:00:00"}]}
```

data 为数组，空页为 []，不返回额外分页包装；promptTokens/completionTokens 为字符数粗估。非法分页返回 400。不存在或其他用户的会话均返回 404，不泄露归属信息。

`DELETE /conversation/1` 事务删除消息与会话，返回 `{"status":200,"message":"success","data":null}`；再次删除返回 404。

## 流式聊天

```http
POST /chat/completions
Authorization: Bearer <JWT>
Content-Type: application/json

{"conversationId":1,"modelCode":"mock","content":"你好"}
```

content 非空且≤6000，conversationId 为正整数。先校验会话归属、模型配置，再保存 user。取最近 20 条历史并按 6000 字符总预算删去最早历史。成功响应 Content-Type 为 text/event-stream，示例：

```text
data:{"type":"delta","content":"你好，"}

data:{"type":"delta","content":"我是"}

data:{"type":"done"}

```

delta 按顺序追加；done 只发一次，此前完整 assistant 已落库。mock 共 20 片，间隔 500ms；上游错误或提前 EOF 将发送 `{"type":"error","content":"模型调用失败，请稍后重试"}` 并关闭，不发 done、不保存半截 assistant。客户端断连会取消后台 Future/关闭上游 Stream。请求超时 5 分钟。

流建立前的鉴权/参数/归属/模型错误仍为 HTTP 200 application/json Result，例如 `{"status":401,"message":"请先登录","data":null}`。客户端需先根据响应 Content-Type 区分 JSON 与 SSE。

## 错误约定

| body.status | 含义 |
|---|---|
| 200 | success |
| 400 | 参数、密码或模型配置错误 |
| 401 | 未登录、JWT 过期或签名无效 |
| 404 | 资源不存在或越权 |
| 500 | 服务端异常 |

无 actuator 依赖，故不提供可选健康检查接口。
