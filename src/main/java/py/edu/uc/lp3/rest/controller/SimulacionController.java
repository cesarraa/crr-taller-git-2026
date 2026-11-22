package py.edu.uc.lp3.rest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import py.edu.uc.lp3.service.ResultadoSimulacion;
import py.edu.uc.lp3.service.SimulacionService;

import java.util.Map;

@RestController
@RequestMapping("/api/simulacion")
@Tag(name = "Simulación", description = "Combate persistido que aplica la regla 1 (daño y munición) y la regla 2 (salud del jugador).")
public class SimulacionController {

    private final SimulacionService simulacionService;

    public SimulacionController(SimulacionService simulacionService) {
        this.simulacionService = simulacionService;
    }

    @PostMapping("/combate")
    @Operation(summary = "Simular combate", description = "Dispara un francotirador y una pistola sobre un jugador, consume munición y reduce la salud. Parámetros omitidos usan los ejemplos persistidos. Responde 409 si falta munición.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Simulación ejecutada y persistida"),
            @ApiResponse(responseCode = "404", description = "Arma o jugador no encontrado"),
            @ApiResponse(responseCode = "409", description = "Munición agotada en alguna de las armas")
    })
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

    @GetMapping("/estado")
    @Operation(summary = "Estado de la simulación", description = "Devuelve los recursos persistidos después de la simulación para comprobar munición y salud en H2.")
    @ApiResponse(responseCode = "200", description = "Estado obtenido correctamente")
    public ResponseEntity<Map<String, Object>> obtenerEstado() {
        return ResponseEntity.ok(simulacionService.obtenerEstadoSimulacion());
    }
}
