package com.fundacion.aseo.controllers;

import com.fundacion.aseo.entities.ReportePuntoCritico;
import com.fundacion.aseo.services.ReportePuntoCriticoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class RutaOptimaController {

    private final ReportePuntoCriticoService service;

    public RutaOptimaController(ReportePuntoCriticoService service) {
        this.service = service;
    }

    public record RutaRequest(double latDepot, double lonDepot, List<Long> reportIds) {}

    public record RutaOptimaResponse(Long id, Double latitud, Double longitud) {
        public static RutaOptimaResponse desde(ReportePuntoCritico r) {
            return new RutaOptimaResponse(r.getId(), r.getLatitud(), r.getLongitud());
        }
    }

    @PostMapping("/ruta-optima")
    public ResponseEntity<List<RutaOptimaResponse>> calcularRutaOptima(@RequestBody RutaRequest request) {
        List<ReportePuntoCritico> ruta = service.calcularRutaOptima(request.latDepot(), request.lonDepot(), request.reportIds());
        List<RutaOptimaResponse> response = ruta.stream().map(RutaOptimaResponse::desde).toList();
        return ResponseEntity.ok(response);
    }
}

