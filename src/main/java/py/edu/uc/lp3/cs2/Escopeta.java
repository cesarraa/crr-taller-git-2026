package py.edu.uc.lp3.cs2;

/**
 * Representa un arma de tipo escopeta. Hereda de Arma.
 */
public class Escopeta extends Arma {

    private int cartuchos;
    private float dispersion;

    public Escopeta(String nombre, float precio, int daño, float peso, int municionMax,
                     int cartuchos, float dispersion) {
        super(nombre, precio, daño, peso, municionMax);
        this.cartuchos = cartuchos;
        this.dispersion = dispersion;
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