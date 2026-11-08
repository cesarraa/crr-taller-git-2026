package py.edu.uc.lp3.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

/**
 * Representa un artefacto arrojadizo de tipo granada en CS2. Hereda de Arma.
 */
@Entity
@DiscriminatorValue("GRANADA")
public class Granada extends Arma {

    @Enumerated(EnumType.STRING)
    protected GranadaTipo tipoGranada;
    protected float radioExplosion;

    public Granada() {
        this("Granada de Humo", 300f, 0, 0.4f, 1, GranadaTipo.HUMO, 5.0f);
    }

    public Granada(String nombre, float precio, int dano, float peso, int municionMax,
                   GranadaTipo tipoGranada, float radioExplosion) {
        super(nombre, precio, dano, peso, municionMax);
        if (tipoGranada == null) {
            throw new IllegalArgumentException("El tipo de granada no puede ser nulo.");
        }
        if (radioExplosion < 0) {
            throw new IllegalArgumentException("El radio de explosión no puede ser negativo.");
        }
        this.tipoGranada = tipoGranada;
        this.radioExplosion = radioExplosion;
    }

    public Granada(String nombre, float precio, int dano, GranadaTipo tipoGranada) {
        super(nombre, precio, dano, 0.4f, 1);
        if (tipoGranada == null) {
            throw new IllegalArgumentException("El tipo de granada no puede ser nulo.");
        }
        this.tipoGranada = tipoGranada;
        this.radioExplosion = 5.0f;
    }

    public String lanzar() {
        if (municionActual > 0) {
            municionActual--;
            return nombre + " (" + tipoGranada + ") fue lanzada con radio de efecto de "
                    + radioExplosion + "m. Restantes: " + municionActual;
        } else {
            return "No quedan granadas de tipo " + nombre + " para lanzar.";
        }
    }

    @Override
    public String describirComportamiento() {
        return "Granada " + nombre + " táctica (" + tipoGranada + "): radio de detonación de "
                + radioExplosion + " metros con capacidad máxima de " + municionMax + " unidad.";
    }

    public GranadaTipo getTipoGranada() {
        return tipoGranada;
    }

    public void setTipoGranada(GranadaTipo tipoGranada) {
        if (tipoGranada == null) {
            throw new IllegalArgumentException("El tipo de granada no puede ser nulo.");
        }
        this.tipoGranada = tipoGranada;
    }

    public float getRadioExplosion() {
        return radioExplosion;
    }

    public void setRadioExplosion(float radioExplosion) {
        if (radioExplosion < 0) {
            throw new IllegalArgumentException("El radio de explosión no puede ser negativo.");
        }
        this.radioExplosion = radioExplosion;
    }
}
