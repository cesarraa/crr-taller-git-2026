package py.edu.uc.lp3.rest.controller;

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
public class ArmaCrudController {

    private final ArmaService armaService;

    public ArmaCrudController(ArmaService armaService) {
        this.armaService = armaService;
    }

    @GetMapping
    public ResponseEntity<List<Arma>> listarArmas() {
        return ResponseEntity.ok(armaService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Arma> obtenerArmaPorId(@PathVariable Long id) {
        return ResponseEntity.ok(armaService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<Arma> crearArma(@RequestBody ArmaRequest nuevaArma) {
        Arma armaGuardada = armaService.crear(nuevaArma);
        URI location = URI.create("/api/armas/" + armaGuardada.getId());
        return ResponseEntity.created(location).body(armaGuardada);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Arma> actualizarArma(@PathVariable Long id, @RequestBody ArmaRequest datosActualizados) {
        return ResponseEntity.ok(armaService.actualizar(id, datosActualizados));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Arma> actualizarParcial(@PathVariable Long id, @RequestBody Map<String, Object> campos) {
        return ResponseEntity.ok(armaService.actualizarParcial(id, campos));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> eliminarArma(@PathVariable Long id) {
        armaService.eliminar(id);
        Map<String, Object> respuesta = new LinkedHashMap<>();
        respuesta.put("status", "EXITOSO");
        respuesta.put("mensaje", "Arma con ID " + id + " eliminada correctamente de la base de datos.");
        return ResponseEntity.ok(respuesta);
    }

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
