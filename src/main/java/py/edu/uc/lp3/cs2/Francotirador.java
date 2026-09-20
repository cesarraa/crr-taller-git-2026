package py.edu.uc.lp3.cs2;

/**
 * Representa un arma especializada en disparos a larga distancia. Hereda de Arma.
 */
public class Francotirador extends Arma {

    private int zoom;

    public Francotirador(String nombre, float precio, int daño, float peso, int municionMax, int zoom) {
        super(nombre, precio, daño, peso, municionMax);
        this.zoom = zoom;
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