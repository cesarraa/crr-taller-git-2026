package py.edu.uc.lp3.rest.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import py.edu.uc.lp3.domain.Francotirador;
import py.edu.uc.lp3.domain.Jugador;
import py.edu.uc.lp3.domain.UICapable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador REST que expone las capacidades de renderizado de la interfaz UICapable.
 *
 * Cumple la consigna:
 * "Un servicio REST pide ese mensaje a través del tipo de la interfaz y responde JSON."
 */
@RestController
@RequestMapping("/ui")
public class UIController {

    /**
     * Endpoint polimórfico principal.
     * Colecciona elementos de familias completamente distintas (Francotirador y Jugador),
     * invocando el mensaje renderizarUI() estrictamente a través de la interfaz UICapable.
     *
     * GET /ui/elementos
     */
    @GetMapping("/elementos")
    public ResponseEntity<List<Map<String, Object>>> listarElementosUI() {
        // Dos clases en familias distintas asignadas a referencias del tipo de la interfaz:
        List<UICapable> elementos = List.of(
                new Jugador("s1mple", "Antiterrorista", 100, 16000),
                new Francotirador("AWP", 4750f, 115, 4.3f, 10, 2)
        );

        List<Map<String, Object>> respuesta = new ArrayList<>();

        for (UICapable elemento : elementos) {
            // Se pide el mensaje a través del tipo de la interfaz:
            String mensajeUI = elemento.renderizarUI();

            Map<String, Object> item = new LinkedHashMap<>();
            item.put("clase", elemento.getClass().getSimpleName());
            item.put("familia", (elemento instanceof py.edu.uc.lp3.domain.Arma)
                    ? "Familia Arma (Balística)"
                    : "Familia Jugador (Personaje/Entidad)");
            item.put("tipoInterfaz", UICapable.class.getName());
            item.put("reemplazaMetodoDefault", elemento instanceof Francotirador);
            item.put("mensajeUI", mensajeUI);
            item.put("imagen", elemento.getImagen());
            item.put("posicion", elemento.getPosicion());
            item.put("sonido", elemento.getSonido());
            item.put("velocidad", elemento.getVelocidad());
            respuesta.add(item);
        }

        return ResponseEntity.ok(respuesta);
    }

    /**
     * Endpoint para consultar individualmente la capacidad UI por tipo.
     * Invoca el mensaje a través del tipo de la interfaz UICapable.
     *
     * Ejemplos:
     * - Francotirador (reemplaza default):
     *   GET /ui/renderizar?tipo=francotirador
     * - Jugador (utiliza default):
     *   GET /ui/renderizar?tipo=jugador
     */
    @GetMapping("/renderizar")
    public ResponseEntity<?> renderizarElemento(
            @RequestParam(defaultValue = "francotirador") String tipo,
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String bando) {

        // Declaración usando el tipo de la interfaz:
        UICapable elemento;
        boolean reemplazaDefault;

        if ("jugador".equalsIgnoreCase(tipo)) {
            String aliasFinal = (nombre != null && !nombre.isBlank()) ? nombre : "ZywOo";
            String bandoFinal = (bando != null && !bando.isBlank()) ? bando : "Terrorista";
            elemento = new Jugador(aliasFinal, bandoFinal, 100, 8000);
            reemplazaDefault = false; // Reutiliza el default method de UICapable
        } else if ("francotirador".equalsIgnoreCase(tipo) || "arma".equalsIgnoreCase(tipo)) {
            String nombreFinal = (nombre != null && !nombre.isBlank()) ? nombre : "AWP Dragon Lore";
            elemento = new Francotirador(nombreFinal, 4750f, 115, 2);
            reemplazaDefault = true; // Reemplaza el default method con su propia versión
        } else {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Tipo no soportado. Valores válidos: 'francotirador' o 'jugador'."
            ));
        }

        // Se pide el mensaje a través del tipo de la interfaz UICapable:
        String mensajeTexto = elemento.renderizarUI();

        Map<String, Object> respuesta = new LinkedHashMap<>();
        respuesta.put("tipoInterfaz", UICapable.class.getSimpleName());
        respuesta.put("claseConcreta", elemento.getClass().getSimpleName());
        respuesta.put("reemplazaDefault", reemplazaDefault);
        respuesta.put("mensajeTexto", mensajeTexto);

        return ResponseEntity.ok(respuesta);
    }
}
