package spring.jwtsecurity.jwt;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import spring.jwtsecurity.entity.MainUser;

/**
 * Check if the token has correct structure,
 * is not expired ...
 */
@Component
public class JwtProvider {

    private final static Logger logger = LoggerFactory.getLogger(JwtProvider.class);

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private int expiration;

    public String generateToken(Authentication authentication) {
        MainUser mainUser = (MainUser) authentication.getPrincipal();
        String token = Jwts.builder().setSubject(mainUser.getUsername())
                                     .setIssuedAt(new Date())
                                     .setExpiration(new Date(new Date().getTime() + expiration*1000))
                                     .signWith(SignatureAlgorithm.HS512, secret)
                                     .compact();
        return token;
    }

    public String getUsernameFromToken(String token) {
        String username = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody().getSubject();
        return username;
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
            return true;
        }catch (MalformedJwtException e) {
            logger.error("Mal formed token "+ e);
        }catch (UnsupportedJwtException e) {
            logger.error("Unsupported token "+ e);
        }catch (ExpiredJwtException e) {
            logger.error("Expired token "+ e);
        }catch (IllegalArgumentException e) {
            logger.error("Empty token token "+ e);
        }catch (SignatureException e) {
            logger.error("Fail signing "+ e);
        }catch (Exception e) {
            logger.error("General exception "+ e);
        }
        return false;
    }

}
