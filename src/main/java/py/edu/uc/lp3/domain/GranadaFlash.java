package py.edu.uc.lp3.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("GRANADA_FLASH")
public class GranadaFlash extends Granada {

    protected GranadaFlash() {
        super("Flashbang", 200f, 1, GranadaTipo.FLASH);
    }

    public GranadaFlash(String nombre, float precio, int dano, float peso, int municionMax,
                        float radioExplosion) {
        super(nombre, precio, dano, peso, municionMax, GranadaTipo.FLASH, radioExplosion);
    }
}
