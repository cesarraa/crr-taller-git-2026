package py.edu.uc.lp3.service.externo;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import py.edu.uc.lp3.config.ApiExternasProperties;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class ClienteA3 extends ClienteExterno {

    public ClienteA3(@Qualifier("restClientA3") RestClient restClient, ApiExternasProperties properties) {
        super(restClient, properties.getA3(), "A3");
    }

    @Override
    public ValidacionExterna validar(Map<String, Object> payload) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("tieneBalas", propiedades.getTieneBalas());
        body.put("granadaLanzada", propiedades.getGranadaLanzada());
        return super.validar(body);
    }
}
