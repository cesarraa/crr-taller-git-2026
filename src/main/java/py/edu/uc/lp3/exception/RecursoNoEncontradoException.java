package py.edu.uc.lp3.exception;

/**
 * Excepción de negocio lanzada cuando un recurso solicitado no existe en la base de datos.
 */
public class RecursoNoEncontradoException extends RuntimeException {

    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
