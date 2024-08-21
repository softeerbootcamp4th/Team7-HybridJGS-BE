package JGS.CasperEvent.global.jwt.util;

import JGS.CasperEvent.global.jwt.dto.Jwt;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtProvider {

    @Value("${spring.jwt.secretKey}")
    private static String jwtSecretKey;
    protected static final byte[] secret = jwtSecretKey.getBytes();
    private final Key key = Keys.hmacShaKeyFor(secret);

    public Jwt createJwt(Map<String, Object> claims) {
        String accessToken = createToken(claims, getExpireDateAccessToken());
        return Jwt.builder()
                .accessToken(accessToken)
                .build();
    }

    private String createToken(Map<String, Object> claims, Date expireDate) {
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(expireDate)
                .signWith(key)
                .compact();
    }

    public Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }


    private Date getExpireDateAccessToken() {
        long expireTimeMils = 1000L * 60 * 60  * 24 * 365;
        return new Date(System.currentTimeMillis() + expireTimeMils);
    }

}
