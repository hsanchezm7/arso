package es.um.arso.compraventa.adaptadores;

import es.um.arso.compraventa.client.UsuariosRestClient;
import es.um.arso.compraventa.servicio.puertos.IServicioUsuariosExterno;
import es.um.arso.compraventa.servicio.puertos.UsuarioInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Service
public class ServicioUsuariosAdapter implements IServicioUsuariosExterno {

    private UsuariosRestClient client;

    public ServicioUsuariosAdapter(@Value("${servicios.usuarios.url}") String baseUrl) {
        Retrofit retrofit =
                new Retrofit.Builder()
                        .baseUrl(baseUrl)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

        this.client = retrofit.create(UsuariosRestClient.class);
    }

    @Override
    public UsuarioInfo getUsuario(String idUsuario) throws Exception {
        Response<UsuarioInfo> response = client.getUsuario(idUsuario).execute();

        if (!response.isSuccessful()) {
            throw new RuntimeException(
                    "Error al obtener usuario: " + response.code() + " - " + response.message());
        }

        return response.body();
    }
}
