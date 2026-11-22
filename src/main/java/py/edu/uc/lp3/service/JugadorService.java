package py.edu.uc.lp3.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import py.edu.uc.lp3.domain.Jugador;
import py.edu.uc.lp3.exception.RecursoNoEncontradoException;
import py.edu.uc.lp3.exception.ReglaNegocioException;
import py.edu.uc.lp3.repository.JugadorRepository;
import py.edu.uc.lp3.rest.dto.JugadorRequest;

import java.util.List;

@Service
public class JugadorService {

    public static final int SALUD_MAXIMA = 100;
    public static final int DINERO_MAXIMO = 16000;

    private final JugadorRepository jugadorRepository;

    public JugadorService(JugadorRepository jugadorRepository) {
        this.jugadorRepository = jugadorRepository;
    }

    @Transactional(readOnly = true)
    public List<Jugador> listarTodas() {
        return jugadorRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Jugador buscarPorId(Long id) {
        if (id == null || id <= 0) {
            throw new ReglaNegocioException("El ID del jugador debe ser un número positivo.");
        }
        return jugadorRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el jugador con ID: " + id));
    }

    @Transactional(readOnly = true)
    public Jugador primerJugador() {
        return jugadorRepository.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new RecursoNoEncontradoException("No hay jugadores persistidos."));
    }

    @Transactional
    public Jugador crear(JugadorRequest req) {
        if (req == null) {
            throw new ReglaNegocioException("Los datos del jugador no pueden ser nulos.");
        }
        validarDatos(req);
        Jugador jugador = new Jugador(
                req.getAlias().trim(),
                req.getBando().trim(),
                req.getSalud(),
                req.getDinero());
        return jugadorRepository.save(jugador);
    }

    public void validarDatos(JugadorRequest req) {
        if (req.getAlias() == null || req.getAlias().isBlank()) {
            throw new ReglaNegocioException("El alias del jugador no puede estar vacío.");
        }
        if (req.getBando() == null || req.getBando().isBlank()) {
            throw new ReglaNegocioException("El bando del jugador no puede estar vacío.");
        }
        if (req.getSalud() == null || req.getSalud() < 0 || req.getSalud() > SALUD_MAXIMA) {
            throw new ReglaNegocioException("La salud debe estar comprendida entre 0 y " + SALUD_MAXIMA + ".");
        }
        if (req.getDinero() == null || req.getDinero() < 0 || req.getDinero() > DINERO_MAXIMO) {
            throw new ReglaNegocioException("El dinero debe estar comprendido entre $0 y $" + DINERO_MAXIMO + ".");
        }
    }

    public void validarSalud(int salud) {
        if (salud < 0 || salud > SALUD_MAXIMA) {
            throw new ReglaNegocioException("La salud debe estar comprendida entre 0 y " + SALUD_MAXIMA + ".");
        }
    }
}
