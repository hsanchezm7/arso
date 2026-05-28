package es.um.arso.compraventa.config;

import es.um.arso.compraventa.client.ProductosRestClient;
import es.um.arso.compraventa.client.UsuariosRestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Configuration
public class RetrofitConfig {

    @Bean
    UsuariosRestClient usuariosRestClient(@Value("${servicios.usuarios.url}") String baseUrl) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(UsuariosRestClient.class);
    }

    @Bean
    ProductosRestClient productosRestClient(@Value("${servicios.productos.url}") String baseUrl) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(ProductosRestClient.class);
    }
}
