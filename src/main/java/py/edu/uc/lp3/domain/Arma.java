package py.edu.uc.lp3.domain;

/**
 * Clase base abstracta que modela un arma genérica de Counter-Strike 2.
 * Define atributos comunes, métodos del dominio y declara el comportamiento
 * polimórfico abstracto que deben sobreescribir las clases hijas.
 */
public abstract class Arma {

    protected String nombre;
    protected float precio;
    protected int dano;
    protected float peso;
    protected int municionMax;
    protected int municionActual;

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
     * Método polimórfico abstracto.
     * Obliga a cada clase hija concreta a definir cómo opera su arma en CS2.
     */
    public abstract String describirComportamiento();

    // ========================================================
    // Sobrecarga de mensajes del dominio (Overloading)
    // Misma acción ('disparar'), distintas firmas y contextos.
    // ========================================================

    /**
     * Sobrecarga 1: Disparo simple a quemarropa sin parámetros adicionales.
     */
    public String disparar() {
        if (municionActual <= 0) {
            return nombre + " no tiene munición disponible. Se requiere recargar.";
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
            return nombre + " sin munición al intentar disparar a " + distanciaMetros + "m.";
        }
        municionActual--;
        double atenuacion = Math.max(0.20, 1.0 - (distanciaMetros * 0.0075));
        int danoEfectivo = (int) Math.round(dano * atenuacion);
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
            return nombre + " sin munición al intentar disparo crítico a " + distanciaMetros + "m.";
        }
        municionActual--;
        double atenuacion = Math.max(0.20, 1.0 - (distanciaMetros * 0.0075));
        double multiplicador = tiroALaCabeza ? 2.5 : 1.0;
        int danoEfectivo = (int) Math.round(dano * atenuacion * multiplicador);
        String zona = tiroALaCabeza ? "¡HEADSHOT!" : "Impacto corporal";
        return nombre + " disparó a " + distanciaMetros + "m (" + zona + ") causando " + danoEfectivo
                + " de daño. Munición: " + municionActual + "/" + municionMax + ".";
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

    @Override
    public String toString() {
        return nombre + " [precio=" + precio + ", dano=" + dano + ", peso=" + peso
                + ", municion=" + municionActual + "/" + municionMax + "]";
    }
}
