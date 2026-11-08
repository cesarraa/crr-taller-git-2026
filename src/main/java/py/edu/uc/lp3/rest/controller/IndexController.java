package py.edu.uc.lp3.rest.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Servicio REST raíz que confirma la disponibilidad del microservicio.
 * Responde en GET / con metadatos del autor, asignatura y catálogo de endpoints.
 */
@RestController
public class IndexController {

    @GetMapping("/")
    public Map<String, Object> index() {
        Map<String, Object> respuesta = new LinkedHashMap<>();
        respuesta.put("status", "UP");
        respuesta.put("servicio", "Microservicio Spring Boot - Taller Git, POO y Persistencia H2");
        respuesta.put("autor", "César Ramírez Román");
        respuesta.put("asignatura", "Lenguajes de Programación 3 (CYT646)");
        respuesta.put("dominio", "Counter-Strike 2 (CS2)");
        respuesta.put("arquitectura", "3 Capas: Controller (REST) -> Service (Negocio/Validaciones) -> Repository (Spring Data JPA / H2)");
        respuesta.put("mecanismosPOO", "Herencia, Sobrecarga, Sobreescritura, Interfaces con Default Methods (UICapable) y Jerarquía de Excepciones");

        Map<String, String> endpoints = new LinkedHashMap<>();
        endpoints.put("raiz", "GET /");
        endpoints.put("crudListar", "GET /api/armas");
        endpoints.put("crudBuscarPorId", "GET /api/armas/{id}");
        endpoints.put("crudCrear", "POST /api/armas (JSON con discriminador 'tipo')");
        endpoints.put("crudActualizarPut", "PUT /api/armas/{id} (JSON completo)");
        endpoints.put("crudActualizarPatch", "PATCH /api/armas/{id} (JSON parcial)");
        endpoints.put("crudEliminar", "DELETE /api/armas/{id}");
        endpoints.put("crudDisparar", "POST /api/armas/{id}/disparar?distancia={m}&headshot={bool}");
        endpoints.put("crudRecargar", "POST /api/armas/{id}/recargar");
        endpoints.put("simulacionCombate", "POST /api/simulacion/combate?distancia={m}&headshot={bool}");
        endpoints.put("simulacionEstado", "GET /api/simulacion/estado");
        endpoints.put("jugadoresListar", "GET /api/jugadores");
        endpoints.put("uiElementos", "GET /ui/elementos (Interfaz UICapable polimórfica)");
        endpoints.put("uiRenderizar", "GET /ui/renderizar?tipo={francotirador|jugador}");
        endpoints.put("consolaH2", "http://localhost:8080/h2-console (jdbc:h2:mem:cs2db)");
        respuesta.put("endpointsDisponibles", endpoints);

        return respuesta;
    }
}
