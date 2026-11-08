package py.edu.uc.lp3.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

/**
 * Especialización de Granada cegadora (Flashbang).
 */
@Entity
@DiscriminatorValue("GRANADA_FLASH")
public class GranadaFlash extends Granada {

    public GranadaFlash(String nombre, float precio, int dano, float peso, int municionMax,
                        float radioExplosion) {
        super(nombre, precio, dano, peso, municionMax, GranadaTipo.FLASH, radioExplosion);
    }

    public GranadaFlash() {
        super("Flashbang", 200f, 1, GranadaTipo.FLASH);
    }
}
