package py.edu.uc.lp3.domain;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.persistence.*;
import py.edu.uc.lp3.exception.EconomiaInvalidaException;
import py.edu.uc.lp3.exception.MunicionAgotadaException;

/**
 * Entidad de dominio persistente que modela un arma de Counter-Strike 2.
 * Mapeada con JPA / Hibernate para persistencia en base de datos H2 con Spring Data (Single Table).
 * Soporta discriminación polimórfica en JSON con Jackson a través del atributo 'tipo'.
 */
@Entity
@Table(name = "armas")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_arma", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("ARMA")
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "tipo",
        defaultImpl = Arma.class
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Francotirador.class, name = "FRANCOTIRADOR"),
        @JsonSubTypes.Type(value = Pistola.class, name = "PISTOLA"),
        @JsonSubTypes.Type(value = Escopeta.class, name = "ESCOPETA"),
        @JsonSubTypes.Type(value = Subfusil.class, name = "SUBFUSIL"),
        @JsonSubTypes.Type(value = Granada.class, name = "GRANADA")
})
public class Arma {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Column(nullable = false)
    protected String nombre;

    @Column(nullable = false)
    protected float precio;

    @Column(nullable = false)
    @JsonAlias({"danio", "daño"})
    protected int dano;

    @Column(nullable = false)
    protected float peso;

    @Column(nullable = false)
    @JsonAlias({"municionMaxima", "municion_max", "maxAmmo"})
    protected int municionMax;

    @Column(nullable = false)
    @JsonAlias({"municion", "municion_actual", "currentAmmo"})
    protected int municionActual;

    /**
     * Constructor por defecto requerido por JPA / Hibernate.
     * Mantiene visibilidad pública/protegida para frameworks y persistencia.
     */
    public Arma() {
    }

    /**
     * Constructor sobrecargado (completo).
     * Valida invariantes de dominio para asegurar un estado legal.
     */
    public Arma(String nombre, float precio, int dano, float peso, int municionMax) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del arma no puede estar vacío.");
        }
        if (precio < 0) {
            throw new IllegalArgumentException("El precio no puede ser negativo.");
        }
        if (precio > 16000f) {
            throw new EconomiaInvalidaException("El precio $" + precio + " supera el tope de $16,000 en la economía de CS2.", precio, 16000f);
        }
        if (dano <= 0) {
            throw new IllegalArgumentException("El daño base debe ser mayor a cero.");
        }
        if (peso <= 0) {
            throw new IllegalArgumentException("El peso debe ser mayor a cero.");
        }
        if (municionMax <= 0) {
            throw new IllegalArgumentException("La munición máxima debe ser mayor a cero.");
        }

        this.nombre = nombre.trim();
        this.precio = precio;
        this.dano = dano;
        this.peso = peso;
        this.municionMax = municionMax;
        this.municionActual = municionMax;
    }

    /**
     * Constructor simple (versión esencial).
     * Inicializa el arma con peso estándar (2.5 kg) y capacidad por defecto (20 balas).
     */
    public Arma(String nombre, float precio, int dano) {
        this(nombre, precio, dano, 2.5f, 20);
    }

    /**
     * Identificador del subtipo para serialización JSON y discriminación polimórfica.
     */
    @Transient
    public String getTipo() {
        return getClass().getSimpleName().toUpperCase();
    }

    /**
     * Método polimórfico del dominio.
     * Ofrece una implementación base que las clases hijas concretas sobreescriben.
     */
    public String describirComportamiento() {
        return "Arma " + (nombre != null ? nombre : "estándar")
                + ": armamento balístico reglamentario de Counter-Strike 2.";
    }

    // ========================================================
    // Sobrecarga de mensajes del dominio (Overloading)
    // Misma acción ('disparar'), distintas firmas y contextos.
    // Lanza MunicionAgotadaException si municionActual <= 0.
    // ========================================================

    /**
     * Sobrecarga 1: Disparo simple a quemarropa sin parámetros adicionales.
     */
    public String disparar() {
        if (municionActual <= 0) {
            throw new MunicionAgotadaException(nombre, municionActual, municionMax);
        }
        municionActual--;
        return nombre + " efectuó un disparo simple (" + dano + " daño base). Munición restante: "
                + municionActual + "/" + municionMax + ".";
    }

    /**
     * Sobrecarga 2: Disparo especificando distancia en metros hacia el objetivo.
     * Aplica atenuación de daño balístico según la distancia recorrida.
     */
    public String disparar(int distanciaMetros) {
        if (distanciaMetros < 0) {
            throw new IllegalArgumentException("La distancia del objetivo no puede ser negativa.");
        }
        if (municionActual <= 0) {
            throw new MunicionAgotadaException(nombre, municionActual, municionMax);
        }
        municionActual--;
        int danoEfectivo = calcularDanioEfectivo(distanciaMetros, false);
        return nombre + " disparó a " + distanciaMetros + " metros infligiendo " + danoEfectivo
                + " de daño efectivo. Munición: " + municionActual + "/" + municionMax + ".";
    }

    /**
     * Sobrecarga 3: Disparo especificando distancia e indicando si fue impacto a la cabeza (headshot).
     */
    public String disparar(int distanciaMetros, boolean tiroALaCabeza) {
        if (distanciaMetros < 0) {
            throw new IllegalArgumentException("La distancia del objetivo no puede ser negativa.");
        }
        if (municionActual <= 0) {
            throw new MunicionAgotadaException(nombre, municionActual, municionMax);
        }
        municionActual--;
        int danoEfectivo = calcularDanioEfectivo(distanciaMetros, tiroALaCabeza);
        String zona = tiroALaCabeza ? "¡HEADSHOT!" : "Impacto corporal";
        return nombre + " disparó a " + distanciaMetros + "m (" + zona + ") causando " + danoEfectivo
                + " de daño. Munición: " + municionActual + "/" + municionMax + ".";
    }

    /**
     * Regla de negocio balística: cálculo del daño efectivo según distancia y punto de impacto.
     */
    public int calcularDanioEfectivo(int distanciaMetros, boolean tiroALaCabeza) {
        double atenuacion = Math.max(0.20, 1.0 - (distanciaMetros * 0.0075));
        double multiplicador = tiroALaCabeza ? 2.5 : 1.0;
        return (int) Math.round(dano * atenuacion * multiplicador);
    }

    /**
     * Mensaje de recarga que restaura la munición a su capacidad máxima permitida.
     */
    public String recargar() {
        this.municionActual = this.municionMax;
        return nombre + " recargada. Munición actual: " + municionActual + "/" + municionMax + ".";
    }

    // ========================================================
    // Getters y Setters con validación de invariantes
    // ========================================================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String obtenerNombre() {
        return nombre;
    }

    public int obtenerPrecio() {
        return (int) precio;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del arma no puede estar vacío.");
        }
        this.nombre = nombre.trim();
    }

    public float getPrecio() {
        return precio;
    }

    public void setPrecio(float precio) {
        if (precio < 0) {
            throw new IllegalArgumentException("El precio no puede ser negativo.");
        }
        if (precio > 16000f) {
            throw new EconomiaInvalidaException("El precio $" + precio + " supera el tope de $16,000 en la economía de CS2.", precio, 16000f);
        }
        this.precio = precio;
    }

    public int getDano() {
        return dano;
    }

    public void setDano(int dano) {
        if (dano <= 0) {
            throw new IllegalArgumentException("El daño debe ser mayor a cero.");
        }
        this.dano = dano;
    }

    public float getPeso() {
        return peso;
    }

    public void setPeso(float peso) {
        if (peso <= 0) {
            throw new IllegalArgumentException("El peso debe ser mayor a cero.");
        }
        this.peso = peso;
    }

    public int getMunicionMax() {
        return municionMax;
    }

    public void setMunicionMax(int municionMax) {
        if (municionMax <= 0) {
            throw new IllegalArgumentException("La munición máxima debe ser mayor a cero.");
        }
        this.municionMax = municionMax;
        if (this.municionActual > municionMax) {
            this.municionActual = municionMax;
        }
    }

    public int getMunicionActual() {
        return municionActual;
    }

    public void setMunicionActual(int municionActual) {
        if (municionActual < 0) {
            throw new IllegalArgumentException("La munición actual no puede ser negativa.");
        }
        if (municionActual > this.municionMax) {
            throw new IllegalArgumentException("La munición actual (" + municionActual
                    + ") no puede exceder la capacidad máxima (" + this.municionMax + ").");
        }
        this.municionActual = municionActual;
    }
}
