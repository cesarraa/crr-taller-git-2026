package py.edu.uc.lp3.exception;

/**
 * Excepción base de la jerarquía del dominio de Counter-Strike 2.
 * Es una excepción no comprobada (unchecked, hereda de RuntimeException),
 * permitiendo una gestión desacoplada en la capa de servicio y un rollback
 * transaccional automático en Spring Data JPA sin contaminar firmas de métodos.
 */
public class DominioCS2Exception extends RuntimeException {

    public DominioCS2Exception(String mensaje) {
        super(mensaje);
    }

    public DominioCS2Exception(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
