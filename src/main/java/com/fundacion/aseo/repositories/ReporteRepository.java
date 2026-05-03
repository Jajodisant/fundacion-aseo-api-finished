package com.fundacion.aseo.repositories;

import com.fundacion.aseo.entities.ReporteBasura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReporteRepository extends JpaRepository<ReporteBasura, Long> {
}
