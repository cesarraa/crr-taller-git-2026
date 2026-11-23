package py.edu.uc.lp3.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "apis")
public class ApiExternasProperties {

    private Api a2 = new Api();
    private Api a3 = new Api();

    public Api getA2() {
        return a2;
    }

    public void setA2(Api a2) {
        this.a2 = a2;
    }

    public Api getA3() {
        return a3;
    }

    public void setA3(Api a3) {
        this.a3 = a3;
    }

    public static class Api {

        private String baseUrl;
        private String openApiUrl;
        private String validacionPath;
        private Long rifleId = 108L;
        private Long jugadorId = 100L;
        private Integer distancia = 35;
        private Long armaDeFuegoId = 1L;
        private Long granadaId = 2L;
        private long connectTimeoutMs = 2000;
        private long readTimeoutMs = 3000;

        public long getConnectTimeoutMs() {
            return connectTimeoutMs;
        }

        public void setConnectTimeoutMs(long connectTimeoutMs) {
            this.connectTimeoutMs = connectTimeoutMs;
        }

        public long getReadTimeoutMs() {
            return readTimeoutMs;
        }

        public void setReadTimeoutMs(long readTimeoutMs) {
            this.readTimeoutMs = readTimeoutMs;
        }

        public String getBaseUrl() {
            return baseUrl;
        }

        public void setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
        }

        public String getOpenApiUrl() {
            return openApiUrl;
        }

        public void setOpenApiUrl(String openApiUrl) {
            this.openApiUrl = openApiUrl;
        }

        public String getValidacionPath() {
            return validacionPath;
        }

        public void setValidacionPath(String validacionPath) {
            this.validacionPath = validacionPath;
        }

        public Long getRifleId() {
            return rifleId;
        }

        public void setRifleId(Long rifleId) {
            this.rifleId = rifleId;
        }

        public Long getJugadorId() {
            return jugadorId;
        }

        public void setJugadorId(Long jugadorId) {
            this.jugadorId = jugadorId;
        }

        public Integer getDistancia() {
            return distancia;
        }

        public void setDistancia(Integer distancia) {
            this.distancia = distancia;
        }

        public Long getArmaDeFuegoId() {
            return armaDeFuegoId;
        }

        public void setArmaDeFuegoId(Long armaDeFuegoId) {
            this.armaDeFuegoId = armaDeFuegoId;
        }

        public Long getGranadaId() {
            return granadaId;
        }

        public void setGranadaId(Long granadaId) {
            this.granadaId = granadaId;
        }
    }
}
