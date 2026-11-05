package com.yunmo.gateway.auth;
import org.junit.jupiter.api.Test;
import io.jsonwebtoken.ExpiredJwtException;
import static org.assertj.core.api.Assertions.*;
class JwtUtilTest  {
    private static final String SECRET="test-jwt-secret-at-least-thirty-two-bytes-long-2026";
    @Test void generateAndParse() {
        JwtUtil jwt=new JwtUtil(SECRET,60);
        assertThat(jwt.parse(jwt.generate(42L))).isEqualTo(42L);
    }
    @Test void expiredTokenFails() {
        JwtUtil expired=new JwtUtil(SECRET,-1);
        assertThatThrownBy(()->expired.parse(expired.generate(42L))).isInstanceOf(ExpiredJwtException.class);
    }
    @Test void wrongSignatureFails() {
        String token=new JwtUtil(SECRET,60).generate(42L);
        assertThatThrownBy(()->new JwtUtil(SECRET+"other",60).parse(token)).isInstanceOf(io.jsonwebtoken.JwtException.class);
    }
}
