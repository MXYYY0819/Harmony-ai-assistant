-- Demo account plaintext: demo / demo123. BCrypt hash only; do not use demo credentials in production.
insert into t_user(username,password_hash,nickname)
select 'demo','$2b$10$abcdefghijklmnopqrstuuoUY6nf..Av.ta8Bs1rtz2fBmxmB6Zvu','演示用户'
where not exists(select 1 from t_user where username='demo');
