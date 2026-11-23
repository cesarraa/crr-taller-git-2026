package py.edu.uc.lp3.rest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import py.edu.uc.lp3.domain.EscenarioValidado;
import py.edu.uc.lp3.rest.dto.EscenarioRequest;
import py.edu.uc.lp3.service.EscenarioService;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/escenarios")
@Tag(name = "Escenarios cruzados", description = "Jugada propia que solo se persiste si A2 y A3 aceptan sus validaciones.")
public class EscenarioController {

    private final EscenarioService escenarioService;

    public EscenarioController(EscenarioService escenarioService) {
        this.escenarioService = escenarioService;
    }

    @PostMapping
    @Operation(summary = "Ejecutar escenario", description = "Llama a A2 y luego a A3 con los datos del arma y del jugador locales. Si ambos aceptan, guarda la jugada en H2; si uno rechaza, responde 422 identificando el origen y no guarda nada.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Escenario aceptado por A2 y A3 y persistido"),
            @ApiResponse(responseCode = "400", description = "Datos locales inválidos"),
            @ApiResponse(responseCode = "404", description = "Arma o jugador local no encontrado"),
            @ApiResponse(responseCode = "422", description = "Rechazo de A2 o A3, identificado en el cuerpo",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(type = "object",
                                    example = "{\"status\":422,\"codigo\":\"CS2_VALIDACION_A3_RECHAZADA\",\"origen\":\"A3\",\"statusExterno\":422,\"cuerpoExterno\":\"{\\\"motivo\\\":\\\"economia\\\"}\",\"mensaje\":\"La validación de la API A3 rechazó el escenario (HTTP 422).\"}")))
    })
    public ResponseEntity<EscenarioValidado> ejecutar(@RequestBody EscenarioRequest req) {
        EscenarioValidado guardado = escenarioService.ejecutar(req);
        return ResponseEntity.created(URI.create("/api/escenarios/" + guardado.getId())).body(guardado);
    }

    @GetMapping
    @Operation(summary = "Listar escenarios guardados", description = "Devuelve las jugadas que pasaron las dos validaciones ajenas.")
    @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente")
    public ResponseEntity<List<EscenarioValidado>> listar() {
        return ResponseEntity.ok(escenarioService.listarTodos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar escenario por ID", description = "Devuelve una jugada previamente persistida.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Escenario encontrado"),
            @ApiResponse(responseCode = "400", description = "ID inválido"),
            @ApiResponse(responseCode = "404", description = "Escenario inexistente")
    })
    public ResponseEntity<EscenarioValidado> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(escenarioService.buscarPorId(id));
    }
}
