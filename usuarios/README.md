# usuarios

Proyecto JAX-RS (Jersey) correspondiente al microservicio encargado de la gestión de los usuarios en el sistema.

## Eventos

### Consumidos

El servicio usuarios reacciona a los siguientes eventos consumiendo mensajes de la cola `arso.usuarios.queue` (asociada a las claves de enrutamiento `bus.compraventa.#` y `bus.valoraciones.#` del exchange `arso.bus`):

| Evento               | Productor        | Clase mapeada             | Necesidad                                                                               |
|----------------------|------------------|---------------------------|-----------------------------------------------------------------------------------------|
| `compraventa-creada` | **compraventa**  | `EventoCompraventaCreada` | Incrementar el contadores de compras/ventas.                                            |
| `valoracion-creada`  | **valoraciones** | `EventoValoracionCreada`  | Actualizar la valoración media de un usuario incorporando la nueva puntuación recibida. |

### Producidos

El servicio publica los siguientes eventos en el exchange `arso.bus` con el prefijo de enrutamiento `bus.usuarios.*`:

| Evento               | Clase de Evento           | Necesidad                                                         | Generación                            |
|----------------------|---------------------------|-------------------------------------------------------------------|---------------------------------------|
| `usuario-creado`     | `EventoUsuarioCreado`     | Notificar al resto de servicios que usen datos de usuarios.       | Un usuario se registra en el sistema. |
| `usuario-modificado` | `EventoUsuarioModificado` | Que los servicios mantenga su copia local de usuario actualizada. | Un usuario actualiza sus datos.       |
