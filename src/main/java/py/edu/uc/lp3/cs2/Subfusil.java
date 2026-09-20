package py.edu.uc.lp3.cs2;

/**
 * Representa un arma automática de tamaño reducido. Hereda de Arma.
 */
public class Subfusil extends Arma {

    private String modoDisparo;
    private int cargador;

    public Subfusil(String nombre, float precio, int daño, float peso, int municionMax,
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