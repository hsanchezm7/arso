package es.um.arso.usuarios.rest.auth;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.impl.DefaultClaims;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public final class JwtUtils {

    private static final Gson GSON = new Gson();
    private static final Type MAP_TYPE = new TypeToken<Map<String, Object>>() {}.getType();

    private JwtUtils() {}

    public static Claims parseClaimsUnverified(String token) {
        if (token == null || token.trim().isEmpty()) {
            throw new IllegalArgumentException("Token JWT vacio");
        }

        String[] parts = token.split("\\.");
        if (parts.length < 2) {
            throw new IllegalArgumentException("Token JWT invalido");
        }

        String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
        Map<String, Object> claimsMap = GSON.fromJson(payloadJson, MAP_TYPE);
        if (claimsMap == null) {
            claimsMap = new HashMap<>();
        }

        // Solo lectura del payload; la validez del token se controla en la pasarela.
        return new DefaultClaims(claimsMap);
    }
}
