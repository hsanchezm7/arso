package es.um.arso.pasarela.servicio;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.time.Instant;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    private static final Logger log = LoggerFactory.getLogger(JwtService.class);

    private static final String TOKEN_TYPE_ACCESS = "access";
    private static final String TOKEN_TYPE_REFRESH = "refresh";

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access.expirationSeconds:15}")
    private int accessExpirationSeconds;

    @Value("${jwt.refresh.expirationSeconds:2592000}")
    private int refreshExpirationSeconds;

    public String generateAccessToken(String subject, String roles) {
        return generateToken(subject, TOKEN_TYPE_ACCESS, roles, accessExpirationSeconds);
    }

    public String generateRefreshToken(String subject) {
        return generateToken(subject, TOKEN_TYPE_REFRESH, null, refreshExpirationSeconds);
    }

    private String generateToken(String subject, String type, String roles, int expirationSeconds) {
        Date expiration = Date.from(Instant.now().plusSeconds(expirationSeconds));

        if (TOKEN_TYPE_ACCESS.equals(type))
            log.info("ACCESS TOKEN generado para user={} roles={} expiraEn={}s", subject, roles, expirationSeconds);
        else log.info("REFRESH TOKEN generado para user={} expiraEn={}s", subject, expirationSeconds);

        JwtBuilder builder = Jwts.builder()
                .setSubject(subject)
                .claim("type", type)
                .setIssuedAt(new Date())
                .setExpiration(expiration);

        if (roles != null) {
            builder.claim("roles", roles);
        }

        return builder.signWith(SignatureAlgorithm.HS256, secret).compact();
    }

    // TODO: comprobar JwtException
    public Claims validateToken(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

    public boolean isAccessToken(Claims claims) {
        return TOKEN_TYPE_ACCESS.equals(claims.get("type"));
    }

    public boolean isRefreshToken(Claims claims) {
        return TOKEN_TYPE_REFRESH.equals(claims.get("type"));
    }
}
