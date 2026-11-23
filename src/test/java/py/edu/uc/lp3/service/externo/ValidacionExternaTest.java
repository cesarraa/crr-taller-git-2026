package py.edu.uc.lp3.service.externo;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ValidacionExternaTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void respuestaAceptadaDeLuisCombateSeAceptaPorAliasAceptada() throws Exception {
        String json = "{\"aceptada\":true,\"mensaje\":\"La regla acepta el disparo y el lanzamiento.\"," +
                "\"danioEfectivo\":11,\"balasTransferidas\":null}";

        ValidacionExterna respuesta = objectMapper.readValue(json, ValidacionExterna.class);

        assertTrue(respuesta.isAprobado());
        assertEquals("La regla acepta el disparo y el lanzamiento.", respuesta.getMensaje());
    }

    @Test
    void respuestaAceptadaDeGiovanniDisparoSeAceptaPorAliasAceptada() throws Exception {
        String json = "{\"aceptada\":true,\"regla\":\"DISPARO\"," +
                "\"mensaje\":\"La regla acepta el disparo.\",\"danoEfectivo\":15}";

        ValidacionExterna respuesta = objectMapper.readValue(json, ValidacionExterna.class);

        assertTrue(respuesta.isAprobado());
        assertEquals("La regla acepta el disparo.", respuesta.getMensaje());
    }

    @Test
    void aceptadaFalsoEsRechazoConMensaje() throws Exception {
        String json = "{\"aceptada\":false,\"mensaje\":\"El arma no tiene balas en el cargador.\"}";

        ValidacionExterna respuesta = objectMapper.readValue(json, ValidacionExterna.class);

        assertFalse(respuesta.isAprobado());
        assertEquals("El arma no tiene balas en el cargador.", respuesta.getMensaje());
    }

    @Test
    void banderaExplicitaFalsaSigueSiendoRechazo() throws Exception {
        ValidacionExterna respuesta = objectMapper.readValue(
                "{\"aprobado\":false,\"mensaje\":\"sin balas\"}", ValidacionExterna.class);

        assertFalse(respuesta.isAprobado());
        assertEquals("sin balas", respuesta.getMensaje());
    }
}
