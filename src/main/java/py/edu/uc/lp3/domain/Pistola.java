package py.edu.uc.lp3.domain;

/**
 * Representa un arma secundaria tipo pistola en Counter-Strike 2.
 * Especialización de Arma caracterizada por alta movilidad y cadencia de apoyo a corta distancia.
 */
public class Pistola extends Arma {

    private String modoDisparo;
    private int cargador;

    /**
     * Constructor sobrecargado (completo).
     * Recibe todas las especificaciones de arma y características específicas de la pistola.
     */
    public Pistola(String nombre, float precio, int dano, float peso, int municionMax,
                   String modoDisparo, int cargador) {
        super(nombre, precio, dano, peso, municionMax);
        if (cargador <= 0) {
            throw new IllegalArgumentException("La capacidad del cargador debe ser positiva.");
        }
        if (modoDisparo == null || modoDisparo.isBlank()) {
            throw new IllegalArgumentException("El modo de disparo no puede estar vacío.");
        }
        this.modoDisparo = modoDisparo.trim();
        this.cargador = cargador;
    }

    /**
     * Constructor simple.
     * Invoca al constructor base con valores estándar de pistola (1.0 kg, 12 balas)
     * e inicializa modo semiautomático legal por defecto.
     */
    public Pistola(String nombre, float precio, int dano) {
        super(nombre, precio, dano, 1.0f, 12);
        this.modoDisparo = "Semiautomático";
        this.cargador = 12;
    }

    /**
     * Constructor por defecto.
     * Instancia una pistola estándar Glock-18 con valores canónicos de CS2.
     */
    public Pistola() {
        this("Glock-18", 200f, 28, 0.9f, 20, "Semiautomático", 20);
    }

    /**
     * Sobreescritura del método abstracto de Arma.
     * Describe la función táctica de la pistola en rondas de pistolas y eco.
     */
    @Override
    public String describirComportamiento() {
        return "Pistola " + nombre + ": arma secundaria para combate a corta distancia en modo "
                + modoDisparo + " con ciclo rápido de recarga para " + cargador + " balas.";
    }

    public String getModoDisparo() {
        return modoDisparo;
    }

    public void setModoDisparo(String modoDisparo) {
        if (modoDisparo == null || modoDisparo.isBlank()) {
            throw new IllegalArgumentException("El modo de disparo no puede estar vacío.");
        }
        this.modoDisparo = modoDisparo.trim();
    }

    public int getCargador() {
        return cargador;
    }

    public void setCargador(int cargador) {
        if (cargador <= 0) {
            throw new IllegalArgumentException("La capacidad del cargador debe ser positiva.");
        }
        this.cargador = cargador;
    }
}
