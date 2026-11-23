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
        private Integer municionActual = 29;
        private Integer danoBase = 36;
        private Double precision = 0.85;
        private Integer distancia = 35;
        private Boolean tieneBalas = true;
        private Boolean granadaLanzada = false;
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

        public Integer getMunicionActual() {
            return municionActual;
        }

        public void setMunicionActual(Integer municionActual) {
            this.municionActual = municionActual;
        }

        public Integer getDanoBase() {
            return danoBase;
        }

        public void setDanoBase(Integer danoBase) {
            this.danoBase = danoBase;
        }

        public Double getPrecision() {
            return precision;
        }

        public void setPrecision(Double precision) {
            this.precision = precision;
        }

        public Integer getDistancia() {
            return distancia;
        }

        public void setDistancia(Integer distancia) {
            this.distancia = distancia;
        }

        public Boolean getTieneBalas() {
            return tieneBalas;
        }

        public void setTieneBalas(Boolean tieneBalas) {
            this.tieneBalas = tieneBalas;
        }

        public Boolean getGranadaLanzada() {
            return granadaLanzada;
        }

        public void setGranadaLanzada(Boolean granadaLanzada) {
            this.granadaLanzada = granadaLanzada;
        }
    }
}
