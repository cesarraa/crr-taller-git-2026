package py.edu.uc.lp3.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Configuration
public class ApiClientConfig {

    @Bean
    public RestClient restClientA2(ApiExternasProperties properties) {
        return cliente(properties.getA2());
    }

    @Bean
    public RestClient restClientA3(ApiExternasProperties properties) {
        return cliente(properties.getA3());
    }

    private RestClient cliente(ApiExternasProperties.Api propiedades) {
        SimpleClientHttpRequestFactory fabrica = new SimpleClientHttpRequestFactory();
        fabrica.setConnectTimeout(Duration.ofMillis(propiedades.getConnectTimeoutMs()));
        fabrica.setReadTimeout(Duration.ofMillis(propiedades.getReadTimeoutMs()));
        return RestClient.builder()
                .baseUrl(propiedades.getBaseUrl())
                .requestFactory(fabrica)
                .build();
    }
}
