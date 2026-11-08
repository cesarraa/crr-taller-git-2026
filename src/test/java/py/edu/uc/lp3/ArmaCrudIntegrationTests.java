package py.edu.uc.lp3;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import py.edu.uc.lp3.domain.Arma;
import py.edu.uc.lp3.domain.Francotirador;
import py.edu.uc.lp3.domain.Jugador;
import py.edu.uc.lp3.domain.Pistola;
import py.edu.uc.lp3.exception.EconomiaInvalidaException;
import py.edu.uc.lp3.exception.MunicionAgotadaException;
import py.edu.uc.lp3.exception.RecursoNoEncontradoException;
import py.edu.uc.lp3.exception.ReglaNegocioException;
import py.edu.uc.lp3.repository.JugadorRepository;
import py.edu.uc.lp3.service.ArmaService;
import py.edu.uc.lp3.service.ResultadoSimulacion;
import py.edu.uc.lp3.service.SimulacionService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas de integración de la Arquitectura en 3 Capas (Controller / Service / Repository con H2).
 * Valida operaciones CRUD completas, jerarquía de excepciones del dominio y simulación persistida.
 */
@SpringBootTest
@Transactional
class ArmaCrudIntegrationTests {

    @Autowired
    private ArmaService armaService;

    @Autowired
    private SimulacionService simulacionService;

    @Autowired
    private JugadorRepository jugadorRepository;

    @Test
    void testCrearYBuscarArma() {
        Arma nueva = new Arma("Galil AR", 1800f, 30, 3.75f, 35);
        Arma guardada = armaService.crear(nueva);

        assertNotNull(guardada.getId(), "El ID debe ser generado automáticamente por H2");
        assertEquals("Galil AR", guardada.getNombre());
        assertEquals(1800f, guardada.getPrecio());
        assertEquals(35, guardada.getMunicionActual());

        Arma encontrada = armaService.buscarPorId(guardada.getId());
        assertEquals(guardada.getId(), encontrada.getId());
        assertEquals("Galil AR", encontrada.getNombre());
    }

    @Test
    void testListarArmas() {
        List<Arma> armas = armaService.listarTodas();
        assertNotNull(armas);
        assertFalse(armas.isEmpty(), "La base de datos debe contener armas pre-cargadas por DataInitializer");
    }

    @Test
    void testRechazoPorNombreDuplicado() {
        Arma arma1 = new Arma("SG 553", 3000f, 30, 3.9f, 30);
        armaService.crear(arma1);

        Arma duplicada = new Arma("sg 553", 2900f, 28, 3.8f, 30);
        assertThrows(ReglaNegocioException.class, () -> armaService.crear(duplicada),
                "Debe rechazar la creación si ya existe un arma con el mismo nombre");
    }

    @Test
    void testRechazoPorPrecioInvalido() {
        // Validación de invariante en entidad: precio negativo
        assertThrows(IllegalArgumentException.class, () -> new Arma("P90", -500f, 26, 2.6f, 50));

        // Validación de regla de negocio en servicio: precio superior al límite de CS2 ($16,000)
        assertThrows(EconomiaInvalidaException.class, () -> new Arma("Cañón Láser", 20000f, 100, 5f, 10));
    }

    @Test
    void testActualizarArma() {
        Arma arma = new Arma("FAMAS", 2050f, 30, 3.4f, 25);
        Arma creada = armaService.crear(arma);

        Arma datosNuevos = new Arma("FAMAS Burst", 2100f, 32, 3.4f, 25);
        Arma actualizada = armaService.actualizar(creada.getId(), datosNuevos);

        assertEquals("FAMAS Burst", actualizada.getNombre());
        assertEquals(2100f, actualizada.getPrecio());
        assertEquals(32, actualizada.getDano());
    }

    @Test
    void testActualizacionParcialPatch() {
        Arma arma = new Arma("MP5-SD", 1500f, 27, 2.9f, 30);
        Arma creada = armaService.crear(arma);

        Map<String, Object> patch = new HashMap<>();
        patch.put("precio", 1400f);
        patch.put("dano", 29);

        Arma parcheada = armaService.actualizarParcial(creada.getId(), patch);
        assertEquals(1400f, parcheada.getPrecio());
        assertEquals(29, parcheada.getDano());
        assertEquals("MP5-SD", parcheada.getNombre()); // Nombre preservado
    }

    @Test
    void testEliminarArma() {
        Arma arma = new Arma("MP7", 1500f, 29, 2.8f, 30);
        Arma creada = armaService.crear(arma);
        Long id = creada.getId();

        armaService.eliminar(id);
        assertThrows(RecursoNoEncontradoException.class, () -> armaService.buscarPorId(id),
                "El arma eliminada no debe poder encontrarse");
    }

    @Test
    void testOperacionesDeNegocioDispararYRecargar() {
        Arma arma = new Arma("M249", 5200f, 32, 7.5f, 100);
        Arma creada = armaService.crear(arma);
        Long id = creada.getId();

        // Disparo con persistencia en BD
        String resDisparo = armaService.dispararArma(id, 20, false);
        assertTrue(resDisparo.contains("disparó"));

        Arma postDisparo = armaService.buscarPorId(id);
        assertEquals(99, postDisparo.getMunicionActual());

        // Recarga con persistencia en BD
        Arma recargada = armaService.recargarArma(id);
        assertEquals(100, recargada.getMunicionActual());
    }

    @Test
    void testPersistirFrancotiradorPolimorfico() {
        Francotirador sniper = new Francotirador("SSG 08 Scout", 1700f, 88, 3.6f, 10, 4);
        Arma guardada = armaService.crear(sniper);

        assertNotNull(guardada.getId());
        assertEquals("SSG 08 Scout", guardada.getNombre());
        assertTrue(guardada.describirComportamiento().contains("Francotirador"));
        assertEquals("FRANCOTIRADOR", guardada.getTipo());
    }

    @Test
    void testJerarquiaExcepcionHija1MunicionAgotada() {
        // Crear arma con 1 bala
        Arma arma = new Arma("Arma UnTiro", 1000f, 50, 2f, 1);
        Arma creada = armaService.crear(arma);

        // Disparo 1: consume la única bala
        armaService.dispararArma(creada.getId(), null, null);
        assertEquals(0, armaService.buscarPorId(creada.getId()).getMunicionActual());

        // Disparo 2: debe lanzar la excepción hija 1 del dominio
        assertThrows(MunicionAgotadaException.class, () -> {
            armaService.dispararArma(creada.getId(), null, null);
        }, "Debe lanzar MunicionAgotadaException cuando se intenta disparar con 0 balas");
    }

    @Test
    void testJerarquiaExcepcionHija2EconomiaInvalida() {
        // Debe lanzar la excepción hija 2 del dominio al superar $16,000
        assertThrows(EconomiaInvalidaException.class, () -> {
            new Arma("AWP Dragon Lore", 25000f, 115, 4.5f, 10);
        }, "Debe lanzar EconomiaInvalidaException al superar el tope de $16,000 en CS2");
    }

    @Test
    void testSimulacionCombatePersistidaEnH2() {
        // Crear las 3 clases participantes
        Francotirador sniper = new Francotirador("AWP Sim", 4750f, 115, 4.3f, 10, 2);
        Pistola pistola = new Pistola("Deagle Sim", 700f, 53, 1.8f, 7, "Semiautomático", 7);
        Jugador jugador = new Jugador("Victima CS2", "Terrorista", 100, 800);

        Arma sniperGuardado = armaService.crear(sniper);
        Arma pistolaGuardada = armaService.crear(pistola);
        Jugador jugadorGuardado = jugadorRepository.save(jugador);

        // Ejecutar simulación que aplica Regla 1 (consumo/balística) y Regla 2 (impacto/salud)
        ResultadoSimulacion resultado = simulacionService.ejecutarSimulacionCombate(
                sniperGuardado.getId(), pistolaGuardada.getId(), jugadorGuardado.getId(), 30, true);

        assertNotNull(resultado);
        assertTrue(resultado.isPersistidoEnH2());
        assertEquals(9, resultado.getFrancotiradorMunicionDespues(), "Francotirador gastó 1 bala");
        assertEquals(6, resultado.getPistolaMunicionDespues(), "Pistola gastó 1 bala");
        assertTrue(resultado.getDanioTotalRecibido() > 0, "Debe haber infligido daño");
        assertTrue(resultado.getJugadorSaludDespues() < 100, "La salud del jugador debe haber disminuido");

        // Verificar persistencia de verdad en H2 (consultar de nuevo desde la BD)
        Arma sniperPost = armaService.buscarPorId(sniperGuardado.getId());
        Arma pistolaPost = armaService.buscarPorId(pistolaGuardada.getId());
        Jugador jugadorPost = jugadorRepository.findById(jugadorGuardado.getId()).orElseThrow();

        assertEquals(9, sniperPost.getMunicionActual(), "La munición del sniper debe estar persistida en H2");
        assertEquals(6, pistolaPost.getMunicionActual(), "La munición de la pistola debe estar persistida en H2");
        assertEquals(resultado.getJugadorSaludDespues(), jugadorPost.getSalud(), "La salud debe estar persistida en H2");
    }
}
