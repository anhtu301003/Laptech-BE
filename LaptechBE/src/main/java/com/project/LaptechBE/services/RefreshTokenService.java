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
public class RefreshTokenService {
    public String extractRefreshTokenUserName(String token) {
        return extractRefreshTokenClaim(token, Claims::getSubject);
    }

    private <T> T extractRefreshTokenClaim(String token, Function<Claims,T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(getSignInRefreshToken()).build().parseClaimsJws(token).getBody();
    }

    private Key getSignInRefreshToken() {
        byte[] keyBytes = Decoders.BASE64.decode(Constants.ACCESS_TOKEN);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public boolean isRefreshTokenValid(String token, UserDetails userDetails) {
        final String username = extractRefreshTokenUserName(token);
        return (username.equals(userDetails.getUsername())) && !isRefreshTokenExpired((token));
    }

    private boolean isRefreshTokenExpired(String token) {
        return extractRefreshTokenExpiration(token).before(new Date());
    }

    private Date extractRefreshTokenExpiration(String token) {
        return extractRefreshTokenClaim(token,Claims::getExpiration);
    }

    public String generateRefreshToken(UserDetails userDetails) {
        Map<String,Object> extraClaims = new HashMap<>();
        if(userDetails instanceof User user) {
            extraClaims.put("id",user.getId());
            extraClaims.put("isAdmin",user.getIsAdmin());
        }

        return generateRefreshToken(extraClaims,userDetails);
    }

    public String generateRefreshToken(Map<String,Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+Constants.EXPIRATION_TIME_REFRESHTOKEN))
                .signWith(getSignInRefreshToken(), SignatureAlgorithm.HS256)
                .compact();
    }
}
