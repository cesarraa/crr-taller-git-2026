package py.edu.uc.lp3.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

/**
 * Especialización de Granada incendiaria (Molotov / Incendiary).
 */
@Entity
@DiscriminatorValue("GRANADA_INCENDIARIA")
public class GranadaIncendiaria extends Granada {

    public GranadaIncendiaria(String nombre, float precio, int dano, float peso, int municionMax,
                              float radioExplosion) {
        super(nombre, precio, dano, peso, municionMax, GranadaTipo.INCENDIARIA, radioExplosion);
    }

    public GranadaIncendiaria() {
        super("Molotov", 400f, 40, GranadaTipo.INCENDIARIA);
    }
}
