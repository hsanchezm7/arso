package es.um.arso.usuarios.rest.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

public final class JwtUtils {

    private static final String SECRETO = "secreto";
    private static final long TIEMPO_SEGUNDOS = 3600;

    private JwtUtils() {}

    public static String generateToken(Map<String, Object> claims) {
        Date caducidad = Date.from(Instant.now().plusSeconds(TIEMPO_SEGUNDOS));
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(caducidad)
                .signWith(SignatureAlgorithm.HS256, SECRETO)
                .compact();
    }

    public static Claims validateToken(String token) {
        return Jwts.parser().setSigningKey(SECRETO).parseClaimsJws(token).getBody();
    }
}
