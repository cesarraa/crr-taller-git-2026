package py.edu.uc.lp3.domain;

/**
 * Representa un rifle de francotirador en Counter-Strike 2.
 * Especialización de Arma que implementa mira telescópica con zoom óptico
 * y daño letal a larga distancia.
 */
public class Francotirador extends Arma implements UICapable {

    private int zoom;

    /**
     * Constructor sobrecargado (completo).
     * Recibe todas las especificaciones balísticas y el factor de magnificación del zoom.
     */
    public Francotirador(String nombre, float precio, int dano, float peso, int municionMax, int zoom) {
        super(nombre, precio, dano, peso, municionMax);
        if (zoom <= 0) {
            throw new IllegalArgumentException("El nivel de aumento del zoom debe ser mayor a cero.");
        }
        this.zoom = zoom;
    }

    /**
     * Constructor simple.
     * Invoca al constructor del padre con valores base estándar (4.5 kg, 10 rondas).
     */
    public Francotirador(String nombre, float precio, int dano, int zoom) {
        super(nombre, precio, dano, 4.5f, 10);
        if (zoom <= 0) {
            throw new IllegalArgumentException("El nivel de aumento del zoom debe ser mayor a cero.");
        }
        this.zoom = zoom;
    }

    /**
     * Constructor por defecto.
     * Crea un francotirador emblemático AWP con atributos canónicos de CS2.
     */
    public Francotirador() {
        this("AWP", 4750f, 115, 4.3f, 10, 2);
    }

    /**
     * Sobreescritura del método abstracto de Arma.
     * Describe la doctrina táctica y mecánica propia del rifle de francotirador.
     */
    @Override
    public String describirComportamiento() {
        return "Francotirador " + nombre + ": disparo quirúrgico letal a larga distancia utilizando mira telescópica con zoom óptico x"
                + zoom + " y penalización severa de precisión al disparar sin fijar mira.";
    }

    /**
     * Comportamiento propio: activa la mira telescópica.
     */
    public String activarZoom() {
        return nombre + " fijó objetivo activando magnificación de mira x" + zoom + ".";
    }

    public int getZoom() {
        return zoom;
    }

    public void setZoom(int zoom) {
        if (zoom <= 0) {
            throw new IllegalArgumentException("El nivel de zoom debe ser mayor a cero.");
        }
        this.zoom = zoom;
    }

    // ========================================================
    // Implementación de UICapable (Reemplaza el método default)
    // ========================================================

    /**
     * Reemplaza el método default de UICapable con una visualización personalizada
     * del HUD para rifles de francotirador con mira óptica y estado de munición.
     */
    @Override
    public String renderizarUI() {
        return "HUD Francotirador [" + nombre + "] -> Imagen: '" + getImagen()
                + "' | Posición: '" + getPosicion()
                + "' | Sonido: '" + getSonido()
                + "' | Velocidad: '" + getVelocidad()
                + "' | Zoom óptico: x" + zoom
                + " | Munición en pantalla: " + municionActual + "/" + municionMax;
    }

    @Override
    public String getImagen() {
        return "assets/ui/weapons/awp_scope.png";
    }

    @Override
    public String getPosicion() {
        return "HUD_BOTTOM_RIGHT (x: 1820, y: 980)";
    }

    @Override
    public String getSonido() {
        return "sounds/weapons/sniper_zoom.wav";
    }

    @Override
    public String getVelocidad() {
        return "0.85x (animación lenta de mira)";
    }
}
