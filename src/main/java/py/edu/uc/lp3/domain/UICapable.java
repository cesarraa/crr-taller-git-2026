package py.edu.uc.lp3.domain;

/**
 * Interfaz que define la capacidad de un elemento del juego para ser representado
 * y renderizado en la interfaz gráfica de usuario (HUD / Radar / UI) de Counter-Strike 2.
 *
 * Basada en el esquema de cátedra:
 * UICapable {
 *   imagen;
 *   posicion;
 *   sonido;
 *   velocidad;
 * }
 */
public interface UICapable {

    /**
     * Mensaje de la interfaz que devuelve una representación textual del renderizado
     * del elemento en la interfaz de usuario (HUD).
     *
     * Proporciona una implementación por defecto (default method de Java 8+)
     * que compone las propiedades visuales y sonoras estándar.
     */
    default String renderizarUI() {
        return "Renderizado HUD genérico CS2 [imagen='" + getImagen()
                + "', posicion='" + getPosicion()
                + "', sonido='" + getSonido()
                + "', velocidad='" + getVelocidad() + "']";
    }

    /**
     * Retorna la ruta o identificador de la imagen/textura para la interfaz.
     */
    default String getImagen() {
        return "assets/ui/icons/hud_default.png";
    }

    /**
     * Retorna las coordenadas o slot en la pantalla del HUD.
     */
    default String getPosicion() {
        return "HUD_CENTRO (x: 960, y: 540)";
    }

    /**
     * Retorna el archivo de audio reproducido por la UI.
     */
    default String getSonido() {
        return "sounds/ui/hud_beep.wav";
    }

    /**
     * Retorna la velocidad de animación o tasa de refresco en la UI.
     */
    default String getVelocidad() {
        return "1.0x (60 FPS)";
    }
}
