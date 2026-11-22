package py.edu.uc.lp3.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("FUSIL")
public class Fusil extends Arma {

    protected Fusil() {
        super("AK-47", 2700f, 36, 3.47f, 30);
    }

    public Fusil(String nombre, float precio, int dano, float peso, int municionMax) {
        super(nombre, precio, dano, peso, municionMax);
    }

    public Fusil(String nombre, float precio, int dano) {
        this(nombre, precio, dano, 3.47f, 30);
    }

    @Override
    public String describirComportamiento() {
        return "Fusil " + getNombre() + ": rifle de asalto versátil para combate a media distancia.";
    }
}
