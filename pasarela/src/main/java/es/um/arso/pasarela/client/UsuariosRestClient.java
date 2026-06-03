package es.um.arso.pasarela.client;

import es.um.arso.pasarela.servicio.puertos.out.UsuarioAuthInfo;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface UsuariosRestClient {
    @Headers("Accept: application/json")
    @GET("api/usuarios/{id}/info")
    Call<UsuarioAuthInfo> getUsuario(@Path("id") String id);

    @Headers({"Accept: application/json", "Content-Type: application/json"})
    @POST("api/usuarios/verificar")
    Call<UsuarioAuthInfo> verificarCredenciales(@Body VerificarCredencialesRequest request);

    @Headers("Accept: application/json")
    @GET("api/usuarios/buscar")
    Call<UsuarioAuthInfo> buscarUsuario(@Query("githubId") String githubId, @Query("email") String email);

    @Headers({"Accept: application/json", "Content-Type: application/json"})
    @POST("api/usuarios/oauth")
    Call<UsuarioAuthInfo> crearUsuarioOauth(@Body UsuarioOauthCreateRequest request);
}
