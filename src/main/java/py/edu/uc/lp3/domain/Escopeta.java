package py.edu.uc.lp3.domain;

/**
 * Representa un arma de tipo escopeta en Counter-Strike 2. Hereda de Arma.
 */
public class Escopeta extends Arma {

    private int cartuchos;
    private float dispersion;

    public Escopeta(String nombre, float precio, int dano, float peso, int municionMax,
                    int cartuchos, float dispersion) {
        super(nombre, precio, dano, peso, municionMax);
        if (cartuchos <= 0) {
            throw new IllegalArgumentException("La cantidad de cartuchos debe ser mayor a cero.");
        }
        if (dispersion <= 0) {
            throw new IllegalArgumentException("La dispersión debe ser mayor a cero.");
        }
        this.cartuchos = cartuchos;
        this.dispersion = dispersion;
    }

    public Escopeta(String nombre, float precio, int dano, int cartuchos) {
        super(nombre, precio, dano, 3.8f, 32);
        if (cartuchos <= 0) {
            throw new IllegalArgumentException("La cantidad de cartuchos debe ser mayor a cero.");
        }
        this.cartuchos = cartuchos;
        this.dispersion = 18.5f;
    }

    public Escopeta() {
        this("Nova", 1050f, 60, 3.5f, 32, 8, 18.0f);
    }

    @Override
    public String describirComportamiento() {
        return "Escopeta " + nombre + ": disparo múltiple de perdigones a quemarropa con dispersión de "
                + dispersion + "% y recarga individual de " + cartuchos + " cartuchos.";
    }

    public int getCartuchos() {
        return cartuchos;
    }

    public void setCartuchos(int cartuchos) {
        if (cartuchos <= 0) {
            throw new IllegalArgumentException("La cantidad de cartuchos debe ser mayor a cero.");
        }
        this.cartuchos = cartuchos;
    }

    public float getDispersion() {
        return dispersion;
    }

    public void setDispersion(float dispersion) {
        if (dispersion <= 0) {
            throw new IllegalArgumentException("La dispersión debe ser mayor a cero.");
        }
        this.dispersion = dispersion;
    }
}
