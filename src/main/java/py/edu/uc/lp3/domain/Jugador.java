package py.edu.uc.lp3.domain;

import jakarta.persistence.*;

/**
 * Representa a un combatiente u operador en Counter-Strike 2.
 * Pertenece a una jerarquía/familia distinta e independiente a la de Arma.
 * Mapeada con JPA para persistencia en base de datos H2.
 *
 * Implementa la interfaz UICapable a su modo:
 * Reutiliza la implementación por defecto (default method) provista por la interfaz,
 * personalizando los recursos de radar y HUD.
 */
@Entity
@Table(name = "jugadores")
public class Jugador implements UICapable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String alias;

    @Column(nullable = false)
    private String bando; // "Terrorista" o "Antiterrorista"

    @Column(nullable = false)
    private int salud;    // 0 a 100

    @Column(nullable = false)
    private int dinero;   // 0 a 16000

    /**
     * Constructor sobrecargado con validación estricta de invariantes.
     */
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

    /**
     * Constructor por defecto requerido por JPA / Hibernate.
     */
    public Jugador() {
        this("s1mple", "Antiterrorista", 100, 16000);
    }

    // ========================================================
    // Implementación de UICapable (utiliza default renderizarUI)
    // ========================================================

    @Override
    public String getImagen() {
        return "assets/ui/radar/dot_" + (bando != null && bando.equalsIgnoreCase("Terrorista") ? "t" : "ct") + ".png";
    }

    @Override
    public String getPosicion() {
        return "HUD_RADAR_TOP_LEFT (x: 50, y: 50)";
    }

    // ========================================================
    // Lógica del dominio y control del estado
    // ========================================================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        if (alias == null || alias.isBlank()) {
            throw new IllegalArgumentException("El alias del jugador no puede estar vacío.");
        }
        this.alias = alias.trim();
    }

    public String getBando() {
        return bando;
    }

    public void setBando(String bando) {
        if (bando == null || bando.isBlank()) {
            throw new IllegalArgumentException("El bando del jugador no puede estar vacío.");
        }
        this.bando = bando.trim();
    }

    public int getSalud() {
        return salud;
    }

    public void setSalud(int salud) {
        if (salud < 0 || salud > 100) {
            throw new IllegalArgumentException("La salud debe estar entre 0 y 100.");
        }
        this.salud = salud;
    }

    public int getDinero() {
        return dinero;
    }

    public void setDinero(int dinero) {
        if (dinero < 0 || dinero > 16000) {
            throw new IllegalArgumentException("El dinero debe estar entre $0 y $16,000.");
        }
        this.dinero = dinero;
    }

    /**
     * Regla de negocio de impacto: reduce la salud protegiendo el invariante del piso 0.
     */
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
