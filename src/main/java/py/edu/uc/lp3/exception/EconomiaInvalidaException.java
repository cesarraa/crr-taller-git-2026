package py.edu.uc.lp3.exception;

/**
 * Excepción hija 2 del dominio CS2.
 * Lanzada cuando se intenta registrar, adquirir o modificar un arma violando los topes
 * o reglas financieras de Counter-Strike 2 (el rango reglamentario es $0 a $16,000 por arma o presupuesto).
 */
public class EconomiaInvalidaException extends DominioCS2Exception {

    private final float precio;
    private final float limiteMaximo;

    public EconomiaInvalidaException(String mensaje, float precio, float limiteMaximo) {
        super(mensaje);
        this.precio = precio;
        this.limiteMaximo = limiteMaximo;
    }

    public EconomiaInvalidaException(float precio) {
        this(String.format("El precio $%.2f viola la regla económica de CS2 (rango reglamentario: $0 a $16,000).", precio),
                precio, 16000f);
    }

    public EconomiaInvalidaException(String mensaje) {
        this(mensaje, 0f, 16000f);
    }

    public float getPrecio() {
        return precio;
    }

    public float getLimiteMaximo() {
        return limiteMaximo;
    }
}
