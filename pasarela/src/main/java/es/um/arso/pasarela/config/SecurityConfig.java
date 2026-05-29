package es.um.arso.pasarela.config;

import es.um.arso.pasarela.adaptadores.in.filtros.JwtRequestFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
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
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .httpBasic(basic -> basic.disable())
                .authorizeRequests(auth -> auth
                        .antMatchers("/auth/**", "/oauth2/**", "/login/**").permitAll()
                        .antMatchers("POST", "/usuarios").permitAll()
                        .antMatchers("POST", "/usuarios/oauth").permitAll()
                        .antMatchers("GET", "/usuarios/buscar").permitAll()
                        .antMatchers("GET", "/usuarios/*/nombre").permitAll()
                        .antMatchers("POST", "/usuarios/verificar").permitAll()
                        .antMatchers("/usuarios/**").authenticated()
                        .antMatchers("/productos/**").permitAll()
                        .antMatchers("/compraventa/**").permitAll()
                        .antMatchers("/valoraciones/**").permitAll()
                        .anyRequest().permitAll()
                )
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                )
                .oauth2Login(oauth2 -> oauth2
                        .successHandler(this.successHandler)
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

        httpSecurity.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
