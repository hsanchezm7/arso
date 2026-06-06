CREATE DATABASE IF NOT EXISTS usuarios_db;
CREATE DATABASE IF NOT EXISTS productos_db;
CREATE DATABASE IF NOT EXISTS ValoracionesDB;

USE productos_db;

CREATE TABLE IF NOT EXISTS categoria (
    id VARCHAR(255) NOT NULL,
    descripcion VARCHAR(255),
    nombre VARCHAR(255),
    ruta VARCHAR(255),
    parent_id VARCHAR(255),
    PRIMARY KEY (id),
    FOREIGN KEY (parent_id) REFERENCES categoria(id)
);

INSERT IGNORE INTO categoria (id, nombre, descripcion, ruta, parent_id) VALUES
('cat-deportes', 'Deportes', 'Artículos deportivos', NULL, NULL),
('cat-informatica', 'Informática', 'Productos de informática y tecnología', NULL, NULL),
('cat-motor', 'Motor', 'Coches, motos y recambios', NULL, NULL),
('cat-hogar', 'Hogar y Jardín', 'Muebles, decoración y herramientas', NULL, NULL),
('cat-moda', 'Moda', 'Ropa, calzado y accesorios', NULL, NULL),
('cat-consolas', 'Consolas y Videojuegos', 'Consolas, juegos y accesorios gamer', NULL, NULL),
('cat-bebes', 'Bebés y Niños', 'Juguetes, ropa y artículos para bebés', NULL, NULL),
('cat-coleccionismo', 'Coleccionismo', 'Antigüedades, arte y objetos de colección', NULL, NULL),
('cat-libros', 'Libros y Música', 'Libros, cómics, vinilos y CDs', NULL, NULL),
('cat-otros', 'Otros', 'Artículos variados', NULL, NULL),
('cat-bicicletas', 'Bicicletas', 'Bicicletas de montaña, carretera y paseo', NULL, 'cat-deportes'),
('cat-portatiles', 'Portátiles', 'Ordenadores portátiles y accesorios', NULL, 'cat-informatica'),
('cat-motos', 'Motos', 'Motocicletas, scooters y equipamiento', NULL, 'cat-motor'),
('cat-muebles', 'Muebles', 'Muebles para salón, dormitorio y cocina', NULL, 'cat-hogar'),
('cat-juegos-ps5', 'Juegos de PS5', 'Videojuegos para PlayStation 5', NULL, 'cat-consolas');
