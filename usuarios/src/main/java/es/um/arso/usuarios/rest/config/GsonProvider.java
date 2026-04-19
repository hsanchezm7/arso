package es.um.arso.usuarios.rest.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import es.um.arso.utils.LocalDateGsonAdapter;
import java.time.LocalDate;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

@Provider
public class GsonProvider implements ContextResolver<Gson> {

    private final Gson gson;

    public GsonProvider() {
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateGsonAdapter())
                .create();
    }

    @Override
    public Gson getContext(Class<?> type) {
        return gson;
    }
}
