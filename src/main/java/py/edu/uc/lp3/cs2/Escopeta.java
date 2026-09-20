package py.edu.uc.lp3.cs2;

/**
 * Representa un arma de tipo escopeta. Hereda de Arma.
 */
public class Escopeta extends Arma {

    private int cartuchos;
    private float dispersion;

    public Escopeta(String nombre, float precio, int dano, float peso, int municionMax,
                     int cartuchos, float dispersion) {
        super(nombre, precio, dano, peso, municionMax);
        this.cartuchos = cartuchos;
        this.dispersion = dispersion;
    }
    
    @Override
    public String describirComportamiento() {
    	return "Disparo de perdigones de alto impacto a quemarropa con dispersión de " + dispersion + "%.";
    }

    public int getCartuchos() {
        return cartuchos;
    }

    public void setCartuchos(int cartuchos) {
        this.cartuchos = cartuchos;
    }

    public float getDispersion() {
        return dispersion;
    }

    public void setDispersion(float dispersion) {
        this.dispersion = dispersion;
    }
}
