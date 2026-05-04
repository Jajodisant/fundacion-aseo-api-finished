package com.fundacion.aseo.entities;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import org.locationtech.jts.geom.Point;

@Entity
@Table(name = "reportes_puntos_criticos")
public class ReportePuntoCritico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String nombreImagen;

    @Column(nullable = false, length = 30)
    private String tipoContenidoImagen;

    @Basic(optional = false)
    @Column(nullable = false, columnDefinition = "bytea")
    private byte[] imagen;

    @Column(nullable = false)
    private Double latitud;

    @Column(nullable = false)
    private Double longitud;

    @Column(nullable = false, columnDefinition = "geometry(Point,4326)")
    private Point ubicacion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EstadoReporte estado;

    @Column(nullable = false)
    private LocalDateTime fechaCreacion;

    protected ReportePuntoCritico() {
    }

    public ReportePuntoCritico(
            String nombreImagen,
            String tipoContenidoImagen,
            byte[] imagen,
            Double latitud,
            Double longitud,
            Point ubicacion
    ) {
        this.nombreImagen = nombreImagen;
        this.tipoContenidoImagen = tipoContenidoImagen;
        this.imagen = imagen;
        this.latitud = latitud;
        this.longitud = longitud;
        this.ubicacion = ubicacion;
        this.estado = EstadoReporte.PENDIENTE;
        this.fechaCreacion = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getNombreImagen() {
        return nombreImagen;
    }

    public String getTipoContenidoImagen() {
        return tipoContenidoImagen;
    }

    public byte[] getImagen() {
        return imagen;
    }

    public Double getLatitud() {
        return latitud;
    }

    public Double getLongitud() {
        return longitud;
    }

    public Point getUbicacion() {
        return ubicacion;
    }

    public EstadoReporte getEstado() {
        return estado;
    }

    public void setEstado(EstadoReporte estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }
}
