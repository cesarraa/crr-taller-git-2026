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
    }
}
