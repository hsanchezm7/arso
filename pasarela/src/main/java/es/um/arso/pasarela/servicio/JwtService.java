package es.um.arso.pasarela.servicio;

import io.jsonwebtoken.Claims;
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

    private final String secret;
    private final int expirationSeconds;

    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expirationSeconds}") int expirationSeconds) {
        if (secret == null || secret.trim().isEmpty()) {
            throw new IllegalArgumentException("jwt.secret obligatorio");
        }
        this.secret = secret;
        this.expirationSeconds = expirationSeconds;
    }

    public String generateToken(String subject, String roles) {
        Date caducidad = Date.from(Instant.now().plusSeconds(expirationSeconds));

        log.info("JWT generado para id={} roles={} expiraEnSegundos={}",
                subject, roles, expirationSeconds);

        return Jwts.builder()
                .setSubject(subject)
                .claim("roles", roles)
                .setExpiration(caducidad)
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    public Claims validateToken(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }
}
