package py.edu.uc.lp3.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Datos de entrada para crear un jugador.")
public class JugadorRequest {

    @Schema(description = "Alias único del combatiente", example = "tomasin")
    private String alias;

    @Schema(description = "Equipo del combatiente", example = "Terrorista", allowableValues = {"Terrorista", "Counter-Terrorista"})
    private String bando;

    @Schema(description = "Salud inicial; entre 0 y 100", example = "100")
    private Integer salud;

    @Schema(description = "Dinero disponible; entre 0 y 16000", example = "800")
    private Integer dinero;

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getBando() {
        return bando;
    }

    public void setBando(String bando) {
        this.bando = bando;
    }

    public Integer getSalud() {
        return salud;
    }

    public void setSalud(Integer salud) {
        this.salud = salud;
    }

    public Integer getDinero() {
        return dinero;
    }

    public void setDinero(Integer dinero) {
        this.dinero = dinero;
    }
}
