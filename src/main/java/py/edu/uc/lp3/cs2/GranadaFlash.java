package py.edu.uc.lp3.cs2;

/**
 * Especialización de Granada de tipo flash (cegadora).
 * No agrega atributos ni métodos propios respecto al diagrama.
 */
public class GranadaFlash extends Granada {

    public GranadaFlash(String nombre, float precio, int daño, float peso, int municionMax,
                         float radioExplosion) {
        super(nombre, precio, daño, peso, municionMax, GranadaTipo.FLASH, radioExplosion);
    }
}