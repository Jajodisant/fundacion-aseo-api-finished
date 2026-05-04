package com.fundacion.aseo.controllers;

import com.zaxxer.hikari.HikariDataSource;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/sistema/db")
public class DatabaseStatusController {

    private final DataSource dataSource;

    public DatabaseStatusController(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @GetMapping("/estado")
    public DatabaseStatusResponse obtenerEstadoConexion() {
        try (var connection = dataSource.getConnection()) {
            var metadata = connection.getMetaData();
            return new DatabaseStatusResponse(
                    connection.isValid(2),
                    dataSource.getClass().getSimpleName(),
                    obtenerNombrePool(),
                    metadata.getDatabaseProductName(),
                    metadata.getDatabaseProductVersion(),
                    consultarVersionPostgis(connection)
            );
        } catch (SQLException exception) {
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "No fue posible validar la conexion con la base de datos.",
                    exception
            );
        }
    }

    private String obtenerNombrePool() {
        if (dataSource instanceof HikariDataSource hikariDataSource) {
            return hikariDataSource.getPoolName();
        }
        return "No identificado";
    }

    private String consultarVersionPostgis(java.sql.Connection connection) throws SQLException {
        try (var statement = connection.prepareStatement("SELECT postgis_version()");
             var resultSet = statement.executeQuery()) {
            resultSet.next();
            return resultSet.getString(1);
        }
    }

    public record DatabaseStatusResponse(
            boolean conexionValida,
            String datasource,
            String pool,
            String motor,
            String version,
            String postgisVersion
    ) {
    }
}
