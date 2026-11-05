-- MySQL does not support CREATE INDEX IF NOT EXISTS; inline indexes are restart-safe.
create table if not exists t_user (
 id bigint auto_increment primary key,
 username varchar(64) not null unique,
 password_hash varchar(100) not null,
 nickname varchar(64),
 created_at timestamp default current_timestamp
);
create table if not exists t_conversation (
 id bigint auto_increment primary key,
 user_id bigint not null,
 title varchar(200),
 model_code varchar(64),
 created_at timestamp default current_timestamp,
 updated_at timestamp default current_timestamp
, index idx_conv_user(user_id, updated_at, id)
);
create table if not exists t_message (
 id bigint auto_increment primary key,
 conversation_id bigint not null,
 role varchar(16) not null,
 content text,
 prompt_tokens int default 0,
 completion_tokens int default 0,
 created_at timestamp default current_timestamp
, index idx_msg_conv(conversation_id, id)
);
