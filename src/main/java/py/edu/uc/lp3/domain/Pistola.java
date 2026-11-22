package py.edu.uc.lp3.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("PISTOLA")
public class Pistola extends Arma {

    private String modoDisparo;
    private int cargador;

    protected Pistola() {
        this("Glock-18", 200f, 28, 0.9f, 20, "Semiautomático", 20);
    }

    public Pistola(String nombre, float precio, int dano, float peso, int municionMax,
                   String modoDisparo, int cargador) {
        super(nombre, precio, dano, peso, municionMax);
        this.modoDisparo = modoDisparo;
        this.cargador = cargador;
    }

    public Pistola(String nombre, float precio, int dano) {
        this(nombre, precio, dano, 1.0f, 12, "Semiautomático", 12);
    }

    @Override
    public String describirComportamiento() {
        return "Pistola " + getNombre() + ": arma secundaria para combate a corta distancia en modo "
                + modoDisparo + " con ciclo rápido de recarga para " + cargador + " balas.";
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
