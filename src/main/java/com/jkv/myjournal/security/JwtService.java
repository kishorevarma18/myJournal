package com.jkv.myjournal.security;

//import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtService {

    @Value("${myjournal.app.secretkey}")
    private String jwtSecret;
    private SecretKey signinKey; // we can use Key for publickey(Only verify token, Asymentric), PrivateKey(Only sign token, Asymentric) and use SecretKey for secretkey(both verify and sign the token).

    @PostConstruct
    public void init(){
        byte[] keyBytes = Base64.getDecoder().decode(jwtSecret);
        this.signinKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateJwtToken(Authentication authentication){
        
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        Map<String, Object> claims = new HashMap<>();
        claims.put("userId",userPrincipal.getId().toString());

        return Jwts.builder()
            .claims(claims)
            .subject(userPrincipal.getUsername())
            .issuedAt(new Date(System.currentTimeMillis()))
            .expiration(new Date(System.currentTimeMillis() + (1000 * 60 * 10)))
            .signWith(signinKey)
            .compact();
    }

    public boolean validateJwt(String jwtToken) {
        try{
            Jwts.parser()
                .verifyWith(signinKey)
                .build()
                .parseSignedClaims(jwtToken);
            return true;
        }
        catch (SecurityException e) {
            log.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }

    public String getUserNameFromJwt(String jwtToken) {
        return Jwts.parser()
            .verifyWith(signinKey)
            .build()
            .parseSignedClaims(jwtToken)
            .getPayload()
            .getSubject();
    }
}
