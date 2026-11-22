package py.edu.uc.lp3.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Entity
@DiscriminatorValue("GRANADA")
public class Granada extends Arma {

    @Enumerated(EnumType.STRING)
    private GranadaTipo tipoGranada;
    private float radioExplosion;

    protected Granada() {
        this("Granada de Humo", 300f, 50, 0.4f, 1, GranadaTipo.HUMO, 5.0f);
    }

    public Granada(String nombre, float precio, int dano, float peso, int municionMax,
                   GranadaTipo tipoGranada, float radioExplosion) {
        super(nombre, precio, dano, peso, municionMax);
        this.tipoGranada = tipoGranada;
        this.radioExplosion = radioExplosion;
    }

    public Granada(String nombre, float precio, int dano, GranadaTipo tipoGranada) {
        this(nombre, precio, dano, 0.4f, 1, tipoGranada, 5.0f);
    }

    @Override
    public String describirComportamiento() {
        return "Granada " + getNombre() + " táctica (" + tipoGranada + "): radio de detonación de "
                + radioExplosion + " metros con capacidad máxima de " + getMunicionMax() + " unidad.";
    }

    public GranadaTipo getTipoGranada() {
        return tipoGranada;
    }

    public void setTipoGranada(GranadaTipo tipoGranada) {
        this.tipoGranada = tipoGranada;
    }

    public float getRadioExplosion() {
        return radioExplosion;
    }

    public void setRadioExplosion(float radioExplosion) {
        this.radioExplosion = radioExplosion;
    }
}
