package es.um.arso.pasarela.utils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtils {

    @Value("${jwt.refresh.cookie.name:refreshToken}")
    private String refreshCookieName;

    @Value("${jwt.refresh.cookie.maxAge:2592000}")
    private int refreshCookieMaxAge;

    @Value("${jwt.refresh.cookie.path:/auth/refresh}")
    private String refreshCookiePath;

    @Value("${jwt.refresh.cookie.secure:true}")
    private boolean cookieSecure;

    public void addRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        Cookie cookie = new Cookie(refreshCookieName, refreshToken);

        cookie.setHttpOnly(true);
        cookie.setSecure(cookieSecure);
        cookie.setPath(refreshCookiePath);
        cookie.setMaxAge(refreshCookieMaxAge);
        // TODO: estaría bien añadir Same-site

        response.addCookie(cookie);
    }

    public void clearRefreshTokenCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie(refreshCookieName, "");

        cookie.setHttpOnly(true);
        cookie.setSecure(cookieSecure);
        cookie.setPath(refreshCookiePath);
        cookie.setMaxAge(0);

        response.addCookie(cookie);
    }

    public String getRefreshCookieName() {
        return refreshCookieName;
    }

    public int getRefreshCookieMaxAge() {
        return refreshCookieMaxAge;
    }

    public String getRefreshCookiePath() {
        return refreshCookiePath;
    }

    public boolean isCookieSecure() {
        return cookieSecure;
    }
}
