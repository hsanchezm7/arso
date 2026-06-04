# compraventa

Proyecto Spring Boot correspondiente al microservicio encargado de gestionar las transacciones de compraventa en la plataforma.

## Eventos

### Consumidos

Este servicio no produce ningún evento del broker de mensajes. En su lugar, usa clientes REST de Retrofit para comunicar con los servicios de **productos** y **usuarios** para validar información de una nueva transacción.

### Producidos

El servicio publica los siguientes eventos en el exchange `arso.bus` con el prefijo de enrutamiento `bus.compraventa.*`:

| Evento               | Clase de Evento           | Necesidad                                                                                                     | Generación                                                      |
|----------------------|---------------------------|---------------------------------------------------------------------------------------------------------------|-----------------------------------------------------------------|
| `compraventa-creada` | `EventoCompraventaCreada` | Notificar al resto del sistema para que los demás servicios actualicen sus contadores y modifiquen el modelo. | Un usuario realiza la compra del producto de otro exitosamente. |
