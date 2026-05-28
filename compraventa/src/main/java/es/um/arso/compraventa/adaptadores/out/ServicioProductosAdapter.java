package es.um.arso.compraventa.adaptadores.out;

import es.um.arso.compraventa.client.ProductosRestClient;
import es.um.arso.compraventa.repositorio.EntidadNoEncontrada;
import es.um.arso.compraventa.servicio.exception.ServicioExternoException;
import es.um.arso.compraventa.servicio.puertos.out.IServicioProductosExterno;
import es.um.arso.compraventa.servicio.puertos.out.ProductoInfo;
import java.io.IOException;
import org.springframework.stereotype.Service;
import retrofit2.Response;

@Service
public class ServicioProductosAdapter implements IServicioProductosExterno {

    private final ProductosRestClient client;

    public ServicioProductosAdapter(ProductosRestClient client) {
        this.client = client;
    }

    @Override
    public ProductoInfo getProducto(String idProducto) throws EntidadNoEncontrada, ServicioExternoException {
        try {
            Response<ProductoInfo> response = client.getProducto(idProducto).execute();

            if (!response.isSuccessful()) handleError(response);

            return response.body();
        } catch (IOException e) {
            throw new ServicioExternoException("Error al comunicar con el servicio de productos: " + e.getMessage(), e);
        }
    }

    private void handleError(Response<?> response) throws EntidadNoEncontrada, ServicioExternoException {
        int code = response.code();

        switch (code) {
            case 404:
                throw new EntidadNoEncontrada("Producto no existe");
            default:
                throw new ServicioExternoException(
                        "Error con el servicio de productos. HTTP " + code + " - " + response.message());
        }
    }
}
