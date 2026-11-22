package py.edu.uc.lp3.exception;

public class ValidacionExternaRechazadaException extends DominioCS2Exception {

    private final String origen;
    private final int statusExterno;
    private final String cuerpoExterno;

    public ValidacionExternaRechazadaException(String origen, int statusExterno, String cuerpoExterno) {
        super("La validación de la API " + origen + " rechazó el escenario (HTTP " + statusExterno + ").");
        this.origen = origen;
        this.statusExterno = statusExterno;
        this.cuerpoExterno = cuerpoExterno;
    }

    public String getOrigen() {
        return origen;
    }

    public int getStatusExterno() {
        return statusExterno;
    }

    public String getCuerpoExterno() {
        return cuerpoExterno;
    }
}
