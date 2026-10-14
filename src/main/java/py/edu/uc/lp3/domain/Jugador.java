package py.edu.uc.lp3.domain;

/**
 * Representa a un combatiente u operador en Counter-Strike 2.
 * Pertenece a una jerarquía/familia distinta e independiente a la de Arma.
 *
 * Implementa la interfaz UICapable a su modo:
 * No sobreescribe renderizarUI(), reutilizando la implementación por defecto (default method)
 * provista por la interfaz, y personalizando los recursos de radar y HUD.
 */
public class Jugador implements UICapable {

    private String alias;
    private String bando; // "Terrorista" o "Antiterrorista"
    private int salud;    // 0 a 100
    private int dinero;   // 0 a 16000

    public Jugador(String alias, String bando, int salud, int dinero) {
        if (alias == null || alias.isBlank()) {
            throw new IllegalArgumentException("El alias del jugador no puede estar vacío.");
        }
        if (bando == null || bando.isBlank()) {
            throw new IllegalArgumentException("El bando del jugador no puede estar vacío.");
        }
        if (salud < 0 || salud > 100) {
            throw new IllegalArgumentException("La salud debe estar comprendida entre 0 y 100.");
        }
        if (dinero < 0 || dinero > 16000) {
            throw new IllegalArgumentException("El dinero debe estar comprendido entre $0 y $16,000.");
        }

        this.alias = alias.trim();
        this.bando = bando.trim();
        this.salud = salud;
        this.dinero = dinero;
    }

    public Jugador() {
        this("s1mple", "Antiterrorista", 100, 16000);
    }

    /**
     * Personaliza la imagen para el radar del HUD.
     */
    @Override
    public String getImagen() {
        return "assets/ui/radar/dot_" + (bando.equalsIgnoreCase("Terrorista") ? "t" : "ct") + ".png";
    }

    /**
     * Posición específica del jugador en el HUD: Radar superior izquierdo.
     */
    @Override
    public String getPosicion() {
        return "HUD_RADAR_TOP_LEFT (x: 50, y: 50)";
    }

    // Nota didáctica: Jugador NO sobreescribe renderizarUI().
    // Aprovecha la implementación 'default' definida en UICapable.

    public String getAlias() {
        return alias;
    }

    public String getBando() {
        return bando;
    }

    public int getSalud() {
        return salud;
    }

    public int getDinero() {
        return dinero;
    }

    public void recibirDanio(int cantidad) {
        if (cantidad < 0) {
            throw new IllegalArgumentException("El daño recibido no puede ser negativo.");
        }
        this.salud = Math.max(0, this.salud - cantidad);
    }

    public boolean estaVivo() {
        return this.salud > 0;
    }
}
