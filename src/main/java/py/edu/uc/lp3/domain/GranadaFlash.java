package py.edu.uc.lp3.domain;

/**
 * Especialización de Granada cegadora (Flashbang).
 */
public class GranadaFlash extends Granada {

    public GranadaFlash(String nombre, float precio, int dano, float peso, int municionMax,
                        float radioExplosion) {
        super(nombre, precio, dano, peso, municionMax, GranadaTipo.FLASH, radioExplosion);
    }

    public GranadaFlash() {
        super("Flashbang", 200f, 1, GranadaTipo.FLASH);
    }
}
