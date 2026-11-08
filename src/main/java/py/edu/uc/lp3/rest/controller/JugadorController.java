package py.edu.uc.lp3.rest.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import py.edu.uc.lp3.domain.Jugador;
import py.edu.uc.lp3.exception.RecursoNoEncontradoException;
import py.edu.uc.lp3.repository.JugadorRepository;

import java.net.URI;
import java.util.List;

/**
 * Controlador REST para la consulta y registro de Jugadores persistidos en H2.
 */
@RestController
@RequestMapping("/api/jugadores")
public class JugadorController {

    private final JugadorRepository jugadorRepository;

    public JugadorController(JugadorRepository jugadorRepository) {
        this.jugadorRepository = jugadorRepository;
    }

    @GetMapping
    public ResponseEntity<List<Jugador>> listarJugadores() {
        return ResponseEntity.ok(jugadorRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Jugador> obtenerJugadorPorId(@PathVariable Long id) {
        Jugador jugador = jugadorRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el jugador con ID: " + id));
        return ResponseEntity.ok(jugador);
    }

    @PostMapping
    public ResponseEntity<Jugador> crearJugador(@RequestBody Jugador jugador) {
        jugador.setId(null);
        Jugador guardado = jugadorRepository.save(jugador);
        return ResponseEntity.created(URI.create("/api/jugadores/" + guardado.getId())).body(guardado);
    }
}
