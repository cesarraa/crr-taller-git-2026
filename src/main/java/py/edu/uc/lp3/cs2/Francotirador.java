package py.edu.uc.lp3.cs2;

/**
 * Representa un arma especializada en disparos a larga distancia. Hereda de Arma.
 */
public class Francotirador extends Arma {

    private int zoom;

    public Francotirador(String nombre, float precio, int dano, float peso, int municionMax, int zoom) {
        super(nombre, precio, dano, peso, municionMax);
        if (zoom <= 0) {
        	throw new IllegalArgumentException("El nivel de aumento del zoom debe ser mayor a cero!");
        }
        this.zoom = zoom;
    }
    
    @Override
    public String describirComportamiento() {
    	return "Disparo de precisión letal a larga distancia utilizando mira telescópica con zoom óptico x" + zoom + " y alta penalización de movimiento al disparar sin apuntar.";
    }

    /**
     * Activa el zoom del francotirador para apuntar a larga distancia.
     */
    public void activarZoom() {
        System.out.println(nombre + " activó el zoom x" + zoom);
    }

    public int getZoom() {
        return zoom;
    }

    public void setZoom(int zoom) {
        this.zoom = zoom;
    }
}
