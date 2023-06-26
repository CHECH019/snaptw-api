package com.xdev.snaptw.security.jwt;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import static com.xdev.snaptw.util.Const.TOKEN_VALIDITY;
import static com.xdev.snaptw.util.Const.SIGNING_KEY;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JwtService {

    public Optional<String> extractUsername(String jwt){
        return extractClaim(jwt, Claims::getSubject);
    }

    public boolean isJwtValid(String jwt, UserDetails userDetails){
        String subject = extractUsername(jwt).orElseThrow(()-> new JwtException(
            "Token provided has no subject"));
        String username = userDetails.getUsername();
        return subject.equals(username) && !isJwtExpired(jwt);
    }

    public boolean isJwtExpired(String jwt){
        Date expiration = extractClaim(jwt, Claims::getExpiration)
                .orElseThrow(() -> new JwtException(
                    "Token provived has no expiration date"));
        return expiration.before(new Date());
    }

    public Optional<String> extractCustomClaim(String jwt, String claim){
        return extractClaim(jwt, c -> c.get(claim,String.class));
    }

    public <T> Optional<T> extractClaim(String jwt, Function<Claims,T> claimsResolver){
        Claims claims = extractClaims(jwt);
        return Optional.ofNullable(claimsResolver.apply(claims));
    }
    
    public Claims extractClaims(String jwt){
        return Jwts
                .parserBuilder()
                .setSigningKey(signingKey())
                .build()
                .parseClaimsJws(jwt)
                .getBody();
    }

    public String generateToken(UserDetails userDetails){
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(Map<String,Object> claims, UserDetails userDetails){
        return Jwts
                .builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis()+TOKEN_VALIDITY))
                .signWith(signingKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public Key signingKey(){
        byte[] keyBytes = Decoders.BASE64.decode(SIGNING_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }


}
