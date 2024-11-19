package com.badri.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.function.Function;
@Component
public class JwtService {
    private static final Key SECRET_KEY= Keys.secretKeyFor(SignatureAlgorithm.HS256);
    public String extractUsername(String token){
        return extractClaims(token,Claims::getSubject);
    }
    public String generateToken(UserDetails userDetails){
        return createToken(new HashMap<>(),userDetails);
    }
    private String createToken(HashMap<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 *60 *24))
                .signWith(SECRET_KEY,SignatureAlgorithm.HS256)
                .compact();
    }
    public <T> T extractClaimByKey(String token, String key, Class<T> clazz) {
        return extractClaims(token, claims -> claims.get(key, clazz));
    }
    private <T> T extractClaims(String token, Function<Claims,T> claimResolver){
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }
    public Claims extractAllClaims(String token){
        return Jwts
                .parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    public boolean isValidToken(String token, UserDetails userDetails){
        String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && isTokenExpired(token));
    }
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
    public Date extractExpiration(String token) {
        return extractClaims(token, Claims::getExpiration);
    }

}
