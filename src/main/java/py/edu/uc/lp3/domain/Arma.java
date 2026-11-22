package py.edu.uc.lp3.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.persistence.*;

@Entity
@Table(name = "armas")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_arma", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("ARMA")
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "tipo"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Fusil.class, name = "FUSIL"),
        @JsonSubTypes.Type(value = Francotirador.class, name = "FRANCOTIRADOR"),
        @JsonSubTypes.Type(value = Pistola.class, name = "PISTOLA"),
        @JsonSubTypes.Type(value = Escopeta.class, name = "ESCOPETA"),
        @JsonSubTypes.Type(value = Subfusil.class, name = "SUBFUSIL"),
        @JsonSubTypes.Type(value = Granada.class, name = "GRANADA"),
        @JsonSubTypes.Type(value = GranadaFlash.class, name = "GRANADA_FLASH"),
        @JsonSubTypes.Type(value = GranadaHumo.class, name = "GRANADA_HUMO"),
        @JsonSubTypes.Type(value = GranadaIncendiaria.class, name = "GRANADA_INCENDIARIA")
})
public abstract class Arma {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private float precio;

    @Column(nullable = false)
    private int dano;

    @Column(nullable = false)
    private float peso;

    @Column(nullable = false)
    private int municionMax;

    @Column(nullable = false)
    private int municionActual;

    protected Arma() {
    }

    protected Arma(String nombre, float precio, int dano, float peso, int municionMax) {
        this.nombre = nombre;
        this.precio = precio;
        this.dano = dano;
        this.peso = peso;
        this.municionMax = municionMax;
        this.municionActual = municionMax;
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String obtenerNombre() {
        return nombre;
    }

    public float getPrecio() {
        return precio;
    }

    public void setPrecio(float precio) {
        this.precio = precio;
    }

    public int obtenerPrecio() {
        return (int) precio;
    }

    public int getDano() {
        return dano;
    }

    public void setDano(int dano) {
        this.dano = dano;
    }

    public float getPeso() {
        return peso;
    }

    public void setPeso(float peso) {
        this.peso = peso;
    }

    public int getMunicionMax() {
        return municionMax;
    }

    public void setMunicionMax(int municionMax) {
        this.municionMax = municionMax;
    }

    public int getMunicionActual() {
        return municionActual;
    }

    public void setMunicionActual(int municionActual) {
        this.municionActual = municionActual;
    }

    @Transient
    @JsonIgnore
    public String getTipo() {
        return getClass().getSimpleName().toUpperCase();
    }

    public abstract String describirComportamiento();
}
