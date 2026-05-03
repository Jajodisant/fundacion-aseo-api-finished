package com.fundacion.aseo.services;

import com.fundacion.aseo.entities.ReportePuntoCritico;
import com.fundacion.aseo.repositories.ReportePuntoCriticoRepository;
import java.io.IOException;
import java.util.*;
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

    private double haversineDistance(double lat1, double lon1, double lat2, double lon2) {
        final double R = 6371.0; // km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    public List<ReportePuntoCritico> calcularRutaOptima(double latDepot, double lonDepot, List<Long> reportIds) {
        if (reportIds.isEmpty()) {
            return List.of();
        }
        if (reportIds.size() > 10) {
            throw new IllegalArgumentException("Máximo 10 puntos para ruta óptima.");
        }

        List<ReportePuntoCritico> reports = repository.findAllById(reportIds);
        if (reports.size() != reportIds.size()) {
            throw new IllegalArgumentException("Algunos reportes no encontrados.");
        }

        record PointData(long id, double lat, double lon) {}

        List<PointData> points = new ArrayList<>();
        points.add(new PointData(0L, latDepot, lonDepot));
        for (int i = 0; i < reports.size(); i++) {
            ReportePuntoCritico r = reports.get(i);
            points.add(new PointData(r.getId(), r.getLatitud(), r.getLongitud()));
        }
        int n = points.size();

        double[][] distanceMatrix = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                distanceMatrix[i][j] = haversineDistance(points.get(i).lat(), points.get(i).lon(), points.get(j).lat(), points.get(j).lon());
            }
        }

        double[][] dp = new double[1 << n][n];
        int[][] prev = new int[1 << n][n];
        for (int i = 0; i < (1 << n); i++) {
            Arrays.fill(dp[i], Double.POSITIVE_INFINITY);
            Arrays.fill(prev[i], -1);
        }
        dp[1][0] = 0; // bit 0 for depot

        for (int mask = 0; mask < (1 << n); mask++) {
            for (int u = 0; u < n; u++) {
                if (dp[mask][u] > 1e20) continue;
                for (int v = 0; v < n; v++) {
                if ((mask & (1 << v)) != 0) continue;
                    int newMask = mask | (1 << v);
                    double newDist = dp[mask][u] + distanceMatrix[u][v];
                    if (newDist < dp[newMask][v]) {
                        dp[newMask][v] = newDist;
                        prev[newMask][v] = u;
                    }
                }
            }
        }

        // Find end city with min dist
        int fullMask = (1 << n) - 1;
        double minDist = Double.POSITIVE_INFINITY;
        int endCity = -1;
        for (int i = 1; i < n; i++) {
            if (dp[fullMask][i] < minDist) {
                minDist = dp[fullMask][i];
                endCity = i;
            }
        }
        if (endCity == -1) {
            throw new IllegalStateException("No ruta encontrada.");
        }

        // Backtrack path
        List<Integer> path = new ArrayList<>();
        int mask = fullMask;
        int curr = endCity;
        while (curr != -1) {
            path.add(curr);
            int p = prev[mask][curr];
            mask = mask ^ (1 << curr);
            curr = p;
        }
        Collections.reverse(path); // depot first

        // Build ordered reports (skip depot)
        List<ReportePuntoCritico> ordered = new ArrayList<>();
        for (int i = 1; i < path.size(); i++) {
            int pidx = path.get(i);
            ordered.add(reports.get(pidx - 1));
        }
        return ordered;
    }
}
