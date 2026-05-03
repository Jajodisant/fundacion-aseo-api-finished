# TODO: Implementación de Cálculo de Ruta Óptima (TSP MVP)

Estado: En progreso

## Pasos:

### 1. [x] Implementar funciones helper en ReportePuntoCriticoService
   - haversineDistance(double lat1, double lon1, double lat2, double lon2) -> double (en km)
   - calcularRutaOptima(double depotLat, double depotLon, List<Long> reportIds) -> List<ReportePuntoCritico> ordenados

### 2. [x] Editar/crear archivos según plan
   - Actualizar ReportePuntoCriticoService.java con nueva lógica TSP (DP para n<=11)
   - Crear RutaOptimaController.java con POST /api/admin/ruta-optima
   - Definir DTO RutaOptimaResponse

### 3. [x] Verificar compilación: mvn clean compile (SUCCESS)

### 4. [ ] Agregar test unitario básico para TSP (optional MVP)

### 5. [ ] Probar: mvn spring-boot:run + curl/Postman POST /api/admin/ruta-optima

## Notas:
- TSP DP: O(2^n * n^2), ok para n=10
- Usa lat/lon de entities
- Input: depot + report IDs
- Output: lista ordenada de reports (excluye depot)

