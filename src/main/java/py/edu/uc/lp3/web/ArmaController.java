package py.edu.uc.lp3.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import py.edu.uc.lp3.cs2.Arma;
import py.edu.uc.lp3.cs2.Francotirador;
import py.edu.uc.lp3.cs2.Pistola;

import java.util.*;

@RestController
@RequestMapping("/armas")
public class ArmaController {
	/**
	 * Endpoint de construcción por URL (Parte E).
	 * Recibe parámetros en la query y delega la validación directamente a la clase Francotirador.
	 * Si los valores son ilegales (ej: precio negativo), la clase lanza IllegalArgumentException.
	 * Ejemplo: GET /armas/construir?nombre=AWP&precio=4750&dano=115&peso=4.3&municionMax=10&zoom=2
	 */
	 @GetMapping("/construir")
	 public ResponseEntity<?> construirFrancotirador(@RequestParam(defaultValue = "AWP") String nombre, @RequestParam(defaultValue = "4750") float precio, @RequestParam(defaultValue = "115") int dano, @RequestParam(defaultValue = "4.3") float peso, @RequestParam(defaultValue = "10") int municionMax, @RequestParam(defaultValue = "2") int zoom) {
	 	try {
	 		Francotirador francotirador = new Francotirador(nombre, precio, dano, peso, municionMax, zoom);
	 		
	 		Map<String, Object> respuesta = new LinkedHashMap<>();
	 		respuesta.put("mensaje", "Arma instanciada exitosamente validada por el dominio.");
	 		respuesta.put("nombre", francotirador.obtenerNombre());
	 		respuesta.put("precio", francotirador.obtenerPrecio());
	 		respuesta.put("dano", francotirador.getDano());
	 		respuesta.put("zoom", francotirador.getZoom());
	 		respuesta.put("comportamiento", francotirador.describirComportamiento());
	 		return ResponseEntity.ok(respuesta);
	 	} catch (IllegalArgumentException e) {
	 		Map<String, Object> error = new LinkedHashMap<>();
	 		error.put("error", "Parámetro inválido rechazado por la clase del modelo.");
	 		error.put("detalle", e.getMessage());
	 		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
	 	}
	 }
	 
	 /**
	  * Endpoint polimórfico (Parte F).
	  * Trata a las dos hijas como el tipo padre 'Arma' dentro de una colección.
	  * No hay 'if' ni 'instanceof' por tipo: cada objeto sabe responder su propio comportamiento.
	  * GET /armas/comportamientos
	  */
	 @GetMapping("/comportamientos")
	 public List<Map<String, Object>> listarComportamientos() {
	 	Arma arma1 = new Pistola("Desert Eagle", 700f, 53, 1.8f, 7, "Semiautomático", 7);
	 	Arma arma2 = new Francotirador("AWP", 4750f, 115, 4.3f, 10, 2);
	 	
	 	List<Arma> inventario = List.of(arma1, arma2);
	 	List<Map<String, Object>> resultado = new ArrayList<>();
	 	
	 	// El controller interactúa exclusivamente con el tipo base Arma
	 	for (Arma arma : inventario) {
	 		Map<String, Object> item = new LinkedHashMap<>();
	 		item.put("arma", arma.obtenerNombre());
	 		item.put("precio", arma.obtenerPrecio());
	 		item.put("comportamiento", arma.describirComportamiento()); // Envío de mensaje polimórfico
	 		resultado.add(item);
	 	}
	 	
	 	return resultado;
	 }
}
