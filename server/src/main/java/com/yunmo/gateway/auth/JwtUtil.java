package com.yunmo.gateway.auth;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
@Component public class JwtUtil  {
    private final SecretKey key;
    private final long expirationSeconds;
    public JwtUtil(@Value("${jwt.secret}")String secret,@Value("${jwt.expiration-seconds:86400}")long expirationSeconds) {
        this.key=Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationSeconds=expirationSeconds;
    }
    public String generate(Long userId) {
        Instant now=Instant.now();
        return Jwts.builder().subject(userId.toString()).issuedAt(Date.from(now)).expiration(Date.from(now.plusSeconds(expirationSeconds))).signWith(key,Jwts.SIG.HS256).compact();
    }
    public Long parse(String token) {
        return Long.valueOf(Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().getSubject());
    }
}
