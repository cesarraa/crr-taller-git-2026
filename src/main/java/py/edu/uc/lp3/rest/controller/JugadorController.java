package py.edu.uc.lp3.rest.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import py.edu.uc.lp3.domain.Jugador;
import py.edu.uc.lp3.rest.dto.JugadorRequest;
import py.edu.uc.lp3.service.JugadorService;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/jugadores")
public class JugadorController {

    private final JugadorService jugadorService;

    public JugadorController(JugadorService jugadorService) {
        this.jugadorService = jugadorService;
    }

    @GetMapping
    public ResponseEntity<List<Jugador>> listarJugadores() {
        return ResponseEntity.ok(jugadorService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Jugador> obtenerJugadorPorId(@PathVariable Long id) {
        return ResponseEntity.ok(jugadorService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<Jugador> crearJugador(@RequestBody JugadorRequest jugador) {
        Jugador guardado = jugadorService.crear(jugador);
        return ResponseEntity.created(URI.create("/api/jugadores/" + guardado.getId())).body(guardado);
    }
}
