package es.um.arso.usuarios.rest.auth;

import io.jsonwebtoken.Claims;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("java:S106")
public class PruebasAuth {

    public static void main(String[] args) {
        // Generacion de un token de prueba con los mismos claims que usa login.
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", "usuario-demo-id");
        claims.put("roles", "USUARIO");

        String token = JwtUtils.generateToken(claims);
        System.out.println("Token generado:");
        System.out.println(token);

        // Validacion del token para comprobar firma y extraccion de claims.
        Claims tokenClaims = JwtUtils.validateToken(token);
        System.out.println("subject=" + tokenClaims.getSubject());
        System.out.println("roles=" + tokenClaims.get("roles", String.class));
    }
}
