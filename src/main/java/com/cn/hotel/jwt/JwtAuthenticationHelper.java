package com.cn.hotel.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtAuthenticationHelper {

    private String secret = "iamalokandthisismysecretkeyforawtauthentocationiwantittobesolong";
    private static final long JWT_TOKEN_VALIDITY = 60*60;

    public String getUsernameFromToken(String token){
        Claims claims = getClaimFromToken(token);
        String username = claims.getSubject();
        return username;
    }

    public Claims getClaimFromToken(String token){
        Claims claims = Jwts.parser().setSigningKey(secret.getBytes())
                .build().parseClaimsJws(token).getBody();
        return claims;
    }

    public Boolean isTokenExpired(String token){
        Claims claims = getClaimFromToken(token);
        Date expiryDate = claims.getExpiration();
        return expiryDate.before(new Date());
    }

    public String generateToken(UserDetails userDetails) {
        Map<String,String> claims = new HashMap<>();
        return Jwts.builder().setClaims(claims).setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
                .signWith(new SecretKeySpec(secret.getBytes(),SignatureAlgorithm.HS512.getJcaName()),SignatureAlgorithm.HS512)
                .compact();

    }
}