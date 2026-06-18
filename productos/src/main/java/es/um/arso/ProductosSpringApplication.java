package es.um.arso;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@SpringBootApplication
@EnableJpaAuditing
@OpenAPIDefinition(
        info = @Info(
                title = "API del servicio Productos",
                description = "Documentación sobre los endpoints del microservicio de productos.",
                version = "1.0"
        )
)
public class ProductosSpringApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductosSpringApplication.class, args);
    }
}
