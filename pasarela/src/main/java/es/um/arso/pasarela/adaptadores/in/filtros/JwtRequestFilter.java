package es.um.arso.pasarela.adaptadores.in.filtros;

import es.um.arso.pasarela.servicio.JwtService;
import io.jsonwebtoken.Claims;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtRequestFilter.class);

    private final JwtService jwtService;

    @Value("${jwt.cookie.name:jwt}")
    private String cookieName;

    public JwtRequestFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if (esRutaAuth(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }

        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = extraerCookie(request);

        if (token == null || token.isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            Claims claims = jwtService.validateToken(token);
            String subject = claims.getSubject();

            if (subject != null) {
                List<SimpleGrantedAuthority> authorities = parseRoles(claims.get("roles", String.class));
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(subject, null, authorities);
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.info("JWT valido para subject={}", subject);
            }

            filterChain.doFilter(request, response);
        } catch (Exception ex) {
            SecurityContextHolder.clearContext();
            log.warn("JWT invalido o caducado");
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Token invalido o caducado\"}");
        }
    }

    private boolean esRutaAuth(String uri) {
        if (uri == null) {
            return false;
        }
        return uri.startsWith("/auth/") || uri.startsWith("/oauth2/") || uri.startsWith("/login/oauth2");
    }

    private String extraerCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }

        for (Cookie cookie : cookies) {
            if (cookieName.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }

        return null;
    }

    private List<SimpleGrantedAuthority> parseRoles(String roles) {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        if (roles == null || roles.trim().isEmpty()) {
            return authorities;
        }

        String[] tokens = roles.split("[, ]+");
        for (String token : tokens) {
            String role = token.trim();
            if (!role.isEmpty()) {
                authorities.add(new SimpleGrantedAuthority(role));
            }
        }

        return authorities;
    }
}
