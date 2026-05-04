package com.fundacion.aseo.controllers;

import com.fundacion.aseo.entities.EstadoReporte;
import com.fundacion.aseo.entities.ReportePuntoCritico;
import com.fundacion.aseo.services.ReportePuntoCriticoService;
import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fundacion.aseo.services.ReportePuntoCriticoService.MapMarker;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/reportes")
public class ReportePuntoCriticoController {

    private static final Logger log = LoggerFactory.getLogger(ReportePuntoCriticoController.class);

    private final ReportePuntoCriticoService service;

    public ReportePuntoCriticoController(ReportePuntoCriticoService service) {
        this.service = service;
    }

    @GetMapping("/admin/reportes-mapa")
    public List<MapMarker> obtenerReportesParaMapa(
            @RequestParam(value = "estado", required = false, defaultValue = "PENDIENTE") EstadoReporte estado) {
        return service.obtenerReportesParaMapa(estado);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReportePuntoCriticoResponse crearReporte(
            @RequestPart("imagen") MultipartFile imagen,
            @RequestParam("latitud") Double latitud,
            @RequestParam("longitud") Double longitud
    ) {
        return ReportePuntoCriticoResponse.desde(service.crearReporte(imagen, latitud, longitud));
    }

    @PatchMapping("/{id}/estado")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void actualizarEstado(@PathVariable Long id, @RequestBody EstadoRequest request) {
        EstadoReporte nuevoEstado = EstadoReporte.valueOf(request.estado());
        service.actualizarEstado(id, nuevoEstado);
    }

    @GetMapping
    public Page<ReportePuntoCriticoResponse> listarReportes(@PageableDefault(page = 0, size = 10) Pageable pageable) {
        return service.findAll(pageable)
                .map(ReportePuntoCriticoResponse::desde);
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

    public record EstadoRequest(String estado) {}
}
