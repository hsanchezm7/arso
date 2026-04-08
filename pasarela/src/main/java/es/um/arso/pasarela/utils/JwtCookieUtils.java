package es.um.arso.pasarela.utils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtCookieUtils {

    @Value("${jwt.cookie.name:jwt}")
    private String cookieName;

    @Value("${jwt.cookie.maxAge:3600}")
    private int cookieMaxAge;

    @Value("${jwt.cookie.path:/}")
    private String cookiePath;

    @Value("${jwt.cookie.secure:false}")
    private boolean cookieSecure;

    public void addJwtCookie(HttpServletResponse response, String jwt) {
        Cookie cookie = new Cookie(cookieName, jwt);
        cookie.setHttpOnly(true);
        cookie.setPath(cookiePath);
        cookie.setSecure(cookieSecure);
        cookie.setMaxAge(cookieMaxAge);
        response.addCookie(cookie);
    }

    public void clearJwtCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie(cookieName, "");
        cookie.setHttpOnly(true);
        cookie.setPath(cookiePath);
        cookie.setSecure(cookieSecure);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}
