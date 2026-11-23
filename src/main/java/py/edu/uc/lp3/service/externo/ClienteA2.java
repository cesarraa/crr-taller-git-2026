package py.edu.uc.lp3.service.externo;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import py.edu.uc.lp3.config.ApiExternasProperties;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class ClienteA2 extends ClienteExterno {

    public ClienteA2(@Qualifier("restClientA2") RestClient restClient, ApiExternasProperties properties) {
        super(restClient, properties.getA2(), "A2");
    }

    @Override
    public ValidacionExterna validar(Map<String, Object> payload) {
        Map<String, Object> query = new LinkedHashMap<>();
        query.put("rifleId", propiedades.getRifleId());
        query.put("jugadorId", propiedades.getJugadorId());
        Object distancia = payload.get("distancia");
        query.put("distancia", distancia == null ? propiedades.getDistancia() : distancia);
        return validarConQuery(query);
    }
}
