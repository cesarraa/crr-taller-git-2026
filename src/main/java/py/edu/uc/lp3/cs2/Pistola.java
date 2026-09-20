package py.edu.uc.lp3.cs2;

/**
 * Representa un arma de tipo pistola. Hereda de Arma.
 */
public class Pistola extends Arma {

    private String modoDisparo;
    private int cargador;

    public Pistola(String nombre, float precio, int daño, float peso, int municionMax,
                    String modoDisparo, int cargador) {
        super(nombre, precio, daño, peso, municionMax);
        this.modoDisparo = modoDisparo;
        this.cargador = cargador;
    }

    public String getModoDisparo() {
        return modoDisparo;
    }

    public void setModoDisparo(String modoDisparo) {
        this.modoDisparo = modoDisparo;
    }

    public int getCargador() {
        return cargador;
    }

    public void setCargador(int cargador) {
        this.cargador = cargador;
    }
}