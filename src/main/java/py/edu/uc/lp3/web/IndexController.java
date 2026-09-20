package py.edu.uc.lp3.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class IndexController {

	@GetMapping("/")
	public Map<String, Object> index() {
		Map<String, Object> respuesta = new LinkedHashMap<>();
		respuesta.put("status", "UP");
		respuesta.put("autor", "César Ramírez Román");
		respuesta.put("asignatura", "Lenguajes de Programación 3 (CYT646)");
		respuesta.put("dominio", "Counter-Strike 2 (CS2)");
		respuesta.put("mensaje", "API REST de modelado orientado a objetos en ejecución");
		return respuesta;
	}
}
