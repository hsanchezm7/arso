# Especificación REST - Microservicio Usuarios

Este documento describe, de forma informal, la especificación REST para el microservicio de
usuarios. Las operaciones descritas se aplican únicamente sobre las colección de Usuarios.

## Usuarios

### Alta de usuario

    POST /usuarios
    Contenido petición: nombre, apellidos, email, clave, fechaNacimiento, telefono
    Retorno: 201 Created y URL en la cabecera "Location"

### Recuperar usuario

    GET /usuarios/{id}
    Retorno: 200 OK y datos del usuario

### Modificar usuario

    PUT /usuarios/{id}
    Contenido petición: nombre, apellidos, clave, fechaNacimiento, telefono
    Retorno: 204 No Content

### Listar usuarios

    GET /usuarios
    Retorno: 200 OK y listado de usuarios
