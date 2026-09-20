package py.edu.uc.lp3.cs2;

/**
 * Representa un arma automática de tamano reducido. Hereda de Arma.
 */
public class Subfusil extends Arma {

    private String modoDisparo;
    private int cargador;

    public Subfusil(String nombre, float precio, int dano, float peso, int municionMax,
                     String modoDisparo, int cargador) {
        super(nombre, precio, dano, peso, municionMax);
        this.modoDisparo = modoDisparo;
        this.cargador = cargador;
    }
    
    @Override
    public String describirComportamiento() {
    	return "Cadencia de fuego elevada en ráfagas " + modoDisparo + " con alta movilidad en combate cerrado.";
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
