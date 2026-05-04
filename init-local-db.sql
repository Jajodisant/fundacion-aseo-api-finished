-- Enable PostGIS extension
CREATE EXTENSION IF NOT EXISTS postgis;

-- Crear tabla si no existe (con imagen como bytea)
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

-- Migración: si la columna imagen es tipo oid (por @Lob antiguo), convertirla a bytea
DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'reportes_puntos_criticos'
          AND column_name = 'imagen'
          AND data_type = 'oid'
    ) THEN
        ALTER TABLE reportes_puntos_criticos DROP COLUMN imagen;
        ALTER TABLE reportes_puntos_criticos ADD COLUMN imagen BYTEA NOT NULL DEFAULT '\x00';
        ALTER TABLE reportes_puntos_criticos ALTER COLUMN imagen DROP DEFAULT;
    END IF;
END $$;

-- Imagen placeholder PNG 1x1 pixel (válida)
DO $$
DECLARE
  placeholder_png bytea := decode(
    '89504e470d0a1a0a0000000d49484452000000010000000108020000009001'||
    '2d00000000c4944415478016360f8cfc00000000200018ee13590000000049454e44ae426082',
    'hex');
BEGIN
  INSERT INTO reportes_puntos_criticos
    (nombre_imagen, tipo_contenido_imagen, imagen, latitud, longitud, ubicacion, estado, fecha_creacion)
  VALUES
    ('test1.png','image/png', placeholder_png, 10.5200,-74.2000, ST_GeomFromText('POINT(-74.2000 10.5200)',4326),'PENDIENTE', NOW() - INTERVAL '3 hours'),
    ('test2.png','image/png', placeholder_png, 10.5250,-74.1950, ST_GeomFromText('POINT(-74.1950 10.5250)',4326),'PENDIENTE', NOW() - INTERVAL '2 hours'),
    ('test3.png','image/png', placeholder_png, 10.5180,-74.2100, ST_GeomFromText('POINT(-74.2100 10.5180)',4326),'PENDIENTE', NOW() - INTERVAL '1 hour'),
    ('test4.png','image/png', placeholder_png, 10.5310,-74.1870, ST_GeomFromText('POINT(-74.1870 10.5310)',4326),'PENDIENTE', NOW() - INTERVAL '30 minutes'),
    ('test5.png','image/png', placeholder_png, 10.5090,-74.2050, ST_GeomFromText('POINT(-74.2050 10.5090)',4326),'COMPLETADO', NOW() - INTERVAL '5 hours')
  ON CONFLICT DO NOTHING;
END $$;
