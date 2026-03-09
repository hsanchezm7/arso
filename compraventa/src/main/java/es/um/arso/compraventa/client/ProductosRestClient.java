package es.um.arso.compraventa.client;

import es.um.arso.compraventa.servicio.puertos.ProductoInfo;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;

public interface ProductosRestClient {

    @Headers("Accept: application/json")
    @GET("productos/{id}")
    Call<ProductoInfo> getProducto(@Path("id") String id);
}
