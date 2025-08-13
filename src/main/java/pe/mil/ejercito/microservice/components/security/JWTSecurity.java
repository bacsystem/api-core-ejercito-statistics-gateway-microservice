package pe.mil.ejercito.microservice.components.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;

/**
 * JWTSecurity
 * <p>
 * JWTSecurity class.
 * <p>
 * THIS COMPONENT WAS BUILT ACCORDING TO THE DEVELOPMENT STANDARDS
 * AND THE BXCODE APPLICATION DEVELOPMENT PROCEDURE AND IS PROTECTED
 * BY THE LAWS OF INTELLECTUAL PROPERTY AND COPYRIGHT...
 *
 * @author bxcode
 * @author bacsystem.sac@gmail.com
 * @since 23/03/2024
 */

@Service
public class JWTSecurity {

    @Value("${microservice.secret-key}")
    private String secretKey;
    @Value("${microservice.authorities-value}")
    private String authorities;


    private SecretKey getKey(String secret) {
        byte[] secretBytes = Base64.getEncoder().encode(secret.getBytes());
        return Keys.hmacShaKeyFor(secretBytes);

    }

    public String generate(final UserDetails userDetails) {
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(Date.from(Instant.now()))
                .claim(authorities, userDetails.getAuthorities())
                .expiration(Date.from(Instant.now().plus(59, ChronoUnit.SECONDS)))
                .signWith(getKey(secretKey))
                .compact();
    }

    public String generate(final String username) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plus(59, ChronoUnit.DAYS)))
                .signWith(getKey(secretKey))
                .compact();
    }

    public String getUsername(final String token) {
        return getClaims(token).getSubject();
    }

    public Date getExpiration(final String token) {
        return getClaims(token).getExpiration();
    }

    public Claims getClaims(final String token) {
        return Jwts.parser()
                .verifyWith(getKey(secretKey))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean validate(final String token) {
        return getExpiration(token)
                .after(Date.from(Instant.now()));
    }

    public boolean validate(final UserDetails user, final String token) {
        return getExpiration(token)
                .after(Date.from(Instant.now()))
                && user.getUsername().equals(getUsername(token));
    }
}


