package es.um.arso.compraventa.config;

import io.jsonwebtoken.Claims;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtRequestFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            String token = extraerBearer(request);
            if (token != null && !token.isEmpty()) {
                try {
                    Claims claims = jwtService.validateToken(token);
                    String subject = claims.getSubject();
                    if (subject != null) {
                        List<SimpleGrantedAuthority> authorities = parseRoles(claims.get("roles", String.class));
                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(subject, null, authorities);
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                } catch (Exception ex) {
                    SecurityContextHolder.clearContext();
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    response.setContentType("application/json");
                    response.getWriter().write("{\"error\":\"Token invalido o caducado\"}");
                    return;
                }
            }
        }

        filterChain.doFilter(request, response);
    }

    private String extraerBearer(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || authHeader.isEmpty()) {
            return null;
        }

        if (!authHeader.startsWith("Bearer ")) {
            return null;
        }

        return authHeader.substring("Bearer ".length());
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
