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
public class RefreshTokenService {

    private final UserRepository userRepository;

    public Map<String, Object> extractRefreshTokenDetails(String token) {
        Claims claims = extractAllClaims(token);

        Map<String,Object> tokenDetails = new HashMap<>();
        tokenDetails.put("id",claims.get("id",String.class));
        tokenDetails.put("isAdmin",claims.get("isAdmin",Boolean.class));

        return tokenDetails;
    }

    private <T> T extractRefreshTokenClaim(String token, Function<Claims,T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInRefreshToken())
                .build().parseClaimsJws(token)
                .getBody();
    }

    public String extractRefreshTokenUserName(String token) {
        Claims claims = extractAllClaims(token);
        String userId = claims.get("id",String.class);
        ObjectId objectId = new ObjectId(userId);
        Optional<User> user = userRepository.findById(objectId);
        System.out.println(user);
        return user.get().getEmail();
    }

    public boolean isRefreshTokenValid(String token, UserDetails userDetails) {
        Claims claims = extractAllClaims(token);
        User user = userRepository.getById(claims.get("id",String.class));

        if (user == null || !user.getEmail().equals(userDetails.getUsername())) {
            throw new SignatureException("Token is invalid or user does not exist");
        }

        if (isRefreshTokenExpired(token)) {
            throw new SignatureException("Token has expired");
        }

        return true;
    }

    private boolean isRefreshTokenExpired(String token) {
        return extractRefreshTokenExpiration(token).before(new Date());
    }

    private Date extractRefreshTokenExpiration(String token) {
        return extractRefreshTokenClaim(token,Claims::getExpiration);
    }

    private Key getSignInRefreshToken() {
        byte[] keyBytes = Decoders.BASE64.decode(Constants.REFRESH_TOKEN);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateRefreshToken(UserDetails userDetails) {
        Map<String,Object> extraClaims = new HashMap<>();
        if(userDetails instanceof User user) {
            extraClaims.put("id",user.getId().toString());
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

    public String refreshAccessToken(String refreshToken) {
        return null;
    }
}
