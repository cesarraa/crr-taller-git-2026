package py.edu.uc.lp3.rest.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import py.edu.uc.lp3.service.ResultadoSimulacion;
import py.edu.uc.lp3.service.SimulacionService;

import java.util.Map;

/**
 * Controlador REST para la simulación persistida de reglas de juego de Counter-Strike 2.
 * Expone la simulación que articula las 3 clases del modelado (Francotirador, Pistola y Jugador),
 * ejecutando las dos reglas de negocio de daño balístico, consumo de munición e impacto vital.
 *
 * Endpoints:
 * - POST /api/simulacion/combate : Ejecuta la simulación con persistencia en H2.
 * - GET  /api/simulacion/estado  : Muestra el estado persistido resultante en la BD.
 */
@RestController
@RequestMapping("/api/simulacion")
public class SimulacionController {

    private final SimulacionService simulacionService;

    public SimulacionController(SimulacionService simulacionService) {
        this.simulacionService = simulacionService;
    }

    /**
     * Ejecuta la simulación de combate CS2.
     * Puede recibir parámetros opcionales por query o body, o correr el caso canónico predeterminado.
     * POST /api/simulacion/combate
     */
    @PostMapping("/combate")
    public ResponseEntity<ResultadoSimulacion> simularCombate(
            @RequestParam(required = false) Long francotiradorId,
            @RequestParam(required = false) Long pistolaId,
            @RequestParam(required = false) Long jugadorId,
            @RequestParam(required = false, defaultValue = "45") Integer distancia,
            @RequestParam(required = false, defaultValue = "true") Boolean headshot) {

        ResultadoSimulacion resultado = simulacionService.ejecutarSimulacionCombate(
                francotiradorId, pistolaId, jugadorId, distancia, headshot);

        return ResponseEntity.ok(resultado);
    }

    /**
     * Consulta el estado persistido de los recursos involucrados en la simulación.
     * GET /api/simulacion/estado
     */
    @GetMapping("/estado")
    public ResponseEntity<Map<String, Object>> obtenerEstado() {
        return ResponseEntity.ok(simulacionService.obtenerEstadoSimulacion());
    }
}
