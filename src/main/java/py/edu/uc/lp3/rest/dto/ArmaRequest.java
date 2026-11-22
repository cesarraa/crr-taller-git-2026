package py.edu.uc.lp3.rest.dto;

import com.fasterxml.jackson.annotation.JsonAlias;

public class ArmaRequest {

    private String tipo;
    private String nombre;
    private Float precio;

    @JsonAlias({"danio", "daño"})
    private Integer dano;

    private Float peso;

    @JsonAlias({"municionMaxima", "municion_max", "maxAmmo"})
    private Integer municionMax;

    @JsonAlias({"municion", "municion_actual", "currentAmmo"})
    private Integer municionActual;

    private Integer zoom;
    private Integer cargador;
    private String modoDisparo;
    private String tipoGranada;
    private Float radioExplosion;
    private Integer cartuchos;
    private Float dispersion;

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Float getPrecio() {
        return precio;
    }

    public void setPrecio(Float precio) {
        this.precio = precio;
    }

    public Integer getDano() {
        return dano;
    }

    public void setDano(Integer dano) {
        this.dano = dano;
    }

    public Float getPeso() {
        return peso;
    }

    public void setPeso(Float peso) {
        this.peso = peso;
    }

    public Integer getMunicionMax() {
        return municionMax;
    }

    public void setMunicionMax(Integer municionMax) {
        this.municionMax = municionMax;
    }

    public Integer getMunicionActual() {
        return municionActual;
    }

    public void setMunicionActual(Integer municionActual) {
        this.municionActual = municionActual;
    }

    public Integer getZoom() {
        return zoom;
    }

    public void setZoom(Integer zoom) {
        this.zoom = zoom;
    }

    public Integer getCargador() {
        return cargador;
    }

    public void setCargador(Integer cargador) {
        this.cargador = cargador;
    }

    public String getModoDisparo() {
        return modoDisparo;
    }

    public void setModoDisparo(String modoDisparo) {
        this.modoDisparo = modoDisparo;
    }

    public String getTipoGranada() {
        return tipoGranada;
    }

    public void setTipoGranada(String tipoGranada) {
        this.tipoGranada = tipoGranada;
    }

    public Float getRadioExplosion() {
        return radioExplosion;
    }

    public void setRadioExplosion(Float radioExplosion) {
        this.radioExplosion = radioExplosion;
    }

    public Integer getCartuchos() {
        return cartuchos;
    }

    public void setCartuchos(Integer cartuchos) {
        this.cartuchos = cartuchos;
    }

    public Float getDispersion() {
        return dispersion;
    }

    public void setDispersion(Float dispersion) {
        this.dispersion = dispersion;
    }
}
