package com.fundacion.aseo.services;

import com.fundacion.aseo.entities.EstadoReporte;
import com.fundacion.aseo.entities.ReportePuntoCritico;
import com.fundacion.aseo.repositories.ReportePuntoCriticoRepository;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class ReportePuntoCriticoService {

    private static final Logger log = LoggerFactory.getLogger(ReportePuntoCriticoService.class);
    private static final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    private final ReportePuntoCriticoRepository repository;

    public ReportePuntoCriticoService(ReportePuntoCriticoRepository repository) {
        this.repository = repository;
    }

    public ReportePuntoCritico crearReporte(MultipartFile imagen, Double latitud, Double longitud) {
        try {
            String nombreImagen = System.currentTimeMillis() + "_" + imagen.getOriginalFilename();
            String tipoContenido = imagen.getContentType();
            byte[] bytes = imagen.getBytes();
            Point ubicacion = geometryFactory.createPoint(new Coordinate(longitud, latitud));
            ReportePuntoCritico reporte = new ReportePuntoCritico(nombreImagen, tipoContenido, bytes, latitud, longitud, ubicacion);
            return repository.save(reporte);
        } catch (IOException e) {
            log.error("Error saving image", e);
            throw new RuntimeException("Failed to store image", e);
        }
    }

    public void actualizarEstado(Long id, EstadoReporte nuevoEstado) {
        Optional<ReportePuntoCritico> optionalReporte = repository.findById(id);
        if (optionalReporte.isPresent()) {
            ReportePuntoCritico reporte = optionalReporte.get();
            reporte.setEstado(nuevoEstado);
            repository.save(reporte);
        } else {
            throw new IllegalArgumentException("Reporte no encontrado: " + id);
        }
    }

    public List<MapMarker> obtenerReportesParaMapa(EstadoReporte estado) {
        return repository.findAllByEstado(estado).stream()
                .map(r -> new MapMarker(
                        r.getId(),
                        r.getLatitud(),
                        r.getLongitud(),
                        r.getEstado(),
                        r.getTipoContenidoImagen(),
                        r.getImagen() != null ? Base64.getEncoder().encodeToString(r.getImagen()) : null
                ))
                .toList();
    }

    public Page<ReportePuntoCritico> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public List<ReportePuntoCritico> calcularRutaOptima(double latDepot, double lonDepot, List<Long> reportIds) {
        Point depot = geometryFactory.createPoint(new Coordinate(lonDepot, latDepot));
        return reportIds.stream()
                .map(repository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .sorted(Comparator.comparingDouble(r -> r.getUbicacion().distance(depot)))
                .toList();
    }

    public record MapMarker(
            Long id,
            Double lat,
            Double lon,
            EstadoReporte estado,
            String tipoContenido,
            String imagenB64
    ) {}
}
