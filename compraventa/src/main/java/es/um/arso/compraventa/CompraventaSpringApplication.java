package es.um.arso.compraventa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@SpringBootApplication
@EnableMongoAuditing
@OpenAPIDefinition(
        info = @Info(
                title = "API del servicio Compraventa",
                description = "Documentación sobre los endpoints del microservicio de compraventa.",
                version = "1.0"
        )
)
public class CompraventaSpringApplication {

    public static void main(String[] args) {
        SpringApplication.run(CompraventaSpringApplication.class, args);
    }
}
