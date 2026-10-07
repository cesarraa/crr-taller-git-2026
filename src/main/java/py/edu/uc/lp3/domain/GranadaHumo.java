package py.edu.uc.lp3.domain;

/**
 * Especialización de Granada de humo (Smoke).
 */
public class GranadaHumo extends Granada {

    public GranadaHumo(String nombre, float precio, int dano, float peso, int municionMax,
                       float radioExplosion) {
        super(nombre, precio, dano, peso, municionMax, GranadaTipo.HUMO, radioExplosion);
    }

    public GranadaHumo() {
        super("Smoke Grenade", 300f, 1, GranadaTipo.HUMO);
    }
}
