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
        Map<String, Object> query = new LinkedHashMap<>();
        query.put("armaDeFuegoId", propiedades.getArmaDeFuegoId());
        query.put("granadaId", propiedades.getGranadaId());

        ValidacionExterna respuesta = validarConQuery(query);
        if (respuesta.getMensaje() == null && respuesta.getDanioInfligido() != null) {
            respuesta.setMensaje(String.format("daño %d, balas restantes %d, granada lanzada %s",
                    respuesta.getDanioInfligido(),
                    respuesta.getBalasRestantes(),
                    Boolean.TRUE.equals(respuesta.getGranadaLanzada())));
        }
        return respuesta;
    }
}
