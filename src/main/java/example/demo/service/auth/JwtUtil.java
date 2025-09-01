package example.demo.service.auth;

import example.demo.util.Constants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class JwtUtil {
    private static final String SECRET_KEY = "gbfddbhghrethy485tgergbvdjfbgjeyty34t5yreughfdbjgdfthy4835398wtghdsfgsfls";


    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public List<? extends GrantedAuthority> extractRoles(String token) {
        List<?> rawRoleList = extractClaim(token, (Claims claims) -> claims.get(Constants.AUTHORITIES, List.class));
        // Cast each element to GrantedAuthority and collect to a new list
        return rawRoleList.stream()
                .map(String.class::cast)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        //return extractClaim(token, (Claims claims) -> claims.get(AUTHORITIES, List.class));
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private SecretKey secretKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    private Claims extractAllClaims(String token) {
        JwtParser parser = Jwts.parser()
                .verifyWith(secretKey())
                .build();
        return (Claims) parser
                .parse(token)
                .getPayload();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String generateToken(String username, List<String> roleList, Map<String, Object> claims) {
        return Jwts.builder()
                //.claim(Constants.AUTHORITIES, roleList)
                .claims(claims)
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(Date.from(new Date().toInstant().plus(Constants.TOKEN_EXPIRATION_TIME_IN_DAYS, ChronoUnit.DAYS)))
                //.signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .signWith(secretKey())
                .compact();
    }

    public Boolean validateToken(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return extractedUsername.equals(username) && !isTokenExpired(token);
    }
}
