package com.fundacion.aseo.controllers;

import com.fundacion.aseo.entities.EstadoReporte;
import com.fundacion.aseo.entities.ReportePuntoCritico;
import com.fundacion.aseo.services.ReportePuntoCriticoService;
import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/reportes")
public class ReportePuntoCriticoController {

    private final ReportePuntoCriticoService service;

    public ReportePuntoCriticoController(ReportePuntoCriticoService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReportePuntoCriticoResponse crearReporte(
            @RequestParam("imagen") MultipartFile imagen,
            @RequestParam("latitud") Double latitud,
            @RequestParam("longitud") Double longitud
    ) {
        return ReportePuntoCriticoResponse.desde(service.crearReporte(imagen, latitud, longitud));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public void manejarSolicitudInvalida(IllegalArgumentException exception) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exception.getMessage(), exception);
    }

    public record ReportePuntoCriticoResponse(
            Long id,
            String nombreImagen,
            Double latitud,
            Double longitud,
            EstadoReporte estado,
            LocalDateTime fechaCreacion
    ) {
        public static ReportePuntoCriticoResponse desde(ReportePuntoCritico reporte) {
            return new ReportePuntoCriticoResponse(
                    reporte.getId(),
                    reporte.getNombreImagen(),
                    reporte.getLatitud(),
                    reporte.getLongitud(),
                    reporte.getEstado(),
                    reporte.getFechaCreacion()
            );
        }
    }
}
