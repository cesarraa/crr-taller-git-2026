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

/**
 * Servicio de Simulación del juego Counter-Strike 2.
 * Orquesta la simulación persistida articulando las 3 clases del modelado:
 * Francotirador (hija 1 de Arma), Pistola (hija 2 de Arma) y Jugador (otra familia).
 *
 * Implementa dos reglas de negocio de juego:
 * - Regla 1 (Balística y Munición): El disparo consume balas y calcula daño efectivo;
 *   si no hay munición, lanza MunicionAgotadaException.
 * - Regla 2 (Impacto y Salud): El combatiente recibe el daño acumulado hasta el piso de 0,
 *   afectando su estado vital (estaVivo).
 *
 * Los resultados se persisten en la base de datos relacional H2.
 */
@Service
public class SimulacionService {

    private final ArmaRepository armaRepository;
    private final JugadorRepository jugadorRepository;

    public SimulacionService(ArmaRepository armaRepository, JugadorRepository jugadorRepository) {
        this.armaRepository = armaRepository;
        this.jugadorRepository = jugadorRepository;
    }

    /**
     * Ejecuta una simulación completa de ronda de combate CS2 y persiste el resultado.
     */
    @Transactional
    public ResultadoSimulacion ejecutarSimulacionCombate(Long francotiradorId, Long pistolaId, Long jugadorId,
                                                         Integer distanciaMetros, Boolean tiroALaCabeza) {
        int distancia = (distanciaMetros != null && distanciaMetros > 0) ? distanciaMetros : 35;
        boolean headshot = (tiroALaCabeza != null) ? tiroALaCabeza : true;

        Francotirador francotirador = obtenerFrancotirador(francotiradorId);
        Pistola pistola = obtenerPistola(pistolaId);
        Jugador jugador = obtenerJugador(jugadorId);

        ResultadoSimulacion resultado = new ResultadoSimulacion();
        resultado.setDescripcion("Simulación balística de combate CS2: enfrentamiento táctico contra combatiente.");
        resultado.setRegla1Aplicada("Regla 1 (Balística y Munición): Al disparar, el arma consume munición del cargador y calcula el daño efectivo con atenuación y multiplicadores; si la munición llega a cero, el arma bloquea el disparo con MunicionAgotadaException.");
        resultado.setRegla2Aplicada("Regla 2 (Impacto y Salud Vital): El Jugador reduce sus puntos de salud según el daño balístico recibido hasta un piso de cero (salud = Math.max(0, salud - danio)), quedando eliminado si la salud llega a cero.");

        // 1. Participante Francotirador
        resultado.setFrancotiradorId(francotirador.getId());
        resultado.setFrancotiradorNombre(francotirador.getNombre());
        resultado.setFrancotiradorMunicionAntes(francotirador.getMunicionActual());

        int danioFrancotirador = francotirador.calcularDanioEfectivo(distancia, headshot);
        francotirador.disparar(distancia, headshot); // Disparo de dominio y consumo de munición
        resultado.setFrancotiradorMunicionDespues(francotirador.getMunicionActual());
        resultado.setFrancotiradorDanioInfligido(danioFrancotirador);

        // 2. Participante Pistola
        resultado.setPistolaId(pistola.getId());
        resultado.setPistolaNombre(pistola.getNombre());
        resultado.setPistolaMunicionAntes(pistola.getMunicionActual());

        int distanciaPistola = Math.min(distancia, 15);
        int danioPistola = pistola.calcularDanioEfectivo(distanciaPistola, false);
        pistola.disparar(distanciaPistola, false); // Disparo de dominio y consumo de munición
        resultado.setPistolaMunicionDespues(pistola.getMunicionActual());
        resultado.setPistolaDanioInfligido(danioPistola);

        // 3. Participante Jugador
        resultado.setJugadorId(jugador.getId());
        resultado.setJugadorAlias(jugador.getAlias());
        resultado.setJugadorSaludAntes(jugador.getSalud());

        int danioTotal = danioFrancotirador + danioPistola;
        jugador.recibirDanio(danioTotal); // Aplicación de Regla 2
        resultado.setDanioTotalRecibido(danioTotal);
        resultado.setJugadorSaludDespues(jugador.getSalud());
        resultado.setJugadorSobrevive(jugador.estaVivo());

        // 4. Persistencia garantizada en H2
        armaRepository.save(francotirador);
        armaRepository.save(pistola);
        jugadorRepository.save(jugador);

        return resultado;
    }

    /**
     * Consulta el estado persistido actual de los recursos que participan de la simulación.
     */
    @Transactional(readOnly = true)
    public Map<String, Object> obtenerEstadoSimulacion() {
        Map<String, Object> estado = new HashMap<>();
        estado.put("totalArmasPersistidas", armaRepository.count());
        estado.put("totalJugadoresPersistidos", jugadorRepository.count());
        estado.put("armas", armaRepository.findAll());
        estado.put("jugadores", jugadorRepository.findAll());
        return estado;
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
        // Buscar el primer Francotirador disponible o crear uno por defecto
        List<Arma> todas = armaRepository.findAll();
        for (Arma a : todas) {
            if (a instanceof Francotirador f) {
                return f;
            }
        }
        return armaRepository.save(new Francotirador("AWP", 4750f, 115, 4.3f, 10, 2));
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
        List<Arma> todas = armaRepository.findAll();
        for (Arma a : todas) {
            if (a instanceof Pistola p) {
                return p;
            }
        }
        return armaRepository.save(new Pistola("Desert Eagle", 700f, 53, 1.8f, 7, "Semiautomático", 7));
    }

    private Jugador obtenerJugador(Long id) {
        if (id != null) {
            return jugadorRepository.findById(id)
                    .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el Jugador con ID: " + id));
        }
        return jugadorRepository.findAll().stream().findFirst()
                .orElseGet(() -> jugadorRepository.save(new Jugador("s1mple", "Antiterrorista", 100, 16000)));
    }
}
