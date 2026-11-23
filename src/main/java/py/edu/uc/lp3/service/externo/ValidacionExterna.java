package py.edu.uc.lp3.service.externo;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ValidacionExterna {

    @JsonAlias({"aceptada", "approved", "valido", "valida"})
    private Boolean aprobado;

    private String mensaje;

    private String codigo;

    private Integer danioInfligido;

    private Integer balasRestantes;

    private Boolean granadaLanzada;

    public boolean isAprobado() {
        return !Boolean.FALSE.equals(aprobado);
    }

    public void setAprobado(boolean aprobado) {
        this.aprobado = aprobado;
    }

    public Integer getDanioInfligido() {
        return danioInfligido;
    }

    public void setDanioInfligido(Integer danioInfligido) {
        this.danioInfligido = danioInfligido;
    }

    public Integer getBalasRestantes() {
        return balasRestantes;
    }

    public void setBalasRestantes(Integer balasRestantes) {
        this.balasRestantes = balasRestantes;
    }

    public Boolean getGranadaLanzada() {
        return granadaLanzada;
    }

    public void setGranadaLanzada(Boolean granadaLanzada) {
        this.granadaLanzada = granadaLanzada;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }
}
