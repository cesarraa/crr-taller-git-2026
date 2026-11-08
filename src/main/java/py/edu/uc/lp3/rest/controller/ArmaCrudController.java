package py.edu.uc.lp3.rest.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import py.edu.uc.lp3.domain.Arma;
import py.edu.uc.lp3.service.ArmaService;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador REST (Presentation Layer) que expone el CRUD completo para la familia Arma.
 * Respeta la arquitectura desacoplada en 3 capas delegando la lógica a ArmaService.
 * Soporta serialización y deserialización polimórfica (Arma, Francotirador, Pistola, etc.)
 * a través de discriminadores de Jackson ('tipo').
 *
 * Endpoints REST:
 * - GET    /api/armas               : Lista todas las armas persistidas (subtipos discriminados)
 * - GET    /api/armas/{id}          : Obtiene arma por su ID (o 404 Not Found)
 * - POST   /api/armas               : Crea arma polimórfica (201 Created o 400/422)
 * - PUT    /api/armas/{id}          : Actualización completa (200 OK)
 * - PATCH  /api/armas/{id}          : Actualización parcial de atributos (200 OK)
 * - DELETE /api/armas/{id}          : Elimina físicamente de H2 (200 OK)
 * - POST   /api/armas/{id}/disparar : Disparo de dominio persistido (lanza 409 si no hay balas)
 * - POST   /api/armas/{id}/recargar : Recarga de dominio persistida
 */
@RestController
@RequestMapping("/api/armas")
public class ArmaCrudController {

    private final ArmaService armaService;

    public ArmaCrudController(ArmaService armaService) {
        this.armaService = armaService;
    }

    /**
     * READ (Colección): Obtiene el listado completo de armas persistidas en H2.
     * GET /api/armas
     */
    @GetMapping
    public ResponseEntity<List<Arma>> listarArmas() {
        List<Arma> armas = armaService.listarTodas();
        return ResponseEntity.ok(armas);
    }

    /**
     * READ (Ítem): Obtiene un arma específica por su ID.
     * GET /api/armas/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Arma> obtenerArmaPorId(@PathVariable Long id) {
        Arma arma = armaService.buscarPorId(id);
        return ResponseEntity.ok(arma);
    }

    /**
     * CREATE: Registra una nueva arma en H2 (subtipo discriminado en JSON con 'tipo').
     * POST /api/armas
     */
    @PostMapping
    public ResponseEntity<Arma> crearArma(@RequestBody Arma nuevaArma) {
        Arma armaGuardada = armaService.crear(nuevaArma);
        URI location = URI.create("/api/armas/" + armaGuardada.getId());
        return ResponseEntity.created(location).body(armaGuardada);
    }

    /**
     * UPDATE (Completo): Reemplaza los atributos de un arma existente por su ID.
     * PUT /api/armas/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<Arma> actualizarArma(@PathVariable Long id, @RequestBody Arma datosActualizados) {
        Arma armaActualizada = armaService.actualizar(id, datosActualizados);
        return ResponseEntity.ok(armaActualizada);
    }

    /**
     * UPDATE (Parcial): Modifica un subconjunto específico de campos sin sobreescribir el resto.
     * PATCH /api/armas/{id}
     */
    @PatchMapping("/{id}")
    public ResponseEntity<Arma> actualizarParcial(@PathVariable Long id, @RequestBody Map<String, Object> campos) {
        Arma armaActualizada = armaService.actualizarParcial(id, campos);
        return ResponseEntity.ok(armaActualizada);
    }

    /**
     * DELETE: Elimina físicamente el arma de la base de datos H2.
     * DELETE /api/armas/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> eliminarArma(@PathVariable Long id) {
        armaService.eliminar(id);
        Map<String, Object> respuesta = new LinkedHashMap<>();
        respuesta.put("status", "EXITOSO");
        respuesta.put("mensaje", "Arma con ID " + id + " eliminada correctamente de la base de datos.");
        return ResponseEntity.ok(respuesta);
    }

    /**
     * Operación de negocio: Dispara el arma persistida en la BD.
     * POST /api/armas/{id}/disparar?distancia={m}&headshot={bool}
     */
    @PostMapping("/{id}/disparar")
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

    /**
     * Operación de negocio: Recarga la munición del arma persistida al 100%.
     * POST /api/armas/{id}/recargar
     */
    @PostMapping("/{id}/recargar")
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
