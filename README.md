# Fundación Aseo API 🚀

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.5-green.svg)](https://spring.io/projects/spring-boot)
[![PostGIS](https://img.shields.io/badge/PostGIS-Enabled-blue.svg)](https://postgis.net/)
[![Java 17](https://img.shields.io/badge/Java-17-orange.svg)](https://openjdk.org/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

API RESTful para la gestión de **reportes de puntos críticos de residuos** en el municipio de Fundación, Magdalena.
Permite a ciudadanos reportar puntos contaminados con ubicación geográfica, descripción y foto; y a administradores visualizarlos en mapa interactivo, actualizar estados y calcular rutas óptimas de recolección.

---

## 📁 Estructura del Proyecto

```
fundacion-aseo-api/
├── pom.xml                              # Dependencias Maven (Spring Boot, PostGIS, Lombok, PG)
├── docker-compose-local.yml             # Docker PostgreSQL/PostGIS local
├── init-local-db.sql                    # Script de inicialización de la DB local
├── src/
│   ├── main/
│   │   ├── java/com/fundacion/aseo/
│   │   │   ├── FundacionAseoApiApplication.java   # Clase principal (@SpringBootApplication)
│   │   │   ├── controllers/
│   │   │   │   ├── ReportePuntoCriticoController.java  # CRUD reportes
│   │   │   │   ├── RutaOptimaController.java           # POST /api/admin/ruta-optima
│   │   │   │   └── DatabaseStatusController.java       # GET /api/sistema/db/estado
│   │   │   ├── entities/
│   │   │   │   ├── EstadoReporte.java             # Enum: PENDIENTE, COMPLETADO
│   │   │   │   └── ReportePuntoCritico.java       # @Entity principal
│   │   │   ├── repositories/
│   │   │   │   └── ReportePuntoCriticoRepository.java
│   │   │   └── services/
│   │   │       └── ReportePuntoCriticoService.java  # Lógica de negocio + MapMarker
│   │   └── resources/
│   │       ├── application.properties             # Config producción (env vars)
│   │       ├── application-local.properties       # Config local Docker (puerto 5433)
│   │       ├── schema.sql                         # DDL tabla reportes
│   │       ├── data.sql                           # 7 reportes de prueba (local)
│   │       └── static/
│   │           ├── reporte-ciudadano.html         # Formulario ciudadano
│   │           └── mapa-admin.html                # Mapa Leaflet admin
│   └── test/
│       └── java/com/fundacion/aseo/
│           └── FundacionAseoApiApplicationTests.java
```

---

## 🏗️ Arquitectura

**Patrón**: MVC con Spring WebMVC y Clean Architecture en capas.

- **Presentation** (Controllers): Manejo HTTP, validación de entradas.
- **Business Logic** (Services): CRUD, cálculo de ruta óptima, serialización de imágenes a Base64.
- **Persistence** (Repositories + Entities): Spring Data JPA + PostGIS (PostgreSQL).
- **View** (Static): HTML + Leaflet JS servido por Spring Boot.

**Perfiles de configuración**:
- `application.properties` — Producción (PostgreSQL configurable vía env vars).
- `application-local.properties` — Local con Docker PostGIS en puerto 5433. Activar con `-Dspring.profiles.active=local`.

---

## 🚀 Quick Start

### Requisitos
- Java 17+, Maven 3.9+
- Docker (para DB local)

### Pasos

```bash
# 1. Clonar el repositorio
git clone https://github.com/tu-usuario/fundacion-aseo-api.git
cd fundacion-aseo-api

# 2. Iniciar la base de datos local con Docker
docker compose -f docker-compose-local.yml up -d

# 3. Compilar
mvn clean compile

# 4. Ejecutar con perfil local
mvn spring-boot:run -Dspring.profiles.active=local

# Servidor disponible en: http://localhost:8080
```

### Producción (PostgreSQL externo / Supabase)

Configura las siguientes variables de entorno y ejecuta sin perfil:

```bash
export SPRING_DATASOURCE_URL=jdbc:postgresql://<host>:<puerto>/<db>
export SPRING_DATASOURCE_USERNAME=<usuario>
export SPRING_DATASOURCE_PASSWORD=<contraseña>
mvn spring-boot:run
```

---

## 🌐 Interfaces Web

| URL | Descripción |
|-----|-------------|
| `http://localhost:8080/reporte-ciudadano.html` | Formulario para ciudadanos (foto + coordenadas) |
| `http://localhost:8080/mapa-admin.html` | Mapa admin con marcadores por estado |

El mapa admin muestra marcadores **rojos** (PENDIENTE) y **azules** (COMPLETADO). Haciendo clic en un marcador se abre un popup con la foto del reporte y un botón para marcarlo como completado.

---

## 📡 API Endpoints

Base URL: `http://localhost:8080/api`

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `POST` | `/reportes` | Crear reporte (multipart: `imagen`, `latitud`, `longitud`) |
| `GET` | `/reportes` | Listar reportes paginados |
| `GET` | `/reportes/admin/reportes-mapa?estado=PENDIENTE` | Marcadores para mapa (con imagen Base64) |
| `PATCH` | `/reportes/{id}/estado` | Actualizar estado (`{"estado": "COMPLETADO"}`) |
| `POST` | `/admin/ruta-optima` | Calcular ruta óptima por IDs |
| `GET` | `/sistema/db/estado` | Health check DB + versión PostGIS |

**Ejemplo — crear reporte:**
```bash
curl -X POST http://localhost:8080/api/reportes \
  -F "imagen=@foto.jpg" \
  -F "latitud=10.98" \
  -F "longitud=-74.78"
```

**Ejemplo — actualizar estado:**
```bash
curl -X PATCH http://localhost:8080/api/reportes/1/estado \
  -H "Content-Type: application/json" \
  -d '{"estado": "COMPLETADO"}'
```

---

## 🧪 Tests

```bash
mvn test
```

---

## 🔧 Troubleshooting

| Problema | Solución |
|----------|----------|
| Error de conexión DB local | Verificar `docker ps` y que el contenedor PostGIS esté corriendo |
| No aparecen marcadores en el mapa | Confirmar que `data.sql` se ejecutó (`spring.sql.init.mode=always` en perfil local) |
| Error PostGIS | Verificar `/api/sistema/db/estado` |
| Error al compilar | Ejecutar `mvn clean install` |
| Foto no visible en popup | Verificar que la imagen se guardó correctamente en la DB |

---

## 👤 Autor

**Jainer Leonardo Pabón Borja**  
Universidad Nacional Abierta y a Distancia — UNAD
