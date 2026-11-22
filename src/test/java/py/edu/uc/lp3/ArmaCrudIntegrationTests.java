package py.edu.uc.lp3;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import py.edu.uc.lp3.domain.Arma;
import py.edu.uc.lp3.domain.Francotirador;
import py.edu.uc.lp3.domain.Jugador;
import py.edu.uc.lp3.domain.Pistola;
import py.edu.uc.lp3.exception.EconomiaInvalidaException;
import py.edu.uc.lp3.exception.MunicionAgotadaException;
import py.edu.uc.lp3.exception.RecursoNoEncontradoException;
import py.edu.uc.lp3.exception.ReglaNegocioException;
import py.edu.uc.lp3.rest.controller.UIController;
import py.edu.uc.lp3.rest.dto.ArmaRequest;
import py.edu.uc.lp3.rest.dto.JugadorRequest;
import py.edu.uc.lp3.service.ArmaService;
import py.edu.uc.lp3.service.JugadorService;
import py.edu.uc.lp3.service.ResultadoSimulacion;
import py.edu.uc.lp3.service.SimulacionService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ArmaCrudIntegrationTests {

    @Autowired
    private ArmaService armaService;

    @Autowired
    private JugadorService jugadorService;

    @Autowired
    private SimulacionService simulacionService;

    @Autowired
    private UIController uiController;

    private ArmaRequest request(String nombre, float precio, int dano) {
        ArmaRequest req = new ArmaRequest();
        req.setNombre(nombre);
        req.setPrecio(precio);
        req.setDano(dano);
        req.setPeso(3f);
        req.setMunicionMax(30);
        return req;
    }

    private JugadorRequest jugadorRequest(String alias) {
        JugadorRequest req = new JugadorRequest();
        req.setAlias(alias);
        req.setBando("Terrorista");
        req.setSalud(100);
        req.setDinero(800);
        return req;
    }

    @Test
    void testCrearYBuscarArma() {
        Arma guardada = armaService.crear(request("Galil AR", 1800f, 30));

        assertNotNull(guardada.getId());
        assertEquals("Galil AR", guardada.getNombre());
        assertEquals(1800f, guardada.getPrecio());
        assertEquals(30, guardada.getMunicionActual());

        Arma encontrada = armaService.buscarPorId(guardada.getId());
        assertEquals(guardada.getId(), encontrada.getId());
        assertEquals("Galil AR", encontrada.getNombre());
    }

    @Test
    void testListarArmas() {
        List<Arma> armas = armaService.listarTodas();
        assertFalse(armas.isEmpty());
    }

    @Test
    void testRechazoPorNombreDuplicado() {
        armaService.crear(request("SG 553", 3000f, 30));
        ArmaRequest duplicada = request("sg 553", 2900f, 28);
        assertThrows(ReglaNegocioException.class, () -> armaService.crear(duplicada));
    }

    @Test
    void testRechazoPorPrecioInvalidoEnUnSoloLugar() {
        assertThrows(EconomiaInvalidaException.class, () -> armaService.crear(request("P90", -500f, 26)));
        assertThrows(EconomiaInvalidaException.class, () -> armaService.crear(request("Cañón Láser", 20000f, 100)));
    }

    @Test
    void testRechazoPorDanoInvalido() {
        assertThrows(ReglaNegocioException.class, () -> armaService.crear(request("AWP Rota", 4750f, -15)));
    }

    @Test
    void testActualizarArma() {
        Arma creada = armaService.crear(request("FAMAS", 2050f, 30));
        ArmaRequest datos = request("FAMAS Burst", 2100f, 32);

        Arma actualizada = armaService.actualizar(creada.getId(), datos);

        assertEquals("FAMAS Burst", actualizada.getNombre());
        assertEquals(2100f, actualizada.getPrecio());
        assertEquals(32, actualizada.getDano());
    }

    @Test
    void testActualizacionParcialPatch() {
        Arma creada = armaService.crear(request("MP5-SD", 1500f, 27));
        Map<String, Object> patch = new HashMap<>();
        patch.put("precio", 1400f);
        patch.put("dano", 29);

        Arma parcheada = armaService.actualizarParcial(creada.getId(), patch);

        assertEquals(1400f, parcheada.getPrecio());
        assertEquals(29, parcheada.getDano());
        assertEquals("MP5-SD", parcheada.getNombre());
    }

    @Test
    void testEliminarArma() {
        Arma creada = armaService.crear(request("MP7", 1500f, 29));
        armaService.eliminar(creada.getId());
        assertThrows(RecursoNoEncontradoException.class, () -> armaService.buscarPorId(creada.getId()));
    }

    @Test
    void testOperacionesDeNegocioDispararYRecargar() {
        Arma creada = armaService.crear(request("M249", 5200f, 32));

        String resultado = armaService.dispararArma(creada.getId(), 20, false);
        assertTrue(resultado.contains("disparó"));
        assertEquals(29, armaService.buscarPorId(creada.getId()).getMunicionActual());

        Arma recargada = armaService.recargarArma(creada.getId());
        assertEquals(30, recargada.getMunicionActual());
    }

    @Test
    void testPersistirFrancotiradorPolimorfico() {
        ArmaRequest req = request("SSG 08 Scout", 1700f, 88);
        req.setTipo("FRANCOTIRADOR");
        req.setPeso(3.6f);
        req.setMunicionMax(10);
        req.setZoom(4);

        Arma guardada = armaService.crear(req);

        assertNotNull(guardada.getId());
        assertInstanceOf(Francotirador.class, guardada);
        assertTrue(guardada.describirComportamiento().contains("Francotirador"));
        assertEquals("FRANCOTIRADOR", guardada.getTipo());
    }

    @Test
    void testJerarquiaExcepcionHija1MunicionAgotadaEnUnPaso() {
        Arma creada = armaService.crear(request("Fusil Sin Balas", 1000f, 50));

        armaService.actualizarParcial(creada.getId(), Map.of("municionActual", 0));
        assertEquals(0, armaService.buscarPorId(creada.getId()).getMunicionActual());

        assertThrows(MunicionAgotadaException.class,
                () -> armaService.dispararArma(creada.getId(), null, null));
    }

    @Test
    void testJerarquiaExcepcionHija2EconomiaInvalida() {
        ArmaRequest req = request("AWP Dragon Lore", 25000f, 115);
        assertThrows(EconomiaInvalidaException.class, () -> armaService.crear(req));
    }

    @Test
    void testValidacionDeJugadoresEnService() {
        JugadorRequest valido = jugadorRequest("Fallen");
        assertEquals("Fallen", jugadorService.crear(valido).getAlias());

        JugadorRequest sinAlias = jugadorRequest("   ");
        JugadorRequest saludMala = jugadorRequest("Fallen 2");
        saludMala.setSalud(105);
        JugadorRequest dineroMalo = jugadorRequest("Fallen 3");
        dineroMalo.setDinero(16001);

        assertThrows(ReglaNegocioException.class, () -> jugadorService.crear(sinAlias));
        assertThrows(ReglaNegocioException.class, () -> jugadorService.crear(saludMala));
        assertThrows(ReglaNegocioException.class, () -> jugadorService.crear(dineroMalo));
    }

    @Test
    void testSimulacionCombatePersistidaEnH2() {
        ArmaRequest sniperReq = request("AWP Sim", 4750f, 115);
        sniperReq.setTipo("FRANCOTIRADOR");
        sniperReq.setPeso(4.3f);
        sniperReq.setMunicionMax(10);
        sniperReq.setZoom(2);

        ArmaRequest pistolaReq = request("Deagle Sim", 700f, 53);
        pistolaReq.setTipo("PISTOLA");
        pistolaReq.setPeso(1.8f);
        pistolaReq.setMunicionMax(7);
        pistolaReq.setModoDisparo("Semiautomático");
        pistolaReq.setCargador(7);

        Arma sniper = armaService.crear(sniperReq);
        Arma pistola = armaService.crear(pistolaReq);
        Jugador jugador = jugadorService.crear(jugadorRequest("Víctima CS2"));

        ResultadoSimulacion resultado = simulacionService.ejecutarSimulacionCombate(
                sniper.getId(), pistola.getId(), jugador.getId(), 30, true);

        assertTrue(resultado.isPersistidoEnH2());
        assertEquals(9, resultado.getFrancotiradorMunicionDespues());
        assertEquals(6, resultado.getPistolaMunicionDespues());
        assertTrue(resultado.getDanioTotalRecibido() > 0);
        assertEquals(0, resultado.getJugadorSaludDespues());
        assertFalse(resultado.isJugadorSobrevive());

        assertEquals(9, armaService.buscarPorId(sniper.getId()).getMunicionActual());
        assertEquals(6, armaService.buscarPorId(pistola.getId()).getMunicionActual());
        assertEquals(0, jugadorService.buscarPorId(jugador.getId()).getSalud());
    }

    @Test
    void testEndpointInterfazLeeEstadoPersistido() {
        Francotirador persistido = armaService.primerFrancotirador();
        armaService.actualizarParcial(persistido.getId(), Map.of("municionActual", 3));

        ResponseEntity<List<Map<String, Object>>> respuesta = uiController.listarElementosUI();
        List<Map<String, Object>> elementos = respuesta.getBody();

        assertNotNull(elementos);
        assertEquals(2, elementos.size());
        assertTrue(elementos.stream()
                .anyMatch(item -> String.valueOf(item.get("mensajeUI")).contains("Munición en pantalla: 3/")));
    }
}
