package es.um.arso.pasarela;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

@SpringBootApplication
@EnableZuulProxy
public class PasarelaSpringApplication {

    public static void main(String[] args) {
        SpringApplication.run(PasarelaSpringApplication.class, args);
    }
}
