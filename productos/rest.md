# Especificación REST - Microservicio Productos

Este documento describe, de forma informal, la especificación REST 
para el microservicio de productos. Las operaciones descritas se 
aplican sobre las colecciones de:

 - **Productos**

 - **Categorías**

## Productos

### Crear producto

    POST /productos
    Contenido petición: titulo, descripcion, precio, estado, categoriaId, envioDisponible, vendedorId
    Retorno: 201 Created y URL en la cabecera "Location"

### Recuperar producto

    GET /productos/{id}
    Retorno: 200 OK y datos del producto

### Modificar producto

    PUT /productos/{id}
    Contenido petición: precio, descripcion
    Retorno: 204 No Content

### Asignar lugar de recogida

    PUT /productos/{id}/lugar-recogida
    Contenido petición: descripcion, longitud, latitud
    Retorno: 204 No Content

### Añadir visualización

    POST /productos/{id}/visualizaciones
    Retorno: 204 No Content

### Buscar productos

    GET /productos?categoriaId=X&texto=Y&estadoMinimo=Z&precioMaximo=W
    Retorno: 200 OK y listado de productos

### Obtener historial del mes

    GET /productos/historial?mes=X&anio=Y
    Retorno: 200 OK y listado de ProductoResumen

## Categorías

### Obtener categorías raíz

    GET /categorias
    Retorno: 200 OK y listado de categorías raíz

### Recuperar categoría

    GET /categorias/{id}
    Retorno: 200 OK y datos de la categoría

### Obtener descendientes de categoría

    GET /categorias/{id}/descendientes
    Retorno: 200 OK y listado de descendientes
