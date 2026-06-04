# valoraciones

Proyecto ASP.NET (.NET 8) correspondiente al microservicio encargado de gestionar las valoraciones entre compradores y vendedores de la plataforma.

## Eventos

### Consumidos

Este servicio no consume ningún evento. Al igual que el servicio de compraventa, se comunica de forma síncrona mediante un cliente REST (`ICompraventasClient`) con el servicio de **compraventa** para verificar que la transacción asociada existe y deducir quién es el evaluador y quién el valorado antes de registrar la puntuación.

### Producidos

El servicio publica los siguientes eventos en el exchange `arso.bus` con el prefijo de enrutamiento `bus.valoraciones.*`:

| Evento              | Clase de Evento          | Necesidad                                                           | Generación                                                              |
|---------------------|--------------------------|---------------------------------------------------------------------|-------------------------------------------------------------------------|
| `valoracion-creada` | `EventoValoracionCreada` | Notificar al resto del sistema para que otros servicios reaccionen. | Un usuario evalúa su experiencia tras una transacción con otro usuario. |
