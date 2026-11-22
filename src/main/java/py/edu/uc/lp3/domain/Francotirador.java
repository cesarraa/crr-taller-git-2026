package py.edu.uc.lp3.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("FRANCOTIRADOR")
public class Francotirador extends Arma implements UICapable {

    private int zoom;

    protected Francotirador() {
        this("AWP", 4750f, 115, 4.3f, 10, 2);
    }

    public Francotirador(String nombre, float precio, int dano, float peso, int municionMax, int zoom) {
        super(nombre, precio, dano, peso, municionMax);
        this.zoom = zoom;
    }

    public Francotirador(String nombre, float precio, int dano, int zoom) {
        this(nombre, precio, dano, 4.5f, 10, zoom);
    }

    @Override
    public String describirComportamiento() {
        return "Francotirador " + getNombre() + ": disparo quirúrgico letal a larga distancia utilizando mira telescópica con zoom óptico x"
                + zoom + " y penalización severa de precisión al disparar sin fijar mira.";
    }

    public int getZoom() {
        return zoom;
    }

    public void setZoom(int zoom) {
        this.zoom = zoom;
    }

    @Override
    public String renderizarUI() {
        return "HUD Francotirador [" + getNombre() + "] -> Imagen: '" + getImagen()
                + "' | Posición: '" + getPosicion()
                + "' | Sonido: '" + getSonido()
                + "' | Velocidad: '" + getVelocidad()
                + "' | Zoom óptico: x" + zoom
                + " | Munición en pantalla: " + getMunicionActual() + "/" + getMunicionMax();
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
