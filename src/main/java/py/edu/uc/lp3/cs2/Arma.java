package py.edu.uc.lp3.cs2;

/**
 * Clase base que representa un arma genérica.
 * Contiene los atributos y comportamientos comunes a todos los tipos de armas.
 */
public abstract class Arma {

    protected String nombre;
    protected float precio;
    protected int daño;
    protected float peso;
    protected int municionMax;
    protected int municionActual;

    public Arma(String nombre, float precio, int daño, float peso, int municionMax) {
        this.nombre = nombre;
        this.precio = precio;
        this.daño = daño;
        this.peso = peso;
        this.municionMax = municionMax;
        this.municionActual = municionMax;
    }

    /**
     * Ejecuta el disparo del arma, consumiendo una unidad de munición si es posible.
     */
    public void disparar() {
        if (municionActual > 0) {
            municionActual--;
            System.out.println(nombre + " disparó. Munición restante: " + municionActual + "/" + municionMax);
        } else {
            System.out.println(nombre + " no tiene munición. Es necesario recargar.");
        }
    }

    /**
     * Recarga el arma hasta su capacidad máxima de munición.
     */
    public void recargar() {
        municionActual = municionMax;
        System.out.println(nombre + " ha sido recargada. Munición: " + municionActual + "/" + municionMax);
    }

    public int obtenerPrecio() {
        return (int) precio;
    }

    public String obtenerNombre() {
        return nombre;
    }

    // Getters y setters

    public float getPrecio() {
        return precio;
    }

    public void setPrecio(float precio) {
        this.precio = precio;
    }

    public int getDaño() {
        return daño;
    }

    public void setDaño(int daño) {
        this.daño = daño;
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

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public String toString() {
        return nombre + " [precio=" + precio + ", daño=" + daño + ", peso=" + peso
                + ", munición=" + municionActual + "/" + municionMax + "]";
    }
}