package py.edu.uc.lp3.rest.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import py.edu.uc.lp3.domain.Arma;
import py.edu.uc.lp3.domain.Francotirador;
import py.edu.uc.lp3.domain.Pistola;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador REST para el dominio de Armas de Counter-Strike 2.
 * Expone endpoints para:
 * 1. Construcción dinámica desde parámetros URL (constructores simples y sobrecargados).
 * 2. Invocación polimórfica del método abstracto sobreescrito en clases hijas.
 * 3. Ejecución de la sobrecarga de mensajes del dominio (disparar).
 */
@RestController
@RequestMapping("/armas")
public class ArmaController {

    /**
     * Endpoint para construir instancias del dominio mediante parámetros en la URL.
     * Permite probar tanto el constructor simple como el sobrecargado.
     *
     * Ejemplos:
     * - Francotirador simple:
     *   GET /armas/construir?tipo=francotirador&nombre=AWP&precio=4750&dano=115&zoom=2
     * - Francotirador sobrecargado:
     *   GET /armas/construir?tipo=francotirador&nombre=AWP&precio=4750&dano=115&peso=4.3&municionMax=10&zoom=2
     * - Pistola simple:
     *   GET /armas/construir?tipo=pistola&nombre=Desert+Eagle&precio=700&dano=53
     * - Error por invariante (400 Bad Request):
     *   GET /armas/construir?tipo=francotirador&nombre=AWP&precio=-500&dano=115
     */
    @GetMapping("/construir")
    public ResponseEntity<?> construirArma(
            @RequestParam(defaultValue = "francotirador") String tipo,
            @RequestParam(defaultValue = "AWP") String nombre,
            @RequestParam(defaultValue = "4750") float precio,
            @RequestParam(defaultValue = "115") int dano,
            @RequestParam(required = false) Float peso,
            @RequestParam(required = false) Integer municionMax,
            @RequestParam(required = false) Integer zoom,
            @RequestParam(required = false) String modoDisparo,
            @RequestParam(required = false) Integer cargador) {

        try {
            Arma armaCreada;
            String constructorUsado;

            if ("pistola".equalsIgnoreCase(tipo)) {
                if (peso != null && municionMax != null && modoDisparo != null && cargador != null) {
                    // Constructor sobrecargado completo
                    armaCreada = new Pistola(nombre, precio, dano, peso, municionMax, modoDisparo, cargador);
                    constructorUsado = "Sobrecargado (nombre, precio, dano, peso, municionMax, modoDisparo, cargador)";
                } else {
                    // Constructor simple (asigna peso, munición y cargador estándar del dominio)
                    armaCreada = new Pistola(nombre, precio, dano);
                    constructorUsado = "Simple (nombre, precio, dano)";
                }
            } else {
                // Por defecto o tipo "francotirador"
                int zoomFinal = (zoom != null) ? zoom : 2;
                if (peso != null && municionMax != null) {
                    // Constructor sobrecargado completo
                    armaCreada = new Francotirador(nombre, precio, dano, peso, municionMax, zoomFinal);
                    constructorUsado = "Sobrecargado (nombre, precio, dano, peso, municionMax, zoom)";
                } else {
                    // Constructor simple (asigna peso y munición estándar del dominio)
                    armaCreada = new Francotirador(nombre, precio, dano, zoomFinal);
                    constructorUsado = "Simple (nombre, precio, dano, zoom)";
                }
            }

            Map<String, Object> respuesta = new LinkedHashMap<>();
            respuesta.put("status", "EXITOSO");
            respuesta.put("mensaje", "Objeto creado legalmente y validado por las reglas de la clase.");
            respuesta.put("tipoArma", armaCreada.getClass().getSimpleName());
            respuesta.put("constructorUtilizado", constructorUsado);
            respuesta.put("nombre", armaCreada.obtenerNombre());
            respuesta.put("precio", armaCreada.obtenerPrecio());
            respuesta.put("dano", armaCreada.getDano());
            respuesta.put("peso", armaCreada.getPeso());
            respuesta.put("municionMax", armaCreada.getMunicionMax());
            respuesta.put("municionActual", armaCreada.getMunicionActual());
            respuesta.put("comportamientoSobreescrito", armaCreada.describirComportamiento());

            return ResponseEntity.ok(respuesta);

        } catch (IllegalArgumentException e) {
            Map<String, Object> error = new LinkedHashMap<>();
            error.put("status", "RECHAZADO");
            error.put("error", "Regla de invariante rota: el dominio rechazó la construcción.");
            error.put("detalle", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * Endpoint polimórfico que interactúa con las clases hijas únicamente a través de Arma.
     * Invoca el método abstracto sobreescrito describirComportamiento() en cada una.
     * GET /armas/comportamientos
     */
    @GetMapping("/comportamientos")
    public List<Map<String, Object>> listarComportamientos() {
        // Se instancian dos clases hijas independientes y se tratan como tipo padre Arma
        Arma arma1 = new Pistola("Desert Eagle", 700f, 53, 1.8f, 7, "Semiautomático", 7);
        Arma arma2 = new Francotirador("AWP", 4750f, 115, 4.3f, 10, 2);

        List<Arma> inventarioPolimorfico = List.of(arma1, arma2);
        List<Map<String, Object>> respuesta = new ArrayList<>();

        for (Arma arma : inventarioPolimorfico) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("tipoClaseHija", arma.getClass().getSimpleName());
            item.put("nombre", arma.obtenerNombre());
            item.put("precio", arma.obtenerPrecio());
            // Mensaje polimórfico resuelto dinámicamente en tiempo de ejecución
            item.put("comportamiento", arma.describirComportamiento());
            respuesta.add(item);
        }

        return respuesta;
    }

    /**
     * Endpoint que evidencia la SOBRECARGA de mensajes del dominio (disparar).
     *
     * Ejemplos:
     * - Disparo básico (sobrecarga 1):
     *   GET /armas/disparar
     * - Disparo con distancia (sobrecarga 2):
     *   GET /armas/disparar?distancia=50
     * - Disparo con distancia y tiro a la cabeza (sobrecarga 3):
     *   GET /armas/disparar?distancia=50&headshot=true
     */
    @GetMapping("/disparar")
    public ResponseEntity<?> probarSobrecargaDisparar(
            @RequestParam(defaultValue = "francotirador") String tipo,
            @RequestParam(required = false) Integer distancia,
            @RequestParam(required = false) Boolean headshot) {

        try {
            Arma arma = "pistola".equalsIgnoreCase(tipo)
                    ? new Pistola("Glock-18", 200f, 28)
                    : new Francotirador("AWP", 4750f, 115, 2);

            String resultadoMensaje;
            String firmaSobrecargada;

            if (distancia == null) {
                // Invocación a Sobrecarga 1: disparar()
                resultadoMensaje = arma.disparar();
                firmaSobrecargada = "disparar() [sin argumentos]";
            } else if (headshot == null) {
                // Invocación a Sobrecarga 2: disparar(int distanciaMetros)
                resultadoMensaje = arma.disparar(distancia);
                firmaSobrecargada = "disparar(int distanciaMetros) [con distancia]";
            } else {
                // Invocación a Sobrecarga 3: disparar(int distanciaMetros, boolean tiroALaCabeza)
                resultadoMensaje = arma.disparar(distancia, headshot);
                firmaSobrecargada = "disparar(int distanciaMetros, boolean tiroALaCabeza)";
            }

            Map<String, Object> respuesta = new LinkedHashMap<>();
            respuesta.put("status", "OK");
            respuesta.put("arma", arma.obtenerNombre());
            respuesta.put("sobrecargaEjecutada", firmaSobrecargada);
            respuesta.put("resultado", resultadoMensaje);
            respuesta.put("municionRestante", arma.getMunicionActual());
            return ResponseEntity.ok(respuesta);

        } catch (IllegalArgumentException e) {
            Map<String, Object> error = new LinkedHashMap<>();
            error.put("status", "RECHAZADO");
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
}
