package es.um.arso.compraventa.adaptadores.out;

import es.um.arso.compraventa.client.UsuariosRestClient;
import es.um.arso.compraventa.repositorio.EntidadNoEncontrada;
import es.um.arso.compraventa.servicio.exception.ServicioExternoException;
import es.um.arso.compraventa.servicio.puertos.out.IServicioUsuariosExterno;
import es.um.arso.compraventa.servicio.puertos.out.UsuarioInfo;
import java.io.IOException;
import org.springframework.stereotype.Service;
import retrofit2.Response;

@Service
public class ServicioUsuariosAdapter implements IServicioUsuariosExterno {

    private final UsuariosRestClient client;

    public ServicioUsuariosAdapter(UsuariosRestClient client) {
        this.client = client;
    }

    @Override
    public UsuarioInfo getUsuario(String idUsuario) throws EntidadNoEncontrada, ServicioExternoException {
        try {
            Response<UsuarioInfo> response = client.getUsuario(idUsuario).execute();

            if (!response.isSuccessful()) handleError(response);

            return response.body();
        } catch (IOException e) {
            throw new ServicioExternoException("Error al comunicar con el servicio de usuarios: " + e.getMessage(), e);
        }
    }

    private void handleError(Response<UsuarioInfo> response) throws EntidadNoEncontrada, ServicioExternoException {
        int code = response.code();

        switch (code) {
            case 404:
                throw new EntidadNoEncontrada("Usuario no existe");
            default:
                throw new ServicioExternoException(
                        "Error con el servicio de usuarios. HTTP " + code + " - " + response.message());
        }
    }
}
