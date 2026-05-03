CREATE EXTENSION IF NOT EXISTS postgis;

CREATE TABLE IF NOT EXISTS reportes_basura (
    id BIGSERIAL PRIMARY KEY,
    ciudadano_nombre VARCHAR(255),
    descripcion VARCHAR(255),
    foto_url VARCHAR(255),
    ubicacion geometry(Point, 4326),
    estado VARCHAR(255) DEFAULT 'PENDIENTE'
);
