package es.um.arso.pasarela.config;

import org.springframework.context.annotation.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import es.um.arso.pasarela.adaptadores.in.filtros.JwtRequestFilter;

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
        httpSecurity.csrf().disable().httpBasic().disable()
                .authorizeRequests()
                .antMatchers("/auth/**", "/oauth2/**", "/login/**").permitAll()
                .antMatchers("/usuarios/**", "/productos/**", "/compraventa/**").authenticated()
                .anyRequest().permitAll()
                .and()
                .oauth2Login().successHandler(this.successHandler)
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        httpSecurity.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
