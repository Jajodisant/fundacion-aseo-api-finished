-- Datos de prueba para perfil local
-- Solo se ejecuta con spring.sql.init.mode=always (application-local.properties)

DO $$
DECLARE
  placeholder_png bytea := decode(
    '89504e470d0a1a0a0000000d49484452000000010000000108020000009001'||
    '2d00000000c4944415478016360f8cfc00000000200018ee13590000000049454e44ae426082',
    'hex');
BEGIN
  -- Solo insertar si la tabla está vacía
  IF (SELECT COUNT(*) FROM reportes_puntos_criticos) = 0 THEN
    INSERT INTO reportes_puntos_criticos
      (nombre_imagen, tipo_contenido_imagen, imagen, latitud, longitud, ubicacion, estado, fecha_creacion)
    VALUES
      ('test1.png','image/png', placeholder_png, 10.5200,-74.2000, ST_GeomFromText('POINT(-74.2000 10.5200)',4326),'PENDIENTE', NOW() - INTERVAL '3 hours'),
      ('test2.png','image/png', placeholder_png, 10.5250,-74.1950, ST_GeomFromText('POINT(-74.1950 10.5250)',4326),'PENDIENTE', NOW() - INTERVAL '2 hours'),
      ('test3.png','image/png', placeholder_png, 10.5180,-74.2100, ST_GeomFromText('POINT(-74.2100 10.5180)',4326),'PENDIENTE', NOW() - INTERVAL '1 hour'),
      ('test4.png','image/png', placeholder_png, 10.5310,-74.1870, ST_GeomFromText('POINT(-74.1870 10.5310)',4326),'PENDIENTE', NOW() - INTERVAL '30 minutes'),
      ('test5.png','image/png', placeholder_png, 10.5090,-74.2050, ST_GeomFromText('POINT(-74.2050 10.5090)',4326),'COMPLETADO', NOW() - INTERVAL '5 hours');
  END IF;
END $$;
