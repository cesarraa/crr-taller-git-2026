package py.edu.uc.lp3.cs2;

/**
 * Especialización de Granada destinada a producir humo.
 * No agrega atributos ni métodos propios respecto al diagrama.
 */
public class GranadaHumo extends Granada {

    public GranadaHumo(String nombre, float precio, int daño, float peso, int municionMax,
                        float radioExplosion) {
        super(nombre, precio, daño, peso, municionMax, GranadaTipo.HUMO, radioExplosion);
    }
}