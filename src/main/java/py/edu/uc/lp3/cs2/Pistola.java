package py.edu.uc.lp3.cs2;

/**
 * Representa un arma de tipo pistola. Hereda de Arma.
 */
public class Pistola extends Arma {

    private String modoDisparo;
    private int cargador;

    public Pistola(String nombre, float precio, int dano, float peso, int municionMax,
                    String modoDisparo, int cargador) {
        super(nombre, precio, dano, peso, municionMax);
        if (cargador <= 0) {
        	throw new IllegalArgumentException("La capacidad del cargador debe ser positiva!");
        }
        if (modoDisparo == null || modoDisparo.isBlank()) {
        	throw new IllegalArgumentException("El modo de disparo es obligatorio!");
        }
        this.modoDisparo = modoDisparo;
        this.cargador = cargador;
    }
    
    @Override
    public String describirComportamiento() {
    	return "Disparo de apoyo a corta distancia en modo " + modoDisparo + " con ciclo rápido de recarga para " + cargador + " balas.";
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
