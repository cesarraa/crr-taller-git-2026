package py.edu.uc.lp3.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "jugadores")
public class Jugador implements UICapable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String alias;

    @Column(nullable = false)
    private String bando;

    @Column(nullable = false)
    private int salud;

    @Column(nullable = false)
    private int dinero;

    protected Jugador() {
        this("s1mple", "Antiterrorista", 100, 16000);
    }

    public Jugador(String alias, String bando, int salud, int dinero) {
        this.alias = alias;
        this.bando = bando;
        this.salud = salud;
        this.dinero = dinero;
    }

    @Override
    public String getImagen() {
        return "assets/ui/radar/dot_" + (bando != null && bando.equalsIgnoreCase("Terrorista") ? "t" : "ct") + ".png";
    }

    @Override
    public String getPosicion() {
        return "HUD_RADAR_TOP_LEFT (x: 50, y: 50)";
    }

    public Long getId() {
        return id;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getBando() {
        return bando;
    }

    public void setBando(String bando) {
        this.bando = bando;
    }

    public int getSalud() {
        return salud;
    }

    public void setSalud(int salud) {
        this.salud = salud;
    }

    public int getDinero() {
        return dinero;
    }

    public void setDinero(int dinero) {
        this.dinero = dinero;
    }
}
