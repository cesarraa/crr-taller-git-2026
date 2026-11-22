package py.edu.uc.lp3.rest.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import py.edu.uc.lp3.domain.Arma;
import py.edu.uc.lp3.rest.dto.ArmaRequest;
import py.edu.uc.lp3.service.ArmaService;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/armas")
public class ArmaController {

    private final ArmaService armaService;

    public ArmaController(ArmaService armaService) {
        this.armaService = armaService;
    }

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

        ArmaRequest req = new ArmaRequest();
        req.setTipo(tipo);
        req.setNombre(nombre);
        req.setPrecio(precio);
        req.setDano(dano);
        req.setPeso(peso);
        req.setMunicionMax(municionMax);
        req.setZoom(zoom != null ? zoom : 2);
        req.setModoDisparo(modoDisparo != null ? modoDisparo : "Semiautomático");
        req.setCargador(cargador != null ? cargador : 12);

        Arma armaCreada = armaService.construirDemo(req);
        String constructorUsado = (peso != null && municionMax != null)
                ? "Sobrecargado (nombre, precio, dano, peso, municionMax, rasgos de subtipo)"
                : "Simple (nombre, precio, dano, rasgos de subtipo)";

        Map<String, Object> respuesta = new LinkedHashMap<>();
        respuesta.put("status", "EXITOSO");
        respuesta.put("mensaje", "Objeto creado legalmente y validado por ArmaService.");
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
    }

    @GetMapping("/comportamientos")
    public List<Map<String, Object>> listarComportamientos() {
        List<Map<String, Object>> respuesta = new ArrayList<>();
        for (Arma arma : armaService.ejemplosPolimorficos()) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("tipoClaseHija", arma.getClass().getSimpleName());
            item.put("nombre", arma.obtenerNombre());
            item.put("precio", arma.obtenerPrecio());
            item.put("comportamiento", arma.describirComportamiento());
            respuesta.add(item);
        }
        return respuesta;
    }

    @GetMapping("/disparar")
    public ResponseEntity<?> probarSobrecargaDisparar(
            @RequestParam(defaultValue = "francotirador") String tipo,
            @RequestParam(required = false) Integer distancia,
            @RequestParam(required = false) Boolean headshot) {

        Arma arma = armaService.prepararDemoDisparo(tipo);
        String resultadoMensaje = armaService.dispararDemo(arma, distancia, headshot);
        String firmaSobrecargada = distancia == null
                ? "disparar() [sin argumentos]"
                : (headshot == null
                    ? "disparar(int distanciaMetros) [con distancia]"
                    : "disparar(int distanciaMetros, boolean tiroALaCabeza)");

        Map<String, Object> respuesta = new LinkedHashMap<>();
        respuesta.put("status", "OK");
        respuesta.put("arma", arma.obtenerNombre());
        respuesta.put("sobrecargaEjecutada", firmaSobrecargada);
        respuesta.put("resultado", resultadoMensaje);
        respuesta.put("municionRestante", arma.getMunicionActual());
        return ResponseEntity.ok(respuesta);
    }
}
