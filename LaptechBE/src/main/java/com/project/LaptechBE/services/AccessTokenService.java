package com.project.LaptechBE.services;

import com.project.LaptechBE.models.User;
import com.project.LaptechBE.untils.Constants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class AccessTokenService {
    public String extractAccessTokenUserName(String token) {
        return extractAccessTokenClaim(token, Claims::getSubject);
    }

    private <T> T extractAccessTokenClaim(String token, Function<Claims,T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(getSignInAccessToken()).build().parseClaimsJws(token).getBody();
    }

    private Key getSignInAccessToken() {
        byte[] keyBytes = Decoders.BASE64.decode(Constants.ACCESS_TOKEN);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public boolean isAccessTokenValid(String token, UserDetails userDetails) {
        final String username = extractAccessTokenUserName(token);
        return (username.equals(userDetails.getUsername())) && !isAccessTokenExpired((token));
    }

    private boolean isAccessTokenExpired(String token) {
        return extractAccessTokenExpiration(token).before(new Date());
    }

    private Date extractAccessTokenExpiration(String token) {
        return extractAccessTokenClaim(token,Claims::getExpiration);
    }

    public String generateAccessToken(UserDetails userDetails) {
        Map<String,Object> extraClaims = new HashMap<>();
        if(userDetails instanceof User user) {
            extraClaims.put("id",user.get_id());
            extraClaims.put("isAdmin",user.getIsAdmin());
        }

        return generateAccessToken(extraClaims,userDetails);
    }

    public String generateAccessToken(Map<String,Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+Constants.EXPIRATION_TIME_ACCESSTOKEN))
                .signWith(getSignInAccessToken(), SignatureAlgorithm.HS256)
                .compact();
    }
}
