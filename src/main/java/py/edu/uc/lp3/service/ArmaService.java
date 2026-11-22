package py.edu.uc.lp3.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import py.edu.uc.lp3.domain.*;
import py.edu.uc.lp3.exception.EconomiaInvalidaException;
import py.edu.uc.lp3.exception.MunicionAgotadaException;
import py.edu.uc.lp3.exception.RecursoNoEncontradoException;
import py.edu.uc.lp3.exception.ReglaNegocioException;
import py.edu.uc.lp3.repository.ArmaRepository;
import py.edu.uc.lp3.rest.dto.ArmaRequest;

import java.util.List;
import java.util.Map;

@Service
public class ArmaService {

    public static final float PRECIO_MAXIMO_CS2 = 16000.0f;
    public static final int DANO_MAXIMO_CS2 = 500;

    private final ArmaRepository armaRepository;

    public ArmaService(ArmaRepository armaRepository) {
        this.armaRepository = armaRepository;
    }

    @Transactional(readOnly = true)
    public List<Arma> listarTodas() {
        return armaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Arma buscarPorId(Long id) {
        if (id == null || id <= 0) {
            throw new ReglaNegocioException("El ID del arma debe ser un número positivo.");
        }
        return armaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el arma con ID: " + id));
    }

    @Transactional(readOnly = true)
    public Francotirador primerFrancotirador() {
        return armaRepository.findAll().stream()
                .filter(Francotirador.class::isInstance)
                .map(Francotirador.class::cast)
                .findFirst()
                .orElseThrow(() -> new RecursoNoEncontradoException("No hay francotiradores persistidos."));
    }

    @Transactional
    public Arma crear(ArmaRequest req) {
        if (req == null) {
            throw new ReglaNegocioException("Los datos del arma no pueden ser nulos.");
        }
        validarDatos(req);
        String nombre = req.getNombre().trim();
        if (armaRepository.existsByNombreIgnoreCase(nombre)) {
            throw new ReglaNegocioException("Ya existe un arma registrada con el nombre: '" + nombre + "'.");
        }
        Arma arma = construirArma(req);
        arma.setNombre(nombre);
        aplicarMunicion(arma, req.getMunicionActual());
        return armaRepository.save(arma);
    }

    @Transactional
    public Arma actualizar(Long id, ArmaRequest req) {
        if (req == null) {
            throw new ReglaNegocioException("Los datos de actualización no pueden ser nulos.");
        }
        Arma arma = buscarPorId(id);
        validarDatos(req);
        String nombre = req.getNombre().trim();
        if (armaRepository.existsByNombreIgnoreCaseAndIdNot(nombre, id)) {
            throw new ReglaNegocioException("Ya existe otra arma registrada con el nombre: '" + nombre + "'.");
        }
        aplicarDatos(arma, req);
        return armaRepository.save(arma);
    }

    @Transactional
    public Arma actualizarParcial(Long id, Map<String, Object> campos) {
        Arma arma = buscarPorId(id);
        if (campos == null || campos.isEmpty()) {
            return arma;
        }

        try {
            if (campos.containsKey("nombre")) {
                String nombre = String.valueOf(campos.get("nombre")).trim();
                if (nombre.isBlank()) {
                    throw new ReglaNegocioException("El nombre del arma no puede estar vacío.");
                }
                if (armaRepository.existsByNombreIgnoreCaseAndIdNot(nombre, id)) {
                    throw new ReglaNegocioException("Ya existe otra arma registrada con el nombre: '" + nombre + "'.");
                }
                arma.setNombre(nombre);
            }
            if (campos.containsKey("precio")) {
                arma.setPrecio(Float.parseFloat(campos.get("precio").toString()));
            }
            if (campos.containsKey("dano") || campos.containsKey("danio")) {
                Object valor = campos.containsKey("dano") ? campos.get("dano") : campos.get("danio");
                arma.setDano(Integer.parseInt(valor.toString()));
            }
            if (campos.containsKey("peso")) {
                arma.setPeso(Float.parseFloat(campos.get("peso").toString()));
            }
            if (campos.containsKey("municionMax")) {
                arma.setMunicionMax(Integer.parseInt(campos.get("municionMax").toString()));
            }
            if (campos.containsKey("municionActual")) {
                int actual = Integer.parseInt(campos.get("municionActual").toString());
                if (actual < 0) {
                    throw new ReglaNegocioException("La munición actual no puede ser negativa.");
                }
                arma.setMunicionActual(Math.min(actual, arma.getMunicionMax()));
            }
            if (arma instanceof Francotirador f && campos.containsKey("zoom")) {
                f.setZoom(Integer.parseInt(campos.get("zoom").toString()));
            }
            if (arma instanceof Pistola p) {
                if (campos.containsKey("cargador")) {
                    p.setCargador(Integer.parseInt(campos.get("cargador").toString()));
                }
                if (campos.containsKey("modoDisparo")) {
                    p.setModoDisparo(String.valueOf(campos.get("modoDisparo")));
                }
            }
        } catch (NumberFormatException e) {
            throw new ReglaNegocioException("Uno de los campos enviados no tiene el formato numérico esperado.");
        }

        validarEstado(arma);
        return armaRepository.save(arma);
    }

    @Transactional
    public void eliminar(Long id) {
        armaRepository.delete(buscarPorId(id));
    }

    @Transactional
    public Arma recargarArma(Long id) {
        Arma arma = buscarPorId(id);
        arma.setMunicionActual(arma.getMunicionMax());
        return armaRepository.save(arma);
    }

    @Transactional
    public String dispararArma(Long id, Integer distancia, Boolean headshot) {
        Arma arma = buscarPorId(id);
        String resultado = disparar(arma, distancia, headshot);
        armaRepository.save(arma);
        return resultado;
    }

    public Arma construirDemo(ArmaRequest req) {
        validarDatos(req);
        Arma arma = construirArma(req);
        aplicarMunicion(arma, req.getMunicionActual());
        return arma;
    }

    public List<Arma> ejemplosPolimorficos() {
        ArmaRequest pistola = new ArmaRequest();
        pistola.setTipo("PISTOLA");
        pistola.setNombre("Desert Eagle");
        pistola.setPrecio(700f);
        pistola.setDano(53);
        pistola.setPeso(1.8f);
        pistola.setMunicionMax(7);
        pistola.setModoDisparo("Semiautomático");
        pistola.setCargador(7);

        ArmaRequest francotirador = new ArmaRequest();
        francotirador.setTipo("FRANCOTIRADOR");
        francotirador.setNombre("AWP");
        francotirador.setPrecio(4750f);
        francotirador.setDano(115);
        francotirador.setPeso(4.3f);
        francotirador.setMunicionMax(10);
        francotirador.setZoom(2);

        return List.of(construirDemo(pistola), construirDemo(francotirador));
    }

    public Arma prepararDemoDisparo(String tipo) {
        ArmaRequest req = new ArmaRequest();
        if ("pistola".equalsIgnoreCase(tipo)) {
            req.setTipo("PISTOLA");
            req.setNombre("Glock-18");
            req.setPrecio(200f);
            req.setDano(28);
            req.setPeso(0.9f);
            req.setMunicionMax(20);
            req.setModoDisparo("Semiautomático");
            req.setCargador(20);
        } else {
            req.setTipo("FRANCOTIRADOR");
            req.setNombre("AWP");
            req.setPrecio(4750f);
            req.setDano(115);
            req.setPeso(4.3f);
            req.setMunicionMax(10);
            req.setZoom(2);
        }
        return construirDemo(req);
    }

    public String dispararDemo(Arma arma, Integer distancia, Boolean headshot) {
        return disparar(arma, distancia, headshot);
    }

    public int calcularDanioEfectivo(Arma arma, int distanciaMetros, boolean tiroALaCabeza) {
        double atenuacion;
        double multiplicador;
        if (arma instanceof Francotirador f) {
            atenuacion = Math.max(0.75, 1.0 - (distanciaMetros * 0.001));
            multiplicador = tiroALaCabeza ? (2.0 + f.getZoom()) : 1.0;
        } else if (arma instanceof Pistola) {
            atenuacion = Math.max(0.15, 1.0 - (distanciaMetros * 0.015));
            multiplicador = tiroALaCabeza ? 2.0 : 1.0;
        } else {
            atenuacion = Math.max(0.20, 1.0 - (distanciaMetros * 0.0075));
            multiplicador = tiroALaCabeza ? 2.5 : 1.0;
        }
        return (int) Math.round(arma.getDano() * atenuacion * multiplicador);
    }

    public void validarMunicion(Arma arma) {
        if (arma.getMunicionActual() <= 0) {
            throw new MunicionAgotadaException(arma.getNombre(), arma.getMunicionActual(), arma.getMunicionMax());
        }
    }

    public void consumirMunicion(Arma arma) {
        validarMunicion(arma);
        arma.setMunicionActual(arma.getMunicionActual() - 1);
    }

    private String disparar(Arma arma, Integer distancia, Boolean headshot) {
        if (distancia == null) {
            validarMunicion(arma);
            int dano = arma.getDano();
            consumirMunicion(arma);
            return arma.getNombre() + " efectuó un disparo simple (" + dano + " daño base). Munición restante: "
                    + arma.getMunicionActual() + "/" + arma.getMunicionMax() + ".";
        }
        validarDistancia(distancia);
        boolean critical = Boolean.TRUE.equals(headshot);
        int dano = calcularDanioEfectivo(arma, distancia, critical);
        consumirMunicion(arma);
        String zona = critical ? "¡HEADSHOT!" : "Impacto corporal";
        return arma.getNombre() + " disparó a " + distancia + "m (" + zona + ") causando " + dano
                + " de daño. Munición: " + arma.getMunicionActual() + "/" + arma.getMunicionMax() + ".";
    }

    private void validarDistancia(int distancia) {
        if (distancia < 0) {
            throw new ReglaNegocioException("La distancia del objetivo no puede ser negativa.");
        }
    }

    private void validarDatos(ArmaRequest req) {
        if (req.getNombre() == null || req.getNombre().isBlank()) {
            throw new ReglaNegocioException("El nombre del arma es obligatorio y no puede estar vacío.");
        }
        if (req.getPrecio() == null || req.getPrecio() < 0 || req.getPrecio() > PRECIO_MAXIMO_CS2) {
            throw new EconomiaInvalidaException(req.getPrecio() == null ? 0f : req.getPrecio());
        }
        if (req.getDano() == null || req.getDano() <= 0 || req.getDano() > DANO_MAXIMO_CS2) {
            throw new ReglaNegocioException("El daño base debe estar entre 1 y " + DANO_MAXIMO_CS2 + ".");
        }
        if (req.getPeso() == null || req.getPeso() <= 0) {
            throw new ReglaNegocioException("El peso del arma debe ser mayor a cero kg.");
        }
        if (req.getMunicionMax() == null || req.getMunicionMax() <= 0) {
            throw new ReglaNegocioException("La capacidad máxima de munición debe ser mayor a cero.");
        }
        if (req.getMunicionActual() != null && req.getMunicionActual() < 0) {
            throw new ReglaNegocioException("La munición actual no puede ser negativa.");
        }
        validarDatosSubtipo(req);
    }

    private void validarDatosSubtipo(ArmaRequest req) {
        String tipo = normalizarTipo(req.getTipo());
        switch (tipo) {
            case "FRANCOTIRADOR" -> {
                if (req.getZoom() == null || req.getZoom() <= 0) {
                    throw new ReglaNegocioException("El zoom del francotirador debe ser mayor a cero.");
                }
            }
            case "PISTOLA", "SUBFUSIL" -> {
                if (req.getCargador() == null || req.getCargador() <= 0) {
                    throw new ReglaNegocioException("La capacidad del cargador debe ser mayor a cero.");
                }
                if (req.getModoDisparo() == null || req.getModoDisparo().isBlank()) {
                    throw new ReglaNegocioException("El modo de disparo no puede estar vacío.");
                }
            }
            case "ESCOPETA" -> {
                if (req.getCartuchos() == null || req.getCartuchos() <= 0) {
                    throw new ReglaNegocioException("La cantidad de cartuchos debe ser mayor a cero.");
                }
                if (req.getDispersion() == null || req.getDispersion() <= 0) {
                    throw new ReglaNegocioException("La dispersión debe ser mayor a cero.");
                }
            }
            case "GRANADA", "GRANADA_FLASH", "GRANADA_HUMO", "GRANADA_INCENDIARIA" -> {
                if (req.getRadioExplosion() != null && req.getRadioExplosion() < 0) {
                    throw new ReglaNegocioException("El radio de explosión no puede ser negativo.");
                }
                if (req.getTipoGranada() != null) {
                    try {
                        GranadaTipo.valueOf(req.getTipoGranada().trim().toUpperCase());
                    } catch (IllegalArgumentException e) {
                        throw new ReglaNegocioException("El tipo de granada no es válido.");
                    }
                }
            }
            default -> {
            }
        }
    }

    private void validarEstado(Arma arma) {
        if (arma.getNombre() == null || arma.getNombre().isBlank()) {
            throw new ReglaNegocioException("El nombre del arma no puede estar vacío.");
        }
        if (arma.getPrecio() < 0 || arma.getPrecio() > PRECIO_MAXIMO_CS2) {
            throw new EconomiaInvalidaException(arma.getPrecio());
        }
        if (arma.getDano() <= 0 || arma.getDano() > DANO_MAXIMO_CS2) {
            throw new ReglaNegocioException("El daño base debe estar entre 1 y " + DANO_MAXIMO_CS2 + ".");
        }
        if (arma.getPeso() <= 0 || arma.getMunicionMax() <= 0) {
            throw new ReglaNegocioException("El peso y la capacidad de munición deben ser mayores a cero.");
        }
        if (arma.getMunicionActual() < 0 || arma.getMunicionActual() > arma.getMunicionMax()) {
            throw new ReglaNegocioException("La munición actual debe estar entre 0 y la capacidad máxima.");
        }
        if (arma instanceof Francotirador f && f.getZoom() <= 0) {
            throw new ReglaNegocioException("El zoom del francotirador debe ser mayor a cero.");
        }
        if (arma instanceof Pistola p && (p.getCargador() <= 0 || p.getModoDisparo() == null || p.getModoDisparo().isBlank())) {
            throw new ReglaNegocioException("La pistola requiere un cargador y un modo de disparo válidos.");
        }
        if (arma instanceof Escopeta e && (e.getCartuchos() <= 0 || e.getDispersion() <= 0)) {
            throw new ReglaNegocioException("La escopeta requiere cartuchos y dispersión válidos.");
        }
        if (arma instanceof Subfusil s && (s.getCargador() <= 0 || s.getModoDisparo() == null || s.getModoDisparo().isBlank())) {
            throw new ReglaNegocioException("El subfusil requiere un cargador y un modo de disparo válidos.");
        }
        if (arma instanceof Granada g && (g.getTipoGranada() == null || g.getRadioExplosion() < 0)) {
            throw new ReglaNegocioException("La granada requiere un tipo y un radio de explosión válidos.");
        }
    }

    private void aplicarMunicion(Arma arma, Integer actual) {
        if (actual != null && actual > 0 && actual <= arma.getMunicionMax()) {
            arma.setMunicionActual(actual);
        } else {
            arma.setMunicionActual(arma.getMunicionMax());
        }
    }

    private void aplicarDatos(Arma arma, ArmaRequest req) {
        arma.setNombre(req.getNombre().trim());
        arma.setPrecio(req.getPrecio());
        arma.setDano(req.getDano());
        arma.setPeso(req.getPeso());
        arma.setMunicionMax(req.getMunicionMax());
        aplicarMunicion(arma, req.getMunicionActual());

        if (arma instanceof Francotirador f && req.getZoom() != null) {
            f.setZoom(req.getZoom());
        }
        if (arma instanceof Pistola p) {
            if (req.getCargador() != null) {
                p.setCargador(req.getCargador());
            }
            if (req.getModoDisparo() != null) {
                p.setModoDisparo(req.getModoDisparo());
            }
        }
        if (arma instanceof Granada g && req.getRadioExplosion() != null) {
            g.setRadioExplosion(req.getRadioExplosion());
        }
    }

    private Arma construirArma(ArmaRequest req) {
        String tipo = normalizarTipo(req.getTipo());
        float precio = req.getPrecio();
        int dano = req.getDano();
        float peso = req.getPeso() != null ? req.getPeso() : 2.5f;
        int max = req.getMunicionMax();
        String nombre = req.getNombre().trim();

        return switch (tipo) {
            case "FRANCOTIRADOR" -> new Francotirador(nombre, precio, dano, peso, max, req.getZoom());
            case "PISTOLA" -> new Pistola(nombre, precio, dano, peso, max, req.getModoDisparo(), req.getCargador());
            case "ESCOPETA" -> new Escopeta(nombre, precio, dano, peso, max, req.getCartuchos(), req.getDispersion());
            case "SUBFUSIL" -> new Subfusil(nombre, precio, dano, peso, max, req.getModoDisparo(), req.getCargador());
            case "GRANADA_FLASH" -> new GranadaFlash(nombre, precio, dano, peso, max, radio(req));
            case "GRANADA_HUMO" -> new GranadaHumo(nombre, precio, dano, peso, max, radio(req));
            case "GRANADA_INCENDIARIA" -> new GranadaIncendiaria(nombre, precio, dano, peso, max, radio(req));
            case "GRANADA" -> new Granada(nombre, precio, dano, peso, max, tipoGranada(req), radio(req));
            default -> new Fusil(nombre, precio, dano, peso, max);
        };
    }

    private float radio(ArmaRequest req) {
        return req.getRadioExplosion() != null ? req.getRadioExplosion() : 5.0f;
    }

    private GranadaTipo tipoGranada(ArmaRequest req) {
        return req.getTipoGranada() == null
                ? GranadaTipo.HUMO
                : GranadaTipo.valueOf(req.getTipoGranada().trim().toUpperCase());
    }

    private String normalizarTipo(String tipo) {
        if (tipo == null || tipo.isBlank()) {
            return "FUSIL";
        }
        return switch (tipo.trim().toUpperCase()) {
            case "RIFLE", "AK47", "AK-47", "M4", "M4A4" -> "FUSIL";
            case "SNIPER", "AWP" -> "FRANCOTIRADOR";
            case "SMG" -> "SUBFUSIL";
            default -> tipo.trim().toUpperCase();
        };
    }
}
