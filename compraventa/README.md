# compraventa

Proyecto Spring Boot correspondiente al microservicio encargado de gestionar las transacciones de compraventa en la plataforma.

## Eventos

### Consumidos

El servicio compraventa reacciona a los siguientes eventos consumiendo mensajes de la cola `arso.compraventa.queue` (asociada a la clave de enrutamiento `bus.usuarios.#` del exchange `arso.bus`):

| Evento               | Productor    | Clase mapeada             | Necesidad                                                        |
|----------------------|--------------|---------------------------|------------------------------------------------------------------|
| `usuario-modificado` | **usuarios** | `EventoUsuarioModificado` | Actualizar el nombre del vendedor/comprador en las compraventas. |

Además, usa clientes REST de Retrofit para comunicar con los servicios de **productos** y **usuarios** para validar información de una nueva transacción.

### Producidos

El servicio publica los siguientes eventos en el exchange `arso.bus` con el prefijo de enrutamiento `bus.compraventa.*`:

| Evento               | Clase de Evento           | Necesidad                                                                                                     | Generación                                                      |
|----------------------|---------------------------|---------------------------------------------------------------------------------------------------------------|-----------------------------------------------------------------|
| `compraventa-creada` | `EventoCompraventaCreada` | Notificar al resto del sistema para que los demás servicios actualicen sus contadores y modifiquen el modelo. | Un usuario realiza la compra del producto de otro exitosamente. |
