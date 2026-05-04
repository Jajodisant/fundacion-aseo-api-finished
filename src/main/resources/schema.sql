CREATE EXTENSION IF NOT EXISTS postgis;

CREATE TABLE IF NOT EXISTS reportes_puntos_criticos (
    id BIGSERIAL PRIMARY KEY,
    nombre_imagen VARCHAR(120) NOT NULL,
    tipo_contenido_imagen VARCHAR(30) NOT NULL,
    imagen BYTEA NOT NULL,
    latitud DOUBLE PRECISION NOT NULL,
    longitud DOUBLE PRECISION NOT NULL,
    ubicacion geometry(Point,4326) NOT NULL,
    estado VARCHAR(30) NOT NULL DEFAULT 'PENDIENTE',
    fecha_creacion TIMESTAMP NOT NULL
);

-- Eliminar tabla huérfana si existe
DROP TABLE IF EXISTS reportes_basura;
