package es.um.arso.compraventa.client;

import es.um.arso.compraventa.servicio.puertos.out.UsuarioInfo;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;

public interface UsuariosRestClient {
    @Headers("Accept: application/json")
    @GET("api/usuarios/{id}/nombre")
    Call<UsuarioInfo> getUsuario(@Path("id") String id);
}
