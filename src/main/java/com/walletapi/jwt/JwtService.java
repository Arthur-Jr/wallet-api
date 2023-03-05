package com.walletapi.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

/**
 * Jwt service, with methods to work on JWT.
 */
@Service
public class JwtService {

  @Value("${JWT_SECRET_KEY}")
  private String secretKey;

  public JwtService(@Value("${JWT_SECRET_KEY}") String secretKey) {
    this.secretKey = secretKey;
  }

  public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  /**
   * Extract specific Claim.
   */
  public <T> T extractClaim(String token, Function<Claims, T> resolver) {
    Claims claims = this.extractAllClaims(token);
    return resolver.apply(claims);
  }

  /**
   * Generate JWT with extraClaims.
   */
  public String generateToken(Map<String, Object> extraClaims, UserDetails user) {
    long twentyFiveHour = 90000000;

    return Jwts.builder()
        .setClaims(extraClaims)
        .setSubject(user.getUsername())
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + twentyFiveHour))
        .signWith(this.getSignKey(), SignatureAlgorithm.HS256)
        .compact();
  }

  /**
   * Generate JWT without extraClaims.
   */
  public String generateToken(UserDetails user) {
    return this.generateToken(new HashMap<>(), user);
  }

  public boolean isTokenValid(String token, UserDetails user) {
    String username = extractUsername(token);
    return (username.equals(user.getUsername())) && !isTokenExpired(token);
  }

  private boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  private Date extractExpiration(String token) {
    return this.extractClaim(token, Claims::getExpiration);
  }

  private Claims extractAllClaims(String token) {
    return Jwts
        .parserBuilder()
        .setSigningKey(getSignKey())
        .build()
        .parseClaimsJws(token)
        .getBody();
  }

  private Key getSignKey() {
    byte[] keyByte = Decoders.BASE64.decode(this.secretKey);
    return Keys.hmacShaKeyFor(keyByte);
  }

}
