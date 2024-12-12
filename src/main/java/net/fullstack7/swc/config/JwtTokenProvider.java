package net.fullstack7.swc.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import lombok.extern.log4j.Log4j2;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Log4j2
@Component
public class JwtTokenProvider {

  @Value("${jwt.secret}")
  private String secretKey;

  private SecretKey key;
  private final long expirationTime = 30;

  @PostConstruct
  protected void init() {
    try {
      byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
      this.key = Keys.hmacShaKeyFor(keyBytes);
      log.info("JWT key initialized successfully");
    } catch (Exception e) {
      log.error("Failed to initialize JWT key: ", e);
      throw new RuntimeException("Failed to initialize JWT key", e);
    }
  }

  // JWT 생성
  public String createToken(String memberId, String name, String email, String phone, String social, String status, String path) {
    Date now = new Date();
    Date expiration = new Date(now.getTime() + expirationTime);

    return Jwts.builder()
        .setSubject(memberId)
        .claim("memberId", memberId)
        .claim("name", name)
        .claim("email", email)
        .claim("phone", phone)
        .claim("social", social)
        .claim("status", status)
        .claim("path", path)
        .setIssuedAt(now)
        .setExpiration(expiration)
        .signWith(key)
        .compact();
  }

  // JWT 검증
  public boolean validateToken(String token) {
    try {
      log.info("Validating token...");
      Jwts.parserBuilder()
          .setSigningKey(key)
          .build()
          .parseClaimsJws(token);
      log.info("Token is valid");
      return true;
    } catch (ExpiredJwtException e) {
      log.error("Token has expired: {}", e.getMessage());
      return false;
    } catch (JwtException | IllegalArgumentException e) {
      log.error("Invalid token: {}", e.getMessage());
      return false;
    }
  }

  // JWT memberId 추출
  public String getMemberId(String token) {
    Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    return claims.getSubject();
  }

  // JWT name 추출
  public String getName(String token) {
    Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    return claims.get("name", String.class);
  }

  // JWT email 추출
  public String getEmail(String token) {
    Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    return claims.get("email", String.class);
  }

  // JWT phone 추출
  public String getPhone(String token) {
    Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    return claims.get("phone", String.class);
  }

  // JWT social 추출
  public String getSocial(String token) {
    Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    return claims.get("social", String.class);
  }

  // JWT status 추출
  public String getStatus(String token) {
    Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    return claims.get("status", String.class);
  }

}

