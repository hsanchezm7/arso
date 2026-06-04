# pasarela

Proyecto Netflix Zuul (basado en Spring) correspondiente al microservicio encargado de gestionar las peticiones como punto de entrada a la aplicación. Ofrece un controlador Auth para la gestión de la autenticación.

## Autenticación

La autenticación se realiza haciendo uso de dos tokens JWT:

- **Token de acceso** (`accessToken`): tiene un ciclo de vida limitado por `jwt.access.expirationSeconds`, normalemente 10 o 15 minutos. Este token se envía al cliente en la respuesta en la entidad `LoginResponseDto` tras a hacer `/login` o `/refresh`. El cliente debe enviarlo al servidor mediante una cabecera `Authorization: Bearer <accessToken>`. En cada petición autenticada, la pasarela comprueba el token mediante el filtro `JwtRequestFilter`.

> [!WARNING]
> Una vulnerabilidad de este enfoque es que un usuario invalidado por el servicio de usuarios (por ejemplo, siendo eliminado de la base de datos), puede atravesar la pasarela hasta que caduque su token de acceso.

- **Token de renovación** (`refreshToken`): tiene un mayor ciclo de vida definido en `jwt.refresh.expirationSeconds`, normalmente de 7 días. Este token no se renueva en el endpoint `/refresh`, si no que, una vez caduca, el usuario debe volver a introducir las credenciales iniciando sesión

## Autorización

La autorización de roles es responsabilidad de cada uno de los microservicios.
