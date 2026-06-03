package es.um.arso.pasarela.adaptadores.out;

import es.um.arso.pasarela.client.UsuarioOauthCreateRequest;
import es.um.arso.pasarela.client.UsuariosRestClient;
import es.um.arso.pasarela.client.VerificarCredencialesRequest;
import es.um.arso.pasarela.servicio.exception.UsuariosClientException;
import es.um.arso.pasarela.servicio.puertos.out.IServicioUsuariosExterno;
import es.um.arso.pasarela.servicio.puertos.out.UsuarioAuthInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Service
public class ServicioUsuariosAdapter implements IServicioUsuariosExterno {

    private static final Logger log = LoggerFactory.getLogger(ServicioUsuariosAdapter.class);

    private UsuariosRestClient client;

    public ServicioUsuariosAdapter(@Value("${servicios.usuarios.url}") String baseUrl) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        this.client = retrofit.create(UsuariosRestClient.class);
        log.info("Cliente usuarios configurado baseUrl={}", baseUrl);
    }

    @Override
    public UsuarioAuthInfo getUsuario(String idUsuario) {
        Response<UsuarioAuthInfo> response;
        try {
            response = client.getUsuario(idUsuario).execute();
        } catch (Exception e) {
            throw new UsuariosClientException("Error de comunicación con servicio usuarios", e);
        }

        if (response.code() == 404) {
            return null;
        }

        if (!response.isSuccessful()) {
            throw new UsuariosClientException(
                    "Error al obtener usuario: " + response.code() + " - " + response.message());
        }

        UsuarioAuthInfo body = response.body();
        if (body != null) {
            log.info("Usuario recuperado id={}", body.getId());
        }
        return body;
    }

    @Override
    public UsuarioAuthInfo autenticarCredenciales(String username, String password) {
        VerificarCredencialesRequest request = new VerificarCredencialesRequest(username, password);
        Response<UsuarioAuthInfo> response;
        try {
            response = client.verificarCredenciales(request).execute();
        } catch (Exception e) {
            throw new UsuariosClientException("Error de comunicación con servicio usuarios", e);
        }

        if (response.code() == 401) {
            log.info("Credenciales invalidas según servicio usuarios username={}", username);
            return null;
        }

        if (!response.isSuccessful()) {
            throw new UsuariosClientException(
                    "Error al verificar credenciales: " + response.code() + " - " + response.message());
        }

        UsuarioAuthInfo body = response.body();
        if (body != null) {
            log.info("Credenciales validas id={} roles={}", body.getId(), body.getRoles());
        }
        return body;
    }

    @Override
    public UsuarioAuthInfo buscarUsuario(String githubId, String email) {
        Response<UsuarioAuthInfo> response;
        try {
            response = client.buscarUsuario(githubId, email).execute();
        } catch (Exception e) {
            throw new UsuariosClientException("Error de comunicación con servicio usuarios", e);
        }

        if (response.code() == 404) {
            log.info("Usuario no encontrado en servicio usuarios");
            return null;
        }

        if (!response.isSuccessful()) {
            throw new UsuariosClientException(
                    "Error al buscar usuario: " + response.code() + " - " + response.message());
        }

        UsuarioAuthInfo usuario = response.body();
        if (usuario != null) {
            log.info("Usuario encontrado id={}", usuario.getId());
        }
        return usuario;
    }

    @Override
    public UsuarioAuthInfo crearUsuarioOauth(String nombre, String email, String githubId) {
        UsuarioOauthCreateRequest request = new UsuarioOauthCreateRequest(nombre, email, githubId);
        Response<UsuarioAuthInfo> response;
        try {
            log.info(
                    "Creando usuario OAuth en servicio usuarios githubId={} emailPresent={}",
                    githubId,
                    email != null && !email.trim().isEmpty());
            response = client.crearUsuarioOauth(request).execute();
        } catch (Exception e) {
            throw new UsuariosClientException("Error de comunicación con servicio usuarios", e);
        }

        if (!response.isSuccessful()) {
            throw new UsuariosClientException(
                    "Error al crear usuario OAuth: " + response.code() + " - " + response.message());
        }

        UsuarioAuthInfo body = response.body();
        if (body != null) {
            log.info("Usuario OAuth creado id={}", body.getId());
        }
        return body;
    }
}
