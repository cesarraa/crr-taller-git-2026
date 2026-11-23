package py.edu.uc.lp3.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Pedido del escenario propio: valida el arma y el jugador locales contra A2 y A3 antes de guardar.")
public class EscenarioRequest {

    @Schema(description = "ID del arma local involucrada", example = "1")
    private Long armaId;

    @Schema(description = "ID del jugador local involucrado", example = "1")
    private Long jugadorId;

    @Schema(description = "Distancia del disparo en metros", example = "35")
    private Integer distancia;

    @Schema(description = "Indica si el disparo es a la cabeza", example = "false")
    private Boolean headshot;

    @Schema(description = "Nombre del escenario; si se omite se usa 'Emboscada validada'", example = "Emboscada en Mirage")
    private String nombreEscenario;

    @Schema(description = "Descripción libre de la jugada", example = "AWP desde ventana sobre jugador con dinero suficiente")
    private String descripcion;

    public Long getArmaId() {
        return armaId;
    }

    public void setArmaId(Long armaId) {
        this.armaId = armaId;
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

    public Boolean getHeadshot() {
        return headshot;
    }

    public void setHeadshot(Boolean headshot) {
        this.headshot = headshot;
    }

    public String getNombreEscenario() {
        return nombreEscenario;
    }

    public void setNombreEscenario(String nombreEscenario) {
        this.nombreEscenario = nombreEscenario;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
