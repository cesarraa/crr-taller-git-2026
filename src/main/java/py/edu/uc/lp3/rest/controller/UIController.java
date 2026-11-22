package py.edu.uc.lp3.rest.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import py.edu.uc.lp3.domain.Arma;
import py.edu.uc.lp3.domain.Francotirador;
import py.edu.uc.lp3.domain.Jugador;
import py.edu.uc.lp3.domain.UICapable;
import py.edu.uc.lp3.service.ArmaService;
import py.edu.uc.lp3.service.JugadorService;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ui")
public class UIController {

    private final ArmaService armaService;
    private final JugadorService jugadorService;

    public UIController(ArmaService armaService, JugadorService jugadorService) {
        this.armaService = armaService;
        this.jugadorService = jugadorService;
    }

    @GetMapping("/elementos")
    public ResponseEntity<List<Map<String, Object>>> listarElementosUI() {
        Jugador jugador = jugadorService.primerJugador();
        Francotirador francotirador = armaService.primerFrancotirador();
        List<UICapable> elementos = List.of(jugador, francotirador);

        List<Map<String, Object>> respuesta = new ArrayList<>();
        for (UICapable elemento : elementos) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("clase", elemento.getClass().getSimpleName());
            item.put("familia", (elemento instanceof Arma)
                    ? "Familia Arma (Balística)"
                    : "Familia Jugador (Personaje/Entidad)");
            item.put("tipoInterfaz", UICapable.class.getName());
            item.put("reemplazaMetodoDefault", elemento instanceof Francotirador);
            item.put("mensajeUI", elemento.renderizarUI());
            item.put("imagen", elemento.getImagen());
            item.put("posicion", elemento.getPosicion());
            item.put("sonido", elemento.getSonido());
            item.put("velocidad", elemento.getVelocidad());
            respuesta.add(item);
        }
        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/renderizar")
    public ResponseEntity<?> renderizarElemento(
            @RequestParam(defaultValue = "francotirador") String tipo,
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String bando) {

        UICapable elemento;
        boolean reemplazaDefault;

        if ("jugador".equalsIgnoreCase(tipo)) {
            elemento = jugadorService.listarTodas().stream()
                    .filter(j -> nombre != null && j.getAlias().equalsIgnoreCase(nombre))
                    .findFirst()
                    .orElseGet(jugadorService::primerJugador);
            reemplazaDefault = false;
        } else if ("francotirador".equalsIgnoreCase(tipo) || "arma".equalsIgnoreCase(tipo)) {
            elemento = armaService.primerFrancotirador();
            reemplazaDefault = true;
        } else {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Tipo no soportado. Valores válidos: 'francotirador' o 'jugador'."
            ));
        }

        Map<String, Object> respuesta = new LinkedHashMap<>();
        respuesta.put("tipoInterfaz", UICapable.class.getSimpleName());
        respuesta.put("claseConcreta", elemento.getClass().getSimpleName());
        respuesta.put("reemplazaDefault", reemplazaDefault);
        respuesta.put("mensajeTexto", elemento.renderizarUI());
        return ResponseEntity.ok(respuesta);
    }
}
