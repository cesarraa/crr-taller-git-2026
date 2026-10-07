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
        respuesta.put("paquetes", "py.edu.uc.lp3.domain / py.edu.uc.lp3.rest.controller");
        respuesta.put("mecanismosPOO", "Sobrecarga (constructores y disparar) y Sobreescritura (describirComportamiento)");
        return respuesta;
    }
}
