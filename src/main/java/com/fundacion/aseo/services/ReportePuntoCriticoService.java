package com.fundacion.aseo.services;

import com.fundacion.aseo.entities.ReportePuntoCritico;
import com.fundacion.aseo.repositories.ReportePuntoCriticoRepository;
import java.io.IOException;
import java.util.Set;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ReportePuntoCriticoService {

    private static final int SRID_WGS84 = 4326;
    private static final Set<String> TIPOS_IMAGEN_PERMITIDOS = Set.of("image/jpeg", "image/png");

    private final ReportePuntoCriticoRepository repository;
    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), SRID_WGS84);

    public ReportePuntoCriticoService(ReportePuntoCriticoRepository repository) {
        this.repository = repository;
    }

    public ReportePuntoCritico crearReporte(MultipartFile imagen, Double latitud, Double longitud) {
        validarImagen(imagen);
        validarCoordenadas(latitud, longitud);

        try {
            var ubicacion = geometryFactory.createPoint(new Coordinate(longitud, latitud));
            ubicacion.setSRID(SRID_WGS84);

            var reporte = new ReportePuntoCritico(
                    obtenerNombreImagen(imagen),
                    imagen.getContentType(),
                    imagen.getBytes(),
                    latitud,
                    longitud,
                    ubicacion
            );

            return repository.save(reporte);
        } catch (IOException exception) {
            throw new IllegalArgumentException("No fue posible leer la imagen adjunta.", exception);
        }
    }

    private void validarImagen(MultipartFile imagen) {
        if (imagen == null || imagen.isEmpty()) {
            throw new IllegalArgumentException("La imagen del reporte es obligatoria.");
        }

        if (!TIPOS_IMAGEN_PERMITIDOS.contains(imagen.getContentType())) {
            throw new IllegalArgumentException("La imagen debe estar en formato JPG o PNG.");
        }
    }

    private void validarCoordenadas(Double latitud, Double longitud) {
        if (latitud == null || longitud == null) {
            throw new IllegalArgumentException("La latitud y la longitud son obligatorias.");
        }

        if (latitud < -90 || latitud > 90) {
            throw new IllegalArgumentException("La latitud debe estar entre -90 y 90.");
        }

        if (longitud < -180 || longitud > 180) {
            throw new IllegalArgumentException("La longitud debe estar entre -180 y 180.");
        }
    }

    private String obtenerNombreImagen(MultipartFile imagen) {
        var nombreOriginal = imagen.getOriginalFilename();
        return nombreOriginal == null || nombreOriginal.isBlank() ? "reporte-basura" : nombreOriginal;
    }
}
