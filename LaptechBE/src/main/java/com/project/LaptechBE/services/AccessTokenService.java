package com.project.LaptechBE.services;

import com.project.LaptechBE.models.User;
import com.project.LaptechBE.repositories.UserRepository;
import com.project.LaptechBE.untils.Constants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class AccessTokenService {

    private final UserRepository userRepository;

    public Map<String, Object> extractAccessTokenDetails(String token) {
        Claims claims = extractAllClaims(token);

        Map<String,Object> tokenDetails = new HashMap<>();
        tokenDetails.put("id",claims.get("id",String.class));
        tokenDetails.put("isAdmin",claims.get("isAdmin",Boolean.class));

        return tokenDetails;
    }

    private <T> T extractAccessTokenClaim(String token, Function<Claims,T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInAccessToken())
                .build().parseClaimsJws(token)
                .getBody();
    }

    public String extractAccessTokenUserName(String token) {
        Claims claims = extractAllClaims(token);
        String userId = claims.get("id",String.class);
        ObjectId objectId = new ObjectId(userId);
        Optional<User> user = userRepository.findById(objectId);
        System.out.println(user);
        return user.get().getEmail();
    }

    public boolean isAccessTokenValid(String token, UserDetails userDetails) {
        Claims claims = extractAllClaims(token);
        User user = userRepository.getById(claims.get("id",String.class));

        if (user == null || !user.getEmail().equals(userDetails.getUsername())) {
            throw new SignatureException("Token is invalid or user does not exist");
        }

        if (isAccessTokenExpired(token)) {
            throw new SignatureException("Token has expired");
        }

        return true;
    }

    private boolean isAccessTokenExpired(String token) {
        return extractAccessTokenExpiration(token).before(new Date());
    }

    private Date extractAccessTokenExpiration(String token) {
        return extractAccessTokenClaim(token,Claims::getExpiration);
    }

    private Key getSignInAccessToken() {
        byte[] keyBytes = Decoders.BASE64.decode(Constants.ACCESS_TOKEN);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateAccessToken(UserDetails userDetails) {
        Map<String,Object> extraClaims = new HashMap<>();
        if(userDetails instanceof User user) {
            extraClaims.put("id",user.getId().toString());
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
