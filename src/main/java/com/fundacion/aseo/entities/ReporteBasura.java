package com.fundacion.aseo.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import org.locationtech.jts.geom.Point;

@Entity
@Table(name = "reportes_basura")
@Data
public class ReporteBasura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String ciudadanoNombre;
    private String descripcion;
    private String fotoUrl;

    @Column(columnDefinition = "geometry(Point, 4326)")
    private Point ubicacion;

    private String estado = "PENDIENTE";
}
