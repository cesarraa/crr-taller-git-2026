package py.edu.uc.lp3;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import py.edu.uc.lp3.domain.Arma;
import py.edu.uc.lp3.domain.Jugador;
import py.edu.uc.lp3.exception.ValidacionExternaRechazadaException;
import py.edu.uc.lp3.repository.EscenarioValidadoRepository;
import py.edu.uc.lp3.rest.dto.ArmaRequest;
import py.edu.uc.lp3.rest.dto.EscenarioRequest;
import py.edu.uc.lp3.rest.dto.JugadorRequest;
import py.edu.uc.lp3.service.ArmaService;
import py.edu.uc.lp3.service.EscenarioService;
import py.edu.uc.lp3.service.JugadorService;
import py.edu.uc.lp3.service.externo.ClienteA2;
import py.edu.uc.lp3.service.externo.ClienteA3;
import py.edu.uc.lp3.service.externo.ValidacionExterna;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class EscenarioCruzadoTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ArmaService armaService;

    @Autowired
    private JugadorService jugadorService;

    @Autowired
    private EscenarioService escenarioService;

    @Autowired
    private EscenarioValidadoRepository escenarioRepository;

    @MockitoBean
    private ClienteA2 clienteA2;

    @MockitoBean
    private ClienteA3 clienteA3;

    @Test
    void testEscenarioFelizPersisteYSoloSeGuardaSiAmbasAceptan() throws Exception {
        Arma arma = crearArma("AWP Escenario Feliz");
        Jugador jugador = crearJugador("feliz");
        long cantidadInicial = escenarioRepository.count();

        when(clienteA2.validar(anyMap())).thenReturn(aprobado("A2: daño permitido"));
        when(clienteA3.validar(anyMap())).thenReturn(aprobado("A3: munición y economía permitidas"));

        mockMvc.perform(post("/api/escenarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request(arma, jugador, "Emboscada en Mirage"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombreEscenario").value("Emboscada en Mirage"))
                .andExpect(jsonPath("$.resultadoA2").value("A2: daño permitido"))
                .andExpect(jsonPath("$.resultadoA3").value("A3: munición y economía permitidas"));

        assertEquals(cantidadInicial + 1, escenarioRepository.count());
        verify(clienteA2, times(1)).validar(anyMap());
        verify(clienteA3, times(1)).validar(anyMap());
    }

    @Test
    void testRechazoDeA2NoTocaLaBaseNiLlamaAA3() throws Exception {
        Arma arma = crearArma("AWP Rechazo A2");
        Jugador jugador = crearJugador("rechazoA2");
        long cantidadInicial = escenarioRepository.count();

        when(clienteA2.validar(anyMap()))
                .thenThrow(new ValidacionExternaRechazadaException("A2", 400, "{\"motivo\":\"dano\"}"));

        mockMvc.perform(post("/api/escenarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request(arma, jugador, "Emboscada A2"))))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.codigo").value("CS2_VALIDACION_A2_RECHAZADA"))
                .andExpect(jsonPath("$.origen").value("A2"))
                .andExpect(jsonPath("$.statusExterno").value(400))
                .andExpect(jsonPath("$.cuerpoExterno").value("{\"motivo\":\"dano\"}"));

        assertEquals(cantidadInicial, escenarioRepository.count());
        verify(clienteA3, never()).validar(anyMap());
    }

    @Test
    void testRechazoDeA3NoTocaLaBaseYAtribuyeElOrigen() throws Exception {
        Arma arma = crearArma("AWP Rechazo A3");
        Jugador jugador = crearJugador("rechazoA3");
        long cantidadInicial = escenarioRepository.count();

        when(clienteA2.validar(anyMap())).thenReturn(aprobado("A2 OK"));
        when(clienteA3.validar(anyMap()))
                .thenThrow(new ValidacionExternaRechazadaException("A3", 422, "{\"motivo\":\"economia\"}"));

        mockMvc.perform(post("/api/escenarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request(arma, jugador, "Emboscada A3"))))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.codigo").value("CS2_VALIDACION_A3_RECHAZADA"))
                .andExpect(jsonPath("$.origen").value("A3"))
                .andExpect(jsonPath("$.statusExterno").value(422));

        assertEquals(cantidadInicial, escenarioRepository.count());
    }

    @Test
    void testApiA3CaidaSeAtribuyeConStatusExternoCero() throws Exception {
        Arma arma = crearArma("AWP A3 caida");
        Jugador jugador = crearJugador("caidaA3");

        when(clienteA2.validar(anyMap())).thenReturn(aprobado("A2 OK"));
        when(clienteA3.validar(anyMap()))
                .thenThrow(new ValidacionExternaRechazadaException("A3", 0, "Connection refused"));

        mockMvc.perform(post("/api/escenarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request(arma, jugador, "Emboscada sin A3"))))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.origen").value("A3"))
                .andExpect(jsonPath("$.statusExterno").value(0));
    }

    @Test
    void testPayloadsEnvianReglasYDatosLocales() {
        Arma arma = crearArma("AWP Payloads");
        Jugador jugador = crearJugador("payloads");

        when(clienteA2.validar(anyMap())).thenReturn(aprobado("A2 OK"));
        when(clienteA3.validar(anyMap())).thenReturn(aprobado("A3 OK"));

        escenarioService.ejecutar(request(arma, jugador, "Emboscada con payloads"));

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Map<String, Object>> captorA2 = ArgumentCaptor.forClass(Map.class);
        @SuppressWarnings("unchecked")
        ArgumentCaptor<Map<String, Object>> captorA3 = ArgumentCaptor.forClass(Map.class);
        verify(clienteA2).validar(captorA2.capture());
        verify(clienteA3).validar(captorA3.capture());

        Map<String, Object> payloadA2 = captorA2.getValue();
        assertEquals("DANO_BALISTICO", payloadA2.get("regla"));
        assertEquals("AWP Payloads", payloadA2.get("arma"));
        assertEquals(115, payloadA2.get("dano"));
        assertEquals(35, payloadA2.get("distancia"));
        assertEquals(false, payloadA2.get("headshot"));

        Map<String, Object> payloadA3 = captorA3.getValue();
        assertEquals("MUNICION_GRANADA_LUIS", payloadA3.get("regla"));
        assertEquals("AWP Payloads", payloadA3.get("arma"));
        assertEquals("payloads", payloadA3.get("jugador"));
    }

    @Test
    void testEscenarioPersistidoSeRecuperaPorGet() throws Exception {
        Arma arma = crearArma("AWP Lectura");
        Jugador jugador = crearJugador("lectura");

        when(clienteA2.validar(anyMap())).thenReturn(aprobado("A2 OK"));
        when(clienteA3.validar(anyMap())).thenReturn(aprobado("A3 OK"));

        String respuesta = mockMvc.perform(post("/api/escenarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request(arma, jugador, "Emboscada leida"))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        long id = objectMapper.readTree(respuesta).get("id").asLong();

        mockMvc.perform(get("/api/escenarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombreEscenario").value("Emboscada leida"));

        mockMvc.perform(get("/api/escenarios/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.armaNombre").value("AWP Lectura"))
                .andExpect(jsonPath("$.jugadorAlias").value("lectura"));
    }

    @Test
    void testDatosLocalesInvalidosNoConsultanClientes() throws Exception {
        EscenarioRequest req = new EscenarioRequest();
        req.setArmaId(0L);
        req.setJugadorId(1L);

        mockMvc.perform(post("/api/escenarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.codigo").value("CS2_REGLA_NEGOCIO"));

        verifyNoInteractions(clienteA2, clienteA3);
    }

    @Test
    void testBuscarEscenarioInexistenteDevuelve404() throws Exception {
        mockMvc.perform(get("/api/escenarios/999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.codigo").value("CS2_NOT_FOUND"));
    }

    private Arma crearArma(String nombre) {
        ArmaRequest req = new ArmaRequest();
        req.setTipo("FRANCOTIRADOR");
        req.setNombre(nombre);
        req.setPrecio(4750f);
        req.setDano(115);
        req.setPeso(6.5f);
        req.setMunicionMax(10);
        req.setZoom(8);
        return armaService.crear(req);
    }

    private Jugador crearJugador(String alias) {
        JugadorRequest req = new JugadorRequest();
        req.setAlias(alias);
        req.setBando("Counter-Terrorista");
        req.setSalud(100);
        req.setDinero(16000);
        return jugadorService.crear(req);
    }

    private EscenarioRequest request(Arma arma, Jugador jugador, String nombre) {
        EscenarioRequest req = new EscenarioRequest();
        req.setArmaId(arma.getId());
        req.setJugadorId(jugador.getId());
        req.setDistancia(35);
        req.setHeadshot(false);
        req.setNombreEscenario(nombre);
        req.setDescripcion("Disparo combinado con dos validaciones externas");
        return req;
    }

    private ValidacionExterna aprobado(String mensaje) {
        ValidacionExterna respuesta = new ValidacionExterna();
        respuesta.setAprobado(true);
        respuesta.setMensaje(mensaje);
        respuesta.setCodigo("ACEPTADO");
        return respuesta;
    }
}
