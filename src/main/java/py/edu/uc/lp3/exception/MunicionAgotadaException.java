package py.edu.uc.lp3.exception;

/**
 * Excepción hija 1 del dominio CS2.
 * Lanzada cuando se intenta efectuar un disparo con un arma cuya munición actual es 0.
 * Refleja el estado físico del arma en el juego (percutor vacío que requiere recarga).
 */
public class MunicionAgotadaException extends DominioCS2Exception {

    private final String nombreArma;
    private final int municionActual;
    private final int municionMax;

    public MunicionAgotadaException(String nombreArma, int municionActual, int municionMax) {
        super(String.format("El arma '%s' no tiene munición disponible para disparar (%d/%d). Requiere recarga inmediata.",
                nombreArma, municionActual, municionMax));
        this.nombreArma = nombreArma;
        this.municionActual = municionActual;
        this.municionMax = municionMax;
    }

    public String getNombreArma() {
        return nombreArma;
    }

    public int getMunicionActual() {
        return municionActual;
    }

    public int getMunicionMax() {
        return municionMax;
    }
}
