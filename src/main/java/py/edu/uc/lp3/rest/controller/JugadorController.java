package py.edu.uc.lp3.rest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import py.edu.uc.lp3.domain.Jugador;
import py.edu.uc.lp3.rest.dto.JugadorRequest;
import py.edu.uc.lp3.service.JugadorService;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/jugadores")
@Tag(name = "Jugadores", description = "CRUD de combatientes usados por la simulación y el escenario de APIs cruzadas.")
public class JugadorController {

    private final JugadorService jugadorService;

    public JugadorController(JugadorService jugadorService) {
        this.jugadorService = jugadorService;
    }

    @GetMapping
    @Operation(summary = "Listar jugadores", description = "Devuelve todos los jugadores persistidos en H2.")
    @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente")
    public ResponseEntity<List<Jugador>> listarJugadores() {
        return ResponseEntity.ok(jugadorService.listarTodas());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar jugador por ID", description = "Devuelve un jugador persistido; falla con 404 si el ID no existe.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Jugador encontrado"),
            @ApiResponse(responseCode = "404", description = "Jugador no encontrado")
    })
    public ResponseEntity<Jugador> obtenerJugadorPorId(@PathVariable Long id) {
        return ResponseEntity.ok(jugadorService.buscarPorId(id));
    }

    @PostMapping
    @Operation(summary = "Crear jugador", description = "Valida alias no vacío, salud 0..100 y dinero 0..16000 en JugadorService.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Jugador creado"),
            @ApiResponse(responseCode = "400", description = "Regla de negocio violada")
    })
    public ResponseEntity<Jugador> crearJugador(@RequestBody JugadorRequest jugador) {
        Jugador guardado = jugadorService.crear(jugador);
        return ResponseEntity.created(URI.create("/api/jugadores/" + guardado.getId())).body(guardado);
    }
}
