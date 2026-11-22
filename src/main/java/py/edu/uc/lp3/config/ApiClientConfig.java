package py.edu.uc.lp3.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class ApiClientConfig {

    @Bean
    public RestClient restClientA2(RestClient.Builder builder, ApiExternasProperties properties) {
        return builder.baseUrl(properties.getA2().getBaseUrl()).build();
    }

    @Bean
    public RestClient restClientA3(RestClient.Builder builder, ApiExternasProperties properties) {
        return builder.baseUrl(properties.getA3().getBaseUrl()).build();
    }
}
