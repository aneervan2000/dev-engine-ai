package com.anee.projects.lovable_clone.security;

import com.anee.projects.lovable_clone.entities.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;

@Component
public class AuthUtil {

    @Value("${jwt.secret-key}")
    private String jwtSecretKey;

    // Converts the JWT secret key string to a SecretKey object using hmacShaKeyFor method
    private SecretKey getSecretKey() {
        // Implementation to convert jwtSecretKey string to SecretKey
        return Keys.hmacShaKeyFor(jwtSecretKey.getBytes(StandardCharsets.UTF_8));
    }

    /** Generates a JWT access token for the given user.
     *<br>
     * with subject as username and userId as a claim.
     * The token is valid for 10 minutes from the time of issuance.
     *
     * @param user the user for whom the token is generated
     * @return a JWT access token as a String
     */
    public String generateAccessToken(User user) {
        return Jwts.builder()
                .subject(user.getUsername())
                .claim("userId", user.getId().toString())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 10)) // Token valid for 10 minutes
                .signWith(getSecretKey())
                .compact();
    }

    /** Verifies the given JWT access token and extracts the user details.
     *<br>
     *
     * If the token is valid, it returns a JwtUserPrincipal containing userId and username.
     * Otherwise, it throws an exception.
     * @param token the JWT access token to verify
     * @return JwtUserPrincipal containing userId and username extracted from the token
     */
    public JwtUserPrincipal verifyAccessToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        Long userId = Long.parseLong(claims.get("userId", String.class));
        String username = claims.getSubject();

        return new JwtUserPrincipal(userId, username, new ArrayList<>());
    }

    public Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null || !(authentication.getPrincipal() instanceof JwtUserPrincipal)) {
            throw new AuthenticationCredentialsNotFoundException("No JWT Found");
        }
        return ((JwtUserPrincipal) authentication.getPrincipal()).userId();
    }
}
