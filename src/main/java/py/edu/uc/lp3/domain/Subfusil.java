package py.edu.uc.lp3.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("SUBFUSIL")
public class Subfusil extends Arma {

    private String modoDisparo;
    private int cargador;

    protected Subfusil() {
        this("MP9", 1250f, 26, 2.5f, 120, "Automático", 30);
    }

    public Subfusil(String nombre, float precio, int dano, float peso, int municionMax,
                    String modoDisparo, int cargador) {
        super(nombre, precio, dano, peso, municionMax);
        this.modoDisparo = modoDisparo;
        this.cargador = cargador;
    }

    public Subfusil(String nombre, float precio, int dano, int cargador) {
        this(nombre, precio, dano, 2.8f, 100, "Automático", cargador);
    }

    @Override
    public String describirComportamiento() {
        return "Subfusil " + getNombre() + ": alta cadencia de fuego en modo " + modoDisparo
                + " con cargador de " + cargador + " balas y excelente bonificación por baja.";
    }

    public String getModoDisparo() {
        return modoDisparo;
    }

    public void setModoDisparo(String modoDisparo) {
        this.modoDisparo = modoDisparo;
    }

    public int getCargador() {
        return cargador;
    }

    public void setCargador(int cargador) {
        this.cargador = cargador;
    }
}
