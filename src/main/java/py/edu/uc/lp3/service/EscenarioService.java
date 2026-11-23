package py.edu.uc.lp3.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import py.edu.uc.lp3.domain.Arma;
import py.edu.uc.lp3.domain.EscenarioValidado;
import py.edu.uc.lp3.domain.Jugador;
import py.edu.uc.lp3.exception.RecursoNoEncontradoException;
import py.edu.uc.lp3.exception.ReglaNegocioException;
import py.edu.uc.lp3.repository.EscenarioValidadoRepository;
import py.edu.uc.lp3.rest.dto.EscenarioRequest;
import py.edu.uc.lp3.service.externo.ClienteA2;
import py.edu.uc.lp3.service.externo.ClienteA3;
import py.edu.uc.lp3.service.externo.ValidacionExterna;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class EscenarioService {

    private static final String NOMBRE_POR_DEFECTO = "Emboscada validada";

    private final EscenarioValidadoRepository escenarioRepository;
    private final ArmaService armaService;
    private final JugadorService jugadorService;
    private final ClienteA2 clienteA2;
    private final ClienteA3 clienteA3;

    public EscenarioService(EscenarioValidadoRepository escenarioRepository,
                            ArmaService armaService,
                            JugadorService jugadorService,
                            ClienteA2 clienteA2,
                            ClienteA3 clienteA3) {
        this.escenarioRepository = escenarioRepository;
        this.armaService = armaService;
        this.jugadorService = jugadorService;
        this.clienteA2 = clienteA2;
        this.clienteA3 = clienteA3;
    }

    @Transactional
    public EscenarioValidado ejecutar(EscenarioRequest req) {
        if (req == null) {
            throw new ReglaNegocioException("Los datos del escenario no pueden ser nulos.");
        }
        if (req.getArmaId() == null || req.getArmaId() <= 0) {
            throw new ReglaNegocioException("El ID del arma debe ser un número positivo.");
        }
        if (req.getJugadorId() == null || req.getJugadorId() <= 0) {
            throw new ReglaNegocioException("El ID del jugador debe ser un número positivo.");
        }
        int distancia = req.getDistancia() == null ? 35 : req.getDistancia();
        if (distancia < 0 || distancia > 200) {
            throw new ReglaNegocioException("La distancia debe estar entre 0 y 200 metros.");
        }
        boolean headshot = Boolean.TRUE.equals(req.getHeadshot());
        String nombre = req.getNombreEscenario() == null || req.getNombreEscenario().isBlank()
                ? NOMBRE_POR_DEFECTO
                : req.getNombreEscenario().trim();

        Arma arma = armaService.buscarPorId(req.getArmaId());
        Jugador jugador = jugadorService.buscarPorId(req.getJugadorId());

        ValidacionExterna validacionA2 = clienteA2.validar(payloadA2(nombre, arma, jugador, distancia, headshot));
        ValidacionExterna validacionA3 = clienteA3.validar(payloadA3(nombre, arma, jugador));

        EscenarioValidado escenario = new EscenarioValidado(
                nombre,
                req.getDescripcion(),
                arma.getNombre(),
                jugador.getAlias(),
                distancia,
                headshot,
                mensaje(validacionA2),
                mensaje(validacionA3),
                LocalDateTime.now());
        return escenarioRepository.save(escenario);
    }

    @Transactional(readOnly = true)
    public List<EscenarioValidado> listarTodos() {
        return escenarioRepository.findAll();
    }

    @Transactional(readOnly = true)
    public EscenarioValidado buscarPorId(Long id) {
        if (id == null || id <= 0) {
            throw new ReglaNegocioException("El ID del escenario debe ser un número positivo.");
        }
        return escenarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el escenario con ID: " + id));
    }

    private Map<String, Object> payloadA2(String nombre, Arma arma, Jugador jugador, int distancia, boolean headshot) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("escenario", nombre);
        payload.put("regla", "DANO_BALISTICO");
        payload.put("arma", arma.getNombre());
        payload.put("tipoArma", arma.getTipo());
        payload.put("dano", arma.getDano());
        payload.put("distancia", distancia);
        payload.put("headshot", headshot);
        payload.put("jugador", jugador.getAlias());
        payload.put("saludJugador", jugador.getSalud());
        return payload;
    }

    private Map<String, Object> payloadA3(String nombre, Arma arma, Jugador jugador) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("escenario", nombre);
        payload.put("regla", "MUNICION_GRANADA_LUIS");
        payload.put("arma", arma.getNombre());
        payload.put("jugador", jugador.getAlias());
        return payload;
    }

    private String mensaje(ValidacionExterna validacion) {
        if (validacion.getMensaje() == null || validacion.getMensaje().isBlank()) {
            return validacion.getCodigo() == null ? "ACEPTADO" : validacion.getCodigo();
        }
        return validacion.getMensaje();
    }
}
