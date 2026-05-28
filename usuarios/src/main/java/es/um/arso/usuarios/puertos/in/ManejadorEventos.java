package es.um.arso.usuarios.puertos.in;

import es.um.arso.repositorio.EntidadNoEncontrada;
import es.um.arso.repositorio.RepositorioException;
import es.um.arso.servicio.FactoriaServicios;
import es.um.arso.usuarios.modelo.Usuario;
import es.um.arso.usuarios.servicio.IServicioUsuarios;

public class ManejadorEventos implements IManejadorEventos {

    private IServicioUsuarios servicio = FactoriaServicios.getServicio(IServicioUsuarios.class);

    @Override
    public void compraventaCreada(String idVendedor, String idComprador)
            throws RepositorioException, EntidadNoEncontrada {
        Usuario vendedor = this.servicio.recuperar(idVendedor);
        vendedor.incrementarNumeroVentas();
        this.servicio.modificar(idVendedor, vendedor);

        Usuario comprador = this.servicio.recuperar(idComprador);
        comprador.incrementarNumeroCompras();
        this.servicio.modificar(idComprador, comprador);
    }

    @Override
    public void valoracionCreada(String idUsuarioValorado, String rolUsuarioValorado, int puntuacion)
            throws RepositorioException, EntidadNoEncontrada {
        if (!rolUsuarioValorado.equals("comprador") && !rolUsuarioValorado.equals("vendedor"))
            throw new IllegalArgumentException("El usuario valorado debe tener rol comprador o vendedor");

        Usuario valorado;
        valorado = this.servicio.recuperar(idUsuarioValorado);
        valorado.valorar(puntuacion, rolUsuarioValorado);

        this.servicio.modificar(idUsuarioValorado, valorado);
    }
}
