package py.edu.uc.lp3.service;

import java.time.LocalDateTime;

/**
 * DTO que encapsula el resultado observable y persistido de la simulación de combate en CS2.
 * Evidencia la aplicación de las dos reglas de negocio sobre las tres clases del modelo
 * (Francotirador, Pistola y Jugador) y su persistencia en base de datos H2.
 */
public class ResultadoSimulacion {

    private LocalDateTime timestamp;
    private String descripcion;
    private String regla1Aplicada;
    private String regla2Aplicada;

    // Participante 1: Francotirador (Hija de Arma)
    private Long francotiradorId;
    private String francotiradorNombre;
    private int francotiradorMunicionAntes;
    private int francotiradorMunicionDespues;
    private int francotiradorDanioInfligido;

    // Participante 2: Pistola (Hija de Arma)
    private Long pistolaId;
    private String pistolaNombre;
    private int pistolaMunicionAntes;
    private int pistolaMunicionDespues;
    private int pistolaDanioInfligido;

    // Participante 3: Jugador (Objetivo / Combatiente)
    private Long jugadorId;
    private String jugadorAlias;
    private int jugadorSaludAntes;
    private int jugadorSaludDespues;
    private boolean jugadorSobrevive;

    private int danioTotalRecibido;
    private boolean persistidoEnH2;

    public ResultadoSimulacion() {
        this.timestamp = LocalDateTime.now();
        this.persistidoEnH2 = true;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getRegla1Aplicada() {
        return regla1Aplicada;
    }

    public void setRegla1Aplicada(String regla1Aplicada) {
        this.regla1Aplicada = regla1Aplicada;
    }

    public String getRegla2Aplicada() {
        return regla2Aplicada;
    }

    public void setRegla2Aplicada(String regla2Aplicada) {
        this.regla2Aplicada = regla2Aplicada;
    }

    public Long getFrancotiradorId() {
        return francotiradorId;
    }

    public void setFrancotiradorId(Long francotiradorId) {
        this.francotiradorId = francotiradorId;
    }

    public String getFrancotiradorNombre() {
        return francotiradorNombre;
    }

    public void setFrancotiradorNombre(String francotiradorNombre) {
        this.francotiradorNombre = francotiradorNombre;
    }

    public int getFrancotiradorMunicionAntes() {
        return francotiradorMunicionAntes;
    }

    public void setFrancotiradorMunicionAntes(int francotiradorMunicionAntes) {
        this.francotiradorMunicionAntes = francotiradorMunicionAntes;
    }

    public int getFrancotiradorMunicionDespues() {
        return francotiradorMunicionDespues;
    }

    public void setFrancotiradorMunicionDespues(int francotiradorMunicionDespues) {
        this.francotiradorMunicionDespues = francotiradorMunicionDespues;
    }

    public int getFrancotiradorDanioInfligido() {
        return francotiradorDanioInfligido;
    }

    public void setFrancotiradorDanioInfligido(int francotiradorDanioInfligido) {
        this.francotiradorDanioInfligido = francotiradorDanioInfligido;
    }

    public Long getPistolaId() {
        return pistolaId;
    }

    public void setPistolaId(Long pistolaId) {
        this.pistolaId = pistolaId;
    }

    public String getPistolaNombre() {
        return pistolaNombre;
    }

    public void setPistolaNombre(String pistolaNombre) {
        this.pistolaNombre = pistolaNombre;
    }

    public int getPistolaMunicionAntes() {
        return pistolaMunicionAntes;
    }

    public void setPistolaMunicionAntes(int pistolaMunicionAntes) {
        this.pistolaMunicionAntes = pistolaMunicionAntes;
    }

    public int getPistolaMunicionDespues() {
        return pistolaMunicionDespues;
    }

    public void setPistolaMunicionDespues(int pistolaMunicionDespues) {
        this.pistolaMunicionDespues = pistolaMunicionDespues;
    }

    public int getPistolaDanioInfligido() {
        return pistolaDanioInfligido;
    }

    public void setPistolaDanioInfligido(int pistolaDanioInfligido) {
        this.pistolaDanioInfligido = pistolaDanioInfligido;
    }

    public Long getJugadorId() {
        return jugadorId;
    }

    public void setJugadorId(Long jugadorId) {
        this.jugadorId = jugadorId;
    }

    public String getJugadorAlias() {
        return jugadorAlias;
    }

    public void setJugadorAlias(String jugadorAlias) {
        this.jugadorAlias = jugadorAlias;
    }

    public int getJugadorSaludAntes() {
        return jugadorSaludAntes;
    }

    public void setJugadorSaludAntes(int jugadorSaludAntes) {
        this.jugadorSaludAntes = jugadorSaludAntes;
    }

    public int getJugadorSaludDespues() {
        return jugadorSaludDespues;
    }

    public void setJugadorSaludDespues(int jugadorSaludDespues) {
        this.jugadorSaludDespues = jugadorSaludDespues;
    }

    public boolean isJugadorSobrevive() {
        return jugadorSobrevive;
    }

    public void setJugadorSobrevive(boolean jugadorSobrevive) {
        this.jugadorSobrevive = jugadorSobrevive;
    }

    public int getDanioTotalRecibido() {
        return danioTotalRecibido;
    }

    public void setDanioTotalRecibido(int danioTotalRecibido) {
        this.danioTotalRecibido = danioTotalRecibido;
    }

    public boolean isPersistidoEnH2() {
        return persistidoEnH2;
    }

    public void setPersistidoEnH2(boolean persistidoEnH2) {
        this.persistidoEnH2 = persistidoEnH2;
    }
}
