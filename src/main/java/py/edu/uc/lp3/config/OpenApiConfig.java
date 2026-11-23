package py.edu.uc.lp3.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI().info(new Info()
                .title("API CS2 - POO-08/09")
                .version("1.0.0")
                .description("CRUD de armas y jugadores, simulación balística persistida y escenario de APIs cruzadas. " +
                        "Las reglas propias son: (1) munición agotada al disparar, HTTP 409 con CS2_ARMA_SIN_MUNICION; " +
                        "(2) precio fuera de 0..16000, HTTP 422 con CS2_ECONOMIA_EXCEDIDA. " +
                        "POST /api/escenarios consulta A2 (Giovanni, munición, puerto 8081) y A3 (Luis, munición y " +
                        "granada, puerto 8080) y solo guarda en H2 si ambas aceptan; un rechazo ajeno responde 422 " +
                        "con codigo CS2_VALIDACION_<A2|A3>_RECHAZADA y el origen."));
    }
}
