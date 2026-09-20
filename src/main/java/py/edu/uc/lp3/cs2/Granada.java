package py.edu.uc.lp3.cs2;

/**
 * Representa un arma de tipo granada, que se lanza en lugar de dispararse.
 * Sirve como clase base para las especializaciones de granada.
 */
public class Granada extends Arma {

    protected GranadaTipo tipoGranada;
    protected float radioExplosion;

    public Granada(String nombre, float precio, int dano, float peso, int municionMax,
                    GranadaTipo tipoGranada, float radioExplosion) {
        super(nombre, precio, dano, peso, municionMax);
        this.tipoGranada = tipoGranada;
        this.radioExplosion = radioExplosion;
    }

    /**
     * Ejecuta el lanzamiento de la granada.
     */
    public void lanzar() {
        if (municionActual > 0) {
            municionActual--;
            System.out.println(nombre + " (" + tipoGranada + ") fue lanzada. Radio de explosión: "
                    + radioExplosion + "m. Restantes: " + municionActual);
        } else {
            System.out.println("No quedan granadas de tipo " + nombre + " para lanzar.");
        }
    }
    
    @Override
    public String describirComportamiento() {
    	return "Proyectil arrojadizo de tipo " + tipoGranada + " con radio de efecto de " + radioExplosion + " metros.";
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
