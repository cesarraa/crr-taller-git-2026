package py.edu.uc.lp3.exception;

/**
 * Excepción para violaciones generales de reglas de negocio en la capa de servicios.
 * Hereda de DominioCS2Exception para integrarse armónicamente a la jerarquía del dominio.
 */
public class ReglaNegocioException extends DominioCS2Exception {

    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }

    public ReglaNegocioException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
