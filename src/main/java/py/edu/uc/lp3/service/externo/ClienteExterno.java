package py.edu.uc.lp3.service.externo;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import py.edu.uc.lp3.config.ApiExternasProperties;
import py.edu.uc.lp3.exception.ValidacionExternaRechazadaException;

import java.nio.charset.StandardCharsets;
import java.util.Map;

public abstract class ClienteExterno {

    protected final RestClient restClient;
    protected final ApiExternasProperties.Api propiedades;
    private final String origen;

    protected ClienteExterno(RestClient restClient, ApiExternasProperties.Api propiedades, String origen) {
        this.restClient = restClient;
        this.propiedades = propiedades;
        this.origen = origen;
    }

    public ValidacionExterna validar(Map<String, Object> payload) {
        try {
            ValidacionExterna respuesta = restClient.post()
                    .uri(propiedades.getValidacionPath())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(payload)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (request, response) -> {
                        String cuerpo = new String(response.getBody().readAllBytes(), StandardCharsets.UTF_8);
                        throw new ValidacionExternaRechazadaException(origen, response.getStatusCode().value(), cuerpo);
                    })
                    .body(ValidacionExterna.class);

            if (respuesta == null || !respuesta.isAprobado()) {
                String cuerpo = respuesta == null ? "Respuesta vacía" : respuesta.getMensaje();
                throw new ValidacionExternaRechazadaException(origen, 200, cuerpo);
            }
            return respuesta;
        } catch (ValidacionExternaRechazadaException ex) {
            throw ex;
        } catch (RestClientException ex) {
            throw new ValidacionExternaRechazadaException(origen, 0, ex.getMessage());
        }
    }
}
