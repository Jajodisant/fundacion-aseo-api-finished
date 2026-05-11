-- Datos de prueba para perfil local
-- Solo se ejecuta con spring.sql.init.mode=always (application-local.properties)
-- Spring divide data.sql por sentencias; evitamos bloques DO $$ ... $$ para mantener compatibilidad.

INSERT INTO reportes_puntos_criticos
    (nombre_imagen, tipo_contenido_imagen, imagen, latitud, longitud, ubicacion, estado, fecha_creacion)
SELECT *
FROM (
    VALUES
        ('test1.png','image/png', decode('89504e470d0a1a0a0000000d494844520000000100000001080200000090012d00000000c4944415478016360f8cfc00000000200018ee13590000000049454e44ae426082', 'hex'), 10.5200,-74.2000, ST_GeomFromText('POINT(-74.2000 10.5200)',4326),'PENDIENTE', NOW() - INTERVAL '3 hours'),
        ('test2.png','image/png', decode('89504e470d0a1a0a0000000d494844520000000100000001080200000090012d00000000c4944415478016360f8cfc00000000200018ee13590000000049454e44ae426082', 'hex'), 10.5250,-74.1950, ST_GeomFromText('POINT(-74.1950 10.5250)',4326),'PENDIENTE', NOW() - INTERVAL '2 hours'),
        ('test3.png','image/png', decode('89504e470d0a1a0a0000000d494844520000000100000001080200000090012d00000000c4944415478016360f8cfc00000000200018ee13590000000049454e44ae426082', 'hex'), 10.5180,-74.2100, ST_GeomFromText('POINT(-74.2100 10.5180)',4326),'PENDIENTE', NOW() - INTERVAL '1 hour'),
        ('test4.png','image/png', decode('89504e470d0a1a0a0000000d494844520000000100000001080200000090012d00000000c4944415478016360f8cfc00000000200018ee13590000000049454e44ae426082', 'hex'), 10.5310,-74.1870, ST_GeomFromText('POINT(-74.1870 10.5310)',4326),'PENDIENTE', NOW() - INTERVAL '30 minutes'),
        ('test5.png','image/png', decode('89504e470d0a1a0a0000000d494844520000000100000001080200000090012d00000000c4944415478016360f8cfc00000000200018ee13590000000049454e44ae426082', 'hex'), 10.5090,-74.2050, ST_GeomFromText('POINT(-74.2050 10.5090)',4326),'COMPLETADO', NOW() - INTERVAL '5 hours')
) AS datos(nombre_imagen, tipo_contenido_imagen, imagen, latitud, longitud, ubicacion, estado, fecha_creacion)
WHERE NOT EXISTS (
    SELECT 1
    FROM reportes_puntos_criticos
);
