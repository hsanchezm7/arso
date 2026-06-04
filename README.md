# arso

## Servicios

| servicio        | directorio                          | puertos (Host:Contenedor)  | OpenAPI / Swagger UI                                                           |
|-----------------|-------------------------------------|----------------------------|--------------------------------------------------------------------------------|
| `mongo`         | -                                   | `27018:27017`              | -                                                                              |
| `mongo-express` | -                                   | `8085:8085`                | -                                                                              |
| `rabbitmq`      | -                                   | `5672:5672`, `15672:15672` | -                                                                              |
| `mysql`         | -                                   | `3307:3306`                | -                                                                              |
| `pasarela`      | [pasarela](pasarela/)               | `8080:8080`                | -                                                                              |
| `usuarios`      | [usuarios](usuarios/)               | `8081:8081`                | -                                                                              |
| `productos`     | [productos](productos/)             | `8082:8082`                | [http://localhost:8082/swagger-ui.html](http://localhost:8082/swagger-ui.html) |
| `compraventa`   | [compraventa](compraventa/)         | `8083:8083`                | [http://localhost:8083/swagger-ui.html](http://localhost:8083/swagger-ui.html) |
| `valoraciones`  | [ValoracionesApi](ValoracionesApi/) | `8084:8084`                | [http://localhost:8084/swagger](http://localhost:8084/swagger)                 |

## Broker de mensajes RabbitMQ

En el fichero [definitions.json](definitions.json) se declaran el bus, las colas y los bindings
necesarios en la arquitectura. Esto ayuda a crear las conexiones en el arranque de la plataforma. En
dicho fichero se definen también el usuario y la contraseña de RabbitMQ (`arso:arso`).

## Acerca de la autenticación y OAuth

La pasarela está configurada con dos tipos de autenticaciones, vía login normal (`/auth/login`) y
vía OAuth de GitHub (`/oauth2/authorization/github`). [Ver más](pasarela/README.md).

El microservicio usuarios mantiene una relación entre el email y el githubId, ya que un usuario
registrado con un email puede iniciar sesión con GitHub si usa el mismo email. Por tanto, es
necesario establecer el email público en GitHub para la correcta autenticación vía OAuth. De otra
forma, GitHub lo oculta a la aplicación.

## Pruebas postman

Las pruebas en Postman se han configurado mediante variables de entorno. Se van definiendo los
valores de éstas con scripts que extraen los datos de los cuerpos de las respuestas o de las
cabeceras (como `Location` gracias a HATEOAS). Se recomienda seguir el siguiente flujo entre
colecciones:

1. auth (microservicios pararela y usuarios)

2. usuarios

3. productos

4. compraventas

5. usuarios y productos nuevamente para visualizar los cambios en las entidades
