package py.edu.uc.lp3.domain;

/**
 * Representa un subfusil (SMG) en Counter-Strike 2. Hereda de Arma.
 */
public class Subfusil extends Arma {

    private String modoDisparo;
    private int cargador;

    public Subfusil(String nombre, float precio, int dano, float peso, int municionMax,
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

    public Subfusil(String nombre, float precio, int dano, int cargador) {
        super(nombre, precio, dano, 2.8f, 100);
        if (cargador <= 0) {
            throw new IllegalArgumentException("La capacidad del cargador debe ser positiva.");
        }
        this.modoDisparo = "Automático";
        this.cargador = cargador;
    }

    public Subfusil() {
        this("MP9", 1250f, 26, 2.5f, 120, "Automático", 30);
    }

    @Override
    public String describirComportamiento() {
        return "Subfusil " + nombre + ": alta cadencia de fuego en modo " + modoDisparo
                + " con cargador de " + cargador + " balas y excelente bonificación por baja.";
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
