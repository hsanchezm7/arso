# productos

Proyecto Spring Boot correspondiente al microservicio encargado de gestionar el catálogo de artículos disponibles en la plataforma.

## Eventos

### Consumidos

El servicio productos reacciona a los siguientes eventos consumiendo mensajes de la cola `arso.productos.queue` (asociada a las claves de enrutamiento `bus.usuarios.#` y `bus.compraventa.#` del exchange `arso.bus`):

| Evento               | Productor       | Clase mapeada             | Necesidad                                                                        |
|----------------------|-----------------|---------------------------|----------------------------------------------------------------------------------|
| `usuario-creado`     | **usuarios**    | `EventoUsuarioCreado`     | Mantener una copia local de los datos básicos del usuario.                       |
| `usuario-modificado` | **usuarios**    | `EventoUsuarioModificado` | Mantener actualizada la copia local del usuario.                                 |
| `compraventa-creada` | **compraventa** | `EventoCompraventaCreada` | Actualizar el estado del producto afectado (por ejemplo, marcarlo como vendido). |

### Producidos

Este servicio no produce ningún evento.
