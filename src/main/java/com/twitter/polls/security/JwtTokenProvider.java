package com.twitter.polls.security;

import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;

@Component
public class JwtTokenProvider {



    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    @Value("${app.jwtSecret}")
    private String jwtSecret;

    @Value("${app.jwtExpirationInMs}")
    private Long jwtExpirationInMs;

    /**
     * This method accepts authentication of which is the returned
     * value from a successful authentication using the AuthenticationManager.
     *
     * @param authentication
     * @return JWT token
     */
    public String generateToken(Authentication authentication){
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        //Here is where you build the Jwt toke8in
//        System.out.println("I got here");
        return Jwts.builder()
                .setSubject(userPrincipal.getId().toString())
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }
    public Long getUserIdFromJwt(String token){
        Claims claims = Jwts
                .parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();
         return Long.valueOf(claims.getSubject());
    }

    public boolean validateToken(String authToken){

        try{
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        }catch (SignatureException e){
            logger.error("Invalid JWT signature");
        }catch(MalformedJwtException e){
            logger.error("Invalid JWT token");
        } catch (ExpiredJwtException  e){
            logger.error("Expired JWT token");
        } catch (UnsupportedJwtException e){
            logger.error("Unsupported JWT token");
        } catch (IllegalArgumentException e){
            logger.error("JWT claims string is empty.");
        }
        return false;
    }
}
