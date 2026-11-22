package py.edu.uc.lp3.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("ESCOPETA")
public class Escopeta extends Arma {

    private int cartuchos;
    private float dispersion;

    protected Escopeta() {
        this("Nova", 1050f, 60, 3.5f, 32, 8, 18.0f);
    }

    public Escopeta(String nombre, float precio, int dano, float peso, int municionMax,
                    int cartuchos, float dispersion) {
        super(nombre, precio, dano, peso, municionMax);
        this.cartuchos = cartuchos;
        this.dispersion = dispersion;
    }

    public Escopeta(String nombre, float precio, int dano, int cartuchos) {
        this(nombre, precio, dano, 3.8f, 32, cartuchos, 18.5f);
    }

    @Override
    public String describirComportamiento() {
        return "Escopeta " + getNombre() + ": disparo múltiple de perdigones a quemarropa con dispersión de "
                + dispersion + "% y recarga individual de " + cartuchos + " cartuchos.";
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
