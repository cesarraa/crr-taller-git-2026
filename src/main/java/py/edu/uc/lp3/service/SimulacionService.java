package py.edu.uc.lp3.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import py.edu.uc.lp3.domain.Arma;
import py.edu.uc.lp3.domain.Francotirador;
import py.edu.uc.lp3.domain.Jugador;
import py.edu.uc.lp3.domain.Pistola;
import py.edu.uc.lp3.exception.RecursoNoEncontradoException;
import py.edu.uc.lp3.exception.ReglaNegocioException;
import py.edu.uc.lp3.repository.ArmaRepository;
import py.edu.uc.lp3.repository.JugadorRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SimulacionService {

    private final ArmaRepository armaRepository;
    private final JugadorRepository jugadorRepository;
    private final ArmaService armaService;
    private final JugadorService jugadorService;

    public SimulacionService(ArmaRepository armaRepository, JugadorRepository jugadorRepository,
                             ArmaService armaService, JugadorService jugadorService) {
        this.armaRepository = armaRepository;
        this.jugadorRepository = jugadorRepository;
        this.armaService = armaService;
        this.jugadorService = jugadorService;
    }

    @Transactional
    public ResultadoSimulacion ejecutarSimulacionCombate(Long francotiradorId, Long pistolaId, Long jugadorId,
                                                          Integer distanciaMetros, Boolean tiroALaCabeza) {
        int distancia = (distanciaMetros != null && distanciaMetros > 0) ? distanciaMetros : 35;
        boolean headshot = Boolean.TRUE.equals(tiroALaCabeza);

        Francotirador francotirador = obtenerFrancotirador(francotiradorId);
        Pistola pistola = obtenerPistola(pistolaId);
        Jugador jugador = obtenerJugador(jugadorId);

        ResultadoSimulacion resultado = new ResultadoSimulacion();
        resultado.setDescripcion("Simulación balística de combate CS2: enfrentamiento táctico contra combatiente.");
        resultado.setRegla1Aplicada("Regla 1 (Balística y Munición): Al disparar, el arma consume munición del cargador y calcula el daño efectivo con atenuación y multiplicadores; si la munición llega a cero, el arma bloquea el disparo con MunicionAgotadaException.");
        resultado.setRegla2Aplicada("Regla 2 (Impacto y Salud Vital): El Jugador reduce sus puntos de salud según el daño balístico recibido hasta un piso de cero, quedando eliminado si la salud llega a cero.");

        resultado.setFrancotiradorId(francotirador.getId());
        resultado.setFrancotiradorNombre(francotirador.getNombre());
        resultado.setFrancotiradorMunicionAntes(francotirador.getMunicionActual());

        int danioFrancotirador = armaService.calcularDanioEfectivo(francotirador, distancia, headshot);
        armaService.consumirMunicion(francotirador);
        resultado.setFrancotiradorMunicionDespues(francotirador.getMunicionActual());
        resultado.setFrancotiradorDanioInfligido(danioFrancotirador);

        resultado.setPistolaId(pistola.getId());
        resultado.setPistolaNombre(pistola.getNombre());
        resultado.setPistolaMunicionAntes(pistola.getMunicionActual());

        int distanciaPistola = Math.min(distancia, 15);
        int danioPistola = armaService.calcularDanioEfectivo(pistola, distanciaPistola, false);
        armaService.consumirMunicion(pistola);
        resultado.setPistolaMunicionDespues(pistola.getMunicionActual());
        resultado.setPistolaDanioInfligido(danioPistola);

        resultado.setJugadorId(jugador.getId());
        resultado.setJugadorAlias(jugador.getAlias());
        resultado.setJugadorSaludAntes(jugador.getSalud());

        int danioTotal = danioFrancotirador + danioPistola;
        aplicarDanio(jugador, danioTotal);
        resultado.setDanioTotalRecibido(danioTotal);
        resultado.setJugadorSaludDespues(jugador.getSalud());
        resultado.setJugadorSobrevive(jugador.getSalud() > 0);

        armaRepository.save(francotirador);
        armaRepository.save(pistola);
        jugadorRepository.save(jugador);

        return resultado;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> obtenerEstadoSimulacion() {
        Map<String, Object> estado = new HashMap<>();
        estado.put("totalArmasPersistidas", armaRepository.count());
        estado.put("totalJugadoresPersistidos", jugadorRepository.count());
        estado.put("armas", armaRepository.findAll());
        estado.put("jugadores", jugadorRepository.findAll());
        return estado;
    }

    private void aplicarDanio(Jugador jugador, int cantidad) {
        if (cantidad < 0) {
            throw new ReglaNegocioException("El daño recibido no puede ser negativo.");
        }
        int saludFinal = Math.max(0, jugador.getSalud() - cantidad);
        jugadorService.validarSalud(saludFinal);
        jugador.setSalud(saludFinal);
    }

    private Francotirador obtenerFrancotirador(Long id) {
        if (id != null) {
            Arma arma = armaRepository.findById(id)
                    .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el Francotirador con ID: " + id));
            if (arma instanceof Francotirador f) {
                return f;
            }
            throw new ReglaNegocioException("El arma con ID " + id + " no es de tipo Francotirador.");
        }
        return armaService.primerFrancotirador();
    }

    private Pistola obtenerPistola(Long id) {
        if (id != null) {
            Arma arma = armaRepository.findById(id)
                    .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró la Pistola con ID: " + id));
            if (arma instanceof Pistola p) {
                return p;
            }
            throw new ReglaNegocioException("El arma con ID " + id + " no es de tipo Pistola.");
        }
        return armaRepository.findAll().stream()
                .filter(Pistola.class::isInstance)
                .map(Pistola.class::cast)
                .findFirst()
                .orElseThrow(() -> new RecursoNoEncontradoException("No hay pistolas persistidas."));
    }

    private Jugador obtenerJugador(Long id) {
        if (id != null) {
            return jugadorService.buscarPorId(id);
        }
        return jugadorService.primerJugador();
    }
}
