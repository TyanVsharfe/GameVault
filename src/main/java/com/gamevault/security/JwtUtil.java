package com.gamevault.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String base64Key;
    private SecretKey jwtSecret;

    @PostConstruct
    private void init() {
        jwtSecret = Keys.hmacShaKeyFor(Base64.getDecoder().decode(base64Key));
    }

    public String generateAccessToken(UserDetails userDetails) {
        long accessTokenExpirationMs = 3;
        return generateToken(userDetails, accessTokenExpirationMs, false);
    }

    public String generateRefreshToken(UserDetails userDetails) {
        long refreshTokenExpirationMs = 336;
        return generateToken(userDetails, refreshTokenExpirationMs, true);
    }

    private String generateToken(UserDetails userDetails, long expirationMs, boolean isRefresh) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", userDetails.getAuthorities());
        claims.put("isRefresh", isRefresh);

        // Добавьте дополнительные данные, если нужно (например, имя, аватарку)
        // claims.put("name", ((YourUserClass) userDetails).getName());
        // claims.put("avatar", ((YourUserClass) userDetails).getAvatarUrl());

        return Jwts.builder()
                .subject(userDetails.getUsername())
                .claims(claims)
                .issuedAt(java.util.Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plus(expirationMs, ChronoUnit.HOURS)))
                .signWith(jwtSecret)
                .compact();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().verifyWith(jwtSecret).build().parseSignedClaims(token).getPayload();
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}
