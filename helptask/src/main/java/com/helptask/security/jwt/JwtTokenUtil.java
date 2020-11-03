package com.helptask.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
@Component
public class JwtTokenUtil implements Serializable {

    private static String CLEAN_KEY_USERNAME= "sub";
    private static String CLEAN_KEY_CREATE= "created";
    private static String CLEAN_KEY_EXPIRED= "exp";

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    public String getUserNameFromToken(String token){
        String username;
        try{
            final Claims claims = getClaimsFromToken(token);
            username = claims.getSubject();
        }catch (Exception e){
            username = null;
        }
        return username;
    }

    public Date getExpirationDataFromToken(String token){
        Date expiration;
        try{
            final Claims claims = getClaimsFromToken(token);
            expiration = claims.getExpiration();
        }catch (Exception e){
            expiration = null;
        }
        return expiration;
    }

    private Claims getClaimsFromToken(String token){
        Claims claims;
        try{
            claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
        }catch (Exception e){
            claims = null;
        }
        return claims;
    }

    private Boolean isTokenExpired(String token){
        final Date expiration = getExpirationDataFromToken(token);
        return expiration.before(new Date());
    }

    public String generateToken(UserDetails userDetails){
        Map<String, Object> claims = new HashMap<>();
        claims.put(CLEAN_KEY_USERNAME, userDetails.getUsername());

        final Date createdData = new Date();
        claims.put(CLEAN_KEY_CREATE,createdData);

        return doGeneratedToken(claims);
    }
    private String doGeneratedToken(Map<String, Object> claims){
       final Date createdDate = (Date) claims.get(CLEAN_KEY_CREATE);
       final Date expirationDate = new Date(createdDate.getTime() + expiration);
       return Jwts.builder()
               .setClaims(claims)
               .setExpiration(expirationDate)
               .signWith(SignatureAlgorithm.HS512, secret)
               .compact();
    }

    public Boolean canTokenBeRefreshed(String token){
        return(!isTokenExpired(token));
    }

    public String refreshToken(String token){
        String refreshedToken;
        try{
            final Claims claims = getClaimsFromToken(token);
            claims.put(CLEAN_KEY_CREATE, new Date());
            refreshedToken = doGeneratedToken(claims);
        }catch (Exception e){
                refreshedToken = null;
        }
        return refreshedToken;
    }

    public Boolean validateToken(String token, UserDetails userDetails){
        JwtUser user = (JwtUser)userDetails;
        final String username = getUserNameFromToken(token);
        return(username.equals(user.getUsername()) && (!isTokenExpired(token)));
    }

}
