package es.um.arso.compraventa.adaptadores.out;

import es.um.arso.compraventa.client.ProductosRestClient;
import es.um.arso.compraventa.servicio.puertos.out.IServicioProductosExterno;
import es.um.arso.compraventa.servicio.puertos.out.ProductoInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Service
public class ServicioProductosAdapter implements IServicioProductosExterno {

    private ProductosRestClient client;

    public ServicioProductosAdapter(@Value("${servicios.productos.url}") String baseUrl) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        this.client = retrofit.create(ProductosRestClient.class);
    }

    @Override
    public ProductoInfo getProducto(String idProducto) throws Exception {
        Response<ProductoInfo> response = client.getProducto(idProducto).execute();

        if (!response.isSuccessful()) {
            throw new RuntimeException("Error al obtener producto: " + response.code() + " - " + response.message());
        }

        return response.body();
    }
}
