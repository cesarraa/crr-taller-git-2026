package py.edu.uc.lp3.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("GRANADA_INCENDIARIA")
public class GranadaIncendiaria extends Granada {

    protected GranadaIncendiaria() {
        super("Molotov", 400f, 40, GranadaTipo.INCENDIARIA);
    }

    public GranadaIncendiaria(String nombre, float precio, int dano, float peso, int municionMax,
                              float radioExplosion) {
        super(nombre, precio, dano, peso, municionMax, GranadaTipo.INCENDIARIA, radioExplosion);
    }
}
