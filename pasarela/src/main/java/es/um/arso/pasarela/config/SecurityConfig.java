package es.um.arso.pasarela.config;

import es.um.arso.pasarela.adaptadores.in.filtros.JwtRequestFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);

    private final SecuritySuccessHandler successHandler;
    private final JwtRequestFilter jwtRequestFilter;

    public SecurityConfig(SecuritySuccessHandler successHandler, JwtRequestFilter jwtRequestFilter) {
        this.successHandler = successHandler;
        this.jwtRequestFilter = jwtRequestFilter;
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        log.info("Configuring Spring Security OAuth2 login for GitHub");
        httpSecurity
                .csrf()
                .disable()
                .httpBasic()
                .disable()
                .authorizeRequests()
                // Rutas públicas: autenticación, OAuth, login
                .antMatchers("/auth/**", "/oauth2/**", "/login/**")
                .permitAll()
                // Crear usuario (POST /usuarios) - público
                .antMatchers("POST", "/usuarios")
                .permitAll()
                // OAuth (POST /usuarios/oauth) - público
                .antMatchers("POST", "/usuarios/oauth")
                .permitAll()
                // Buscar usuario (GET /usuarios/buscar) - público
                .antMatchers("GET", "/usuarios/buscar")
                .permitAll()
                // Llenar nombre usuario (GET /usuarios/{id}/nombre) - público
                .antMatchers("GET", "/usuarios/*/nombre")
                .permitAll()
                // Verificar credenciales (POST /usuarios/verificar) - público
                .antMatchers("POST", "/usuarios/verificar")
                .permitAll()
                // Resto de /usuarios requiere autenticación
                .antMatchers("/usuarios/**")
                .authenticated()
                // /productos delega autorización en el microservicio productos
                .antMatchers("/productos/**")
                .permitAll()
                // /compraventa requiere autenticación
                .antMatchers("/compraventa/**")
                .permitAll()
                // /valoraciones es público
                .antMatchers("/valoraciones/**")
                .permitAll()
                // Todo lo demás permitido
                .anyRequest()
                .permitAll()
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                .and()
                .oauth2Login()
                .successHandler(this.successHandler)
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        httpSecurity.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
