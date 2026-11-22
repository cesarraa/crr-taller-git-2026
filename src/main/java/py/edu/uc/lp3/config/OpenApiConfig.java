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
                .description("CRUD de armas, simulación balística persistida y escenario de APIs cruzadas del Taller de Git, POO y Persistencia H2."));
    }
}
