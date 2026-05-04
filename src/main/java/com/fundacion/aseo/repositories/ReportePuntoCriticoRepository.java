package com.fundacion.aseo.repositories;

import com.fundacion.aseo.entities.ReportePuntoCritico;
import org.springframework.data.jpa.repository.JpaRepository;

import com.fundacion.aseo.entities.EstadoReporte;
import java.util.List;

public interface ReportePuntoCriticoRepository extends JpaRepository<ReportePuntoCritico, Long> {
    List<ReportePuntoCritico> findAllByEstado(EstadoReporte estado);
}
