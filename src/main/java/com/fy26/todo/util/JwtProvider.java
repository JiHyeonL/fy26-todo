package com.fy26.todo.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtProvider {

    @Value("${jwt.secret-key}")
    private String secretKeyString;

    @Value("${jwt.expiration-ms}")
    private long expirationMs;

    private SecretKey secretKey;

    @PostConstruct
    public void init() {
        this.secretKey = Keys.hmacShaKeyFor(secretKeyString.getBytes());
    }

    public String generateToken(final Long memberId) {
        final Date now = new Date();
        final Date expiryDate = new Date(now.getTime() + expirationMs);
        return Jwts.builder()
                .subject(memberId.toString())
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(secretKey, Jwts.SIG.HS256)
                .compact();
    }

    public Long getMemberId(final String token) {
        final Claims claims = getClaimsJws(token).getPayload();
        return Long.valueOf(claims.getSubject());
    }

    public boolean validateToken(final String token) {
        try {
            getClaimsJws(token);
            return true;
        } catch (final Exception e) {
            return false;
        }
    }

    private Jws<Claims> getClaimsJws(final String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token);
    }
}
