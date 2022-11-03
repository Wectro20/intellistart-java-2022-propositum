package com.intellias.intellistart.interviewplanning.security.config;

import com.intellias.intellistart.interviewplanning.exceptions.UserNotFoundException;
import com.intellias.intellistart.interviewplanning.model.User;
import com.intellias.intellistart.interviewplanning.model.User.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

/**
 * Util class for creating, parsing and validating jwt token.
 */
@Component
public class JwtTokenUtil {

  private static final long JWT_TOKEN_VALIDITY = 5 * (long) 60 * 60;
  @Value("${jwt.secret}")
  private String secret;

  /**
   * Retrieve user email from token.
   *
   * @param token provided access token.
   */
  public String getUserEmailFromToken(String token) {
    return getClaimsFromToken(token, Claims::getSubject);
  }

  /**
   * Retrieve expiration date from token.
   *
   * @param token provided access token.
   */
  public Date getExpirationDateFromToken(String token) {
    return getClaimsFromToken(token, Claims::getExpiration);
  }

  /**
   * Retrieve user email from token.
   *
   * @param token          provided access token.
   * @param claimsResolver function which applies to object, which passed to claimsResolver.
   */
  public <T> T getClaimsFromToken(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = getAllClaimsFromToken(token);
    return claimsResolver.apply(claims);
  }

  /**
   * Retrieve all claims from passed token.
   *
   * @param token jwt access token.
   */
  public Claims getAllClaimsFromToken(String token) {
    return
        Jwts.parserBuilder().setSigningKey(secret.getBytes()).build().parseClaimsJws(token)
            .getBody();
  }

  /**
   * Checks whether passed token is expired.
   *
   * @param token jwt access token.
   * @return true is token us expired.
   */
  private Boolean isTokenExpired(String token) {
    final Date expiration = getExpirationDateFromToken(token);
    return expiration.before(new Date());
  }

  /**
   * Parse retrieved token to User.
   *
   * @param token jwt access token.
   * @return User, which parsed from token.
   */
  public User parseTokenToUser(String token) {
    try {
      Claims claims = Jwts.parserBuilder()
          .setSigningKey(secret.getBytes()).build()
          .parseClaimsJws(token)
          .getBody();

      User userFromToken = new User();
      userFromToken.setEmail(claims.getSubject());

      List<Map<String, String>> authorities = (List<Map<String, String>>) claims.get("role");

      Map<String, String> mapOfAuthorities = authorities.get(0);
      UserRole role = UserRole.valueOf(mapOfAuthorities.get("authority"));

      userFromToken.setRole(role);
      return userFromToken;
    } catch (JwtException | ClassCastException e) {
      throw new UserNotFoundException("Could not parse token");
    }
  }

  /**
   * Generate user token. subject - userEmail. role - user`s authorities.
   *
   * @param userDetails provided userDetails
   * @return generated token.
   */
  public String generateToken(UserDetails userDetails) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("role", userDetails.getAuthorities());
    return doGenerateToken(claims, userDetails.getUsername());
  }

  /**
   * Generate user token. subject - userEmail. claims - user`s role.
   *
   * @param claims  addition info about subject.
   * @param subject token`s subject.
   */
  private String doGenerateToken(Map<String, Object> claims, String subject) {
    return Jwts.builder().setClaims(claims).setSubject(subject)
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
        .signWith(Keys.hmacShaKeyFor(secret.getBytes()))
        .compact();
  }

  /**
   * Checks whether token is valid by comparing email from userDetails and email retrieved from
   * token.
   *
   * @param token       needs to retrieve email.
   * @param userDetails needs to retrieve user email.
   */
  public Boolean validateToken(String token, UserDetails userDetails) {
    final String userEmail = getUserEmailFromToken(token);
    return (userEmail.equals(userDetails.getUsername()) && !isTokenExpired(token));
  }
}
