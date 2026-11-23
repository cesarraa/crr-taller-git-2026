package py.edu.uc.lp3.service.externo;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ValidacionExternaTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void respuestaAceptadaDeLuisNoTraeBanderaPeroSeAcepta() throws Exception {
        String json = "{\"danioInfligido\":19,\"balasRestantes\":11,\"granadaLanzada\":true}";

        ValidacionExterna respuesta = objectMapper.readValue(json, ValidacionExterna.class);

        assertTrue(respuesta.isAprobado());
        assertEquals(19, respuesta.getDanioInfligido());
        assertEquals(11, respuesta.getBalasRestantes());
        assertTrue(respuesta.getGranadaLanzada());
    }

    @Test
    void respuestaAceptadaDeGiovanniConservaElMensaje() throws Exception {
        String json = "{\"mensaje\":\"AK-47 ROUND ROBIN dispara a 35 m y causa 20 de daño.\"," +
                "\"rifleId\":108,\"municionRestante\":28,\"jugadorId\":100," +
                "\"danoRecibido\":20,\"vidaRestante\":60}";

        ValidacionExterna respuesta = objectMapper.readValue(json, ValidacionExterna.class);

        assertTrue(respuesta.isAprobado());
        assertEquals("AK-47 ROUND ROBIN dispara a 35 m y causa 20 de daño.", respuesta.getMensaje());
    }

    @Test
    void banderaExplicitaFalsaSigueSiendoRechazo() throws Exception {
        ValidacionExterna respuesta = objectMapper.readValue(
                "{\"aprobado\":false,\"mensaje\":\"sin balas\"}", ValidacionExterna.class);

        assertFalse(respuesta.isAprobado());
        assertEquals("sin balas", respuesta.getMensaje());
    }
}
