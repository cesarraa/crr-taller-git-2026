package py.edu.uc.lp3.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "escenarios_validados")
@Schema(description = "Jugada CS2 guardada después de que A2 y A3 aceptaron sus validaciones.")
public class EscenarioValidado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador de la jugada persistida", example = "1")
    private Long id;

    @Schema(description = "Nombre del escenario ejecutado", example = "Emboscada en Mirage")
    private String nombreEscenario;

    @Schema(description = "Descripción libre enviada por el cliente")
    private String descripcion;

    @Schema(description = "Arma local involucrada", example = "AWP")
    private String armaNombre;

    @Schema(description = "Jugador local involucrado", example = "feliz")
    private String jugadorAlias;

    @Schema(description = "Distancia del disparo en metros", example = "35")
    private Integer distancia;

    @Schema(description = "Indica si el disparo fue a la cabeza", example = "false")
    private Boolean headshot;

    @Schema(description = "Respuesta de la validación de munición de Giovanni (A2)", example = "A2: munición permitida")
    private String resultadoA2;

    @Schema(description = "Respuesta de la validación de munición y granada de Luis (A3)", example = "daño 19, balas restantes 11, granada lanzada true")
    private String resultadoA3;

    @Schema(description = "Fecha y hora en que ambas validaciones aceptaron y se guardó la jugada")
    private LocalDateTime registradoEn;

    protected EscenarioValidado() {
    }

    public EscenarioValidado(String nombreEscenario, String descripcion, String armaNombre, String jugadorAlias,
                             Integer distancia, Boolean headshot, String resultadoA2, String resultadoA3,
                             LocalDateTime registradoEn) {
        this.nombreEscenario = nombreEscenario;
        this.descripcion = descripcion;
        this.armaNombre = armaNombre;
        this.jugadorAlias = jugadorAlias;
        this.distancia = distancia;
        this.headshot = headshot;
        this.resultadoA2 = resultadoA2;
        this.resultadoA3 = resultadoA3;
        this.registradoEn = registradoEn;
    }

    public Long getId() {
        return id;
    }

    public String getNombreEscenario() {
        return nombreEscenario;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getArmaNombre() {
        return armaNombre;
    }

    public String getJugadorAlias() {
        return jugadorAlias;
    }

    public Integer getDistancia() {
        return distancia;
    }

    public Boolean getHeadshot() {
        return headshot;
    }

    public String getResultadoA2() {
        return resultadoA2;
    }

    public String getResultadoA3() {
        return resultadoA3;
    }

    public LocalDateTime getRegistradoEn() {
        return registradoEn;
    }
}
