package py.edu.uc.lp3.rest.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Servicio REST raíz que confirma la disponibilidad del microservicio.
 * Responde en GET / con metadatos del autor, asignatura y estado operativo.
 */
@RestController
public class IndexController {

    @GetMapping("/")
    public Map<String, Object> index() {
        Map<String, Object> respuesta = new LinkedHashMap<>();
        respuesta.put("status", "UP");
        respuesta.put("servicio", "Microservicio Spring Boot - Taller Git y POO");
        respuesta.put("autor", "César Ramírez Román");
        respuesta.put("asignatura", "Lenguajes de Programación 3 (CYT646)");
        respuesta.put("dominio", "Counter-Strike 2 (CS2)");
        respuesta.put("mecanismosPOO", "Sobrecarga, Sobreescritura e Interfaces con Default Methods (UICapable)");
        respuesta.put("endpointsDisponibles", Map.of(
                "raiz", "GET /",
                "armasConstruir", "GET /armas/construir?tipo={francotirador|pistola}&...",
                "armasComportamientos", "GET /armas/comportamientos",
                "armasDisparar", "GET /armas/disparar?distancia={m}&headshot={bool}",
                "uiElementos", "GET /ui/elementos",
                "uiRenderizar", "GET /ui/renderizar?tipo={francotirador|jugador}"
        ));
        return respuesta;
    }
}
