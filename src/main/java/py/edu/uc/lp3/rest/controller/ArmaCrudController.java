package py.edu.uc.lp3.rest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import py.edu.uc.lp3.domain.Arma;
import py.edu.uc.lp3.rest.dto.ArmaRequest;
import py.edu.uc.lp3.service.ArmaService;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/armas")
@Tag(name = "Armas", description = "CRUD REST de la familia Arma con discriminación polimórfica por 'tipo'.")
public class ArmaCrudController {

    private final ArmaService armaService;

    public ArmaCrudController(ArmaService armaService) {
        this.armaService = armaService;
    }

    @GetMapping
    @Operation(summary = "Listar armas", description = "Devuelve todas las armas persistidas en H2 con un único campo 'tipo'.")
    @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente")
    public ResponseEntity<List<Arma>> listarArmas() {
        return ResponseEntity.ok(armaService.listarTodas());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar arma por ID", description = "Devuelve un arma persistida; falla con 404 si el ID no existe.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Arma encontrada"),
            @ApiResponse(responseCode = "404", description = "No existe un arma con ese ID")
    })
    public ResponseEntity<Arma> obtenerArmaPorId(@PathVariable Long id) {
        return ResponseEntity.ok(armaService.buscarPorId(id));
    }

    @PostMapping
    @Operation(summary = "Crear arma", description = "Crea el subtipo indicado por 'tipo'. Precio fuera de 0..16000 responde 422; otras reglas rotas responden 400.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Arma creada"),
            @ApiResponse(responseCode = "400", description = "Regla de negocio violada"),
            @ApiResponse(responseCode = "422", description = "Precio fuera del rango permitido")
    })
    public ResponseEntity<Arma> crearArma(@RequestBody ArmaRequest nuevaArma) {
        Arma armaGuardada = armaService.crear(nuevaArma);
        URI location = URI.create("/api/armas/" + armaGuardada.getId());
        return ResponseEntity.created(location).body(armaGuardada);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Reemplazar arma", description = "Actualización completa: exige todos los campos obligatorios y aplica las mismas validaciones que POST.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Arma reemplazada"),
            @ApiResponse(responseCode = "400", description = "Regla de negocio violada"),
            @ApiResponse(responseCode = "404", description = "Arma no encontrada"),
            @ApiResponse(responseCode = "422", description = "Precio fuera del rango permitido")
    })
    public ResponseEntity<Arma> actualizarArma(@PathVariable Long id, @RequestBody ArmaRequest datosActualizados) {
        return ResponseEntity.ok(armaService.actualizar(id, datosActualizados));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Actualizar arma parcialmente", description = "Modifica solo las claves enviadas. Permite dejar municionActual en 0 para provocar el conflicto de la regla 1.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Arma actualizada"),
            @ApiResponse(responseCode = "400", description = "Regla de negocio violada"),
            @ApiResponse(responseCode = "404", description = "Arma no encontrada"),
            @ApiResponse(responseCode = "422", description = "Precio fuera del rango permitido")
    })
    public ResponseEntity<Arma> actualizarParcial(@PathVariable Long id, @RequestBody Map<String, Object> campos) {
        return ResponseEntity.ok(armaService.actualizarParcial(id, campos));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar arma", description = "Borra físicamente el arma de H2.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Arma eliminada"),
            @ApiResponse(responseCode = "404", description = "Arma no encontrada")
    })
    public ResponseEntity<Map<String, Object>> eliminarArma(@PathVariable Long id) {
        armaService.eliminar(id);
        Map<String, Object> respuesta = new LinkedHashMap<>();
        respuesta.put("status", "EXITOSO");
        respuesta.put("mensaje", "Arma con ID " + id + " eliminada correctamente de la base de datos.");
        return ResponseEntity.ok(respuesta);
    }

    @PostMapping("/{id}/disparar")
    @Operation(summary = "Disparar arma (regla 1)", description = "Descuenta una bala y calcula el daño efectivo. Responde 409 con CS2_ARMA_SIN_MUNICION cuando el cargador está vacío.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Disparo resuelto y munición persistida"),
            @ApiResponse(responseCode = "404", description = "Arma no encontrada"),
            @ApiResponse(responseCode = "409", description = "Munición agotada")
    })
    public ResponseEntity<Map<String, Object>> dispararArma(
            @PathVariable Long id,
            @RequestParam(required = false) Integer distancia,
            @RequestParam(required = false) Boolean headshot) {

        String resultadoDisparo = armaService.dispararArma(id, distancia, headshot);
        Arma armaActualizada = armaService.buscarPorId(id);

        Map<String, Object> respuesta = new LinkedHashMap<>();
        respuesta.put("status", "OK");
        respuesta.put("id", id);
        respuesta.put("arma", armaActualizada.getNombre());
        respuesta.put("tipo", armaActualizada.getTipo());
        respuesta.put("resultado", resultadoDisparo);
        respuesta.put("municionRestante", armaActualizada.getMunicionActual());
        respuesta.put("municionMax", armaActualizada.getMunicionMax());
        return ResponseEntity.ok(respuesta);
    }

    @PostMapping("/{id}/recargar")
    @Operation(summary = "Recargar arma", description = "Llena el cargador al 100% en H2.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Arma recargada"),
            @ApiResponse(responseCode = "404", description = "Arma no encontrada")
    })
    public ResponseEntity<Map<String, Object>> recargarArma(@PathVariable Long id) {
        Arma armaActualizada = armaService.recargarArma(id);

        Map<String, Object> respuesta = new LinkedHashMap<>();
        respuesta.put("status", "OK");
        respuesta.put("id", id);
        respuesta.put("arma", armaActualizada.getNombre());
        respuesta.put("mensaje", "Arma recargada al 100% de su capacidad.");
        respuesta.put("municionActual", armaActualizada.getMunicionActual());
        respuesta.put("municionMax", armaActualizada.getMunicionMax());
        return ResponseEntity.ok(respuesta);
    }
}
