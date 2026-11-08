package py.edu.uc.lp3.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

/**
 * Especialización de Granada de humo (Smoke).
 */
@Entity
@DiscriminatorValue("GRANADA_HUMO")
public class GranadaHumo extends Granada {

    public GranadaHumo(String nombre, float precio, int dano, float peso, int municionMax,
                       float radioExplosion) {
        super(nombre, precio, dano, peso, municionMax, GranadaTipo.HUMO, radioExplosion);
    }

    public GranadaHumo() {
        super("Smoke Grenade", 300f, 1, GranadaTipo.HUMO);
    }
}
