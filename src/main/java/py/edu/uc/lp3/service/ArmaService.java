package py.edu.uc.lp3.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import py.edu.uc.lp3.domain.Arma;
import py.edu.uc.lp3.domain.Francotirador;
import py.edu.uc.lp3.domain.Pistola;
import py.edu.uc.lp3.exception.EconomiaInvalidaException;
import py.edu.uc.lp3.exception.RecursoNoEncontradoException;
import py.edu.uc.lp3.exception.ReglaNegocioException;
import py.edu.uc.lp3.repository.ArmaRepository;

import java.util.List;
import java.util.Map;

/**
 * Capa de Negocio / Servicio (Service Layer) para la entidad Arma.
 * Centraliza las reglas de negocio, validaciones de dominio y transaccionalidad.
 */
@Service
public class ArmaService {

    public static final float PRECIO_MAXIMO_CS2 = 16000.0f;
    public static final int DANO_MAXIMO_CS2 = 500;

    private final ArmaRepository armaRepository;

    public ArmaService(ArmaRepository armaRepository) {
        this.armaRepository = armaRepository;
    }

    /**
     * Retorna el listado completo de armas persistidas en la base de datos H2.
     */
    @Transactional(readOnly = true)
    public List<Arma> listarTodas() {
        return armaRepository.findAll();
    }

    /**
     * Busca un arma por su identificador primario (ID).
     * Lanza RecursoNoEncontradoException si no existe.
     */
    @Transactional(readOnly = true)
    public Arma buscarPorId(Long id) {
        if (id == null || id <= 0) {
            throw new ReglaNegocioException("El ID del arma debe ser un número positivo.");
        }
        return armaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el arma con ID: " + id));
    }

    /**
     * Registra una nueva arma en la base de datos aplicando validaciones de negocio.
     */
    @Transactional
    public Arma crear(Arma nuevaArma) {
        if (nuevaArma == null) {
            throw new ReglaNegocioException("Los datos del arma no pueden ser nulos.");
        }

        validarDatosArma(nuevaArma);

        // Validación de negocio: Unicidad del nombre del arma
        String nombreLimpio = nuevaArma.getNombre().trim();
        if (armaRepository.existsByNombreIgnoreCase(nombreLimpio)) {
            throw new ReglaNegocioException("Ya existe un arma registrada con el nombre: '" + nombreLimpio + "'.");
        }

        nuevaArma.setId(null); // Asegura inserción de nuevo registro
        nuevaArma.setNombre(nombreLimpio);

        // Regla de negocio: si no se especifica munición actual válida, se inicializa al tope del cargador
        if (nuevaArma.getMunicionActual() <= 0 || nuevaArma.getMunicionActual() > nuevaArma.getMunicionMax()) {
            nuevaArma.recargar();
        }

        return armaRepository.save(nuevaArma);
    }

    /**
     * Actualiza completamente los datos de un arma existente (reemplazo semántico PUT).
     */
    @Transactional
    public Arma actualizar(Long id, Arma datosNuevos) {
        if (datosNuevos == null) {
            throw new ReglaNegocioException("Los datos de actualización no pueden ser nulos.");
        }

        Arma armaExistente = buscarPorId(id);
        validarDatosArma(datosNuevos);

        String nuevoNombre = datosNuevos.getNombre().trim();

        // Validación de negocio: El nuevo nombre no puede colisionar con otra arma distinta
        if (armaRepository.existsByNombreIgnoreCaseAndIdNot(nuevoNombre, id)) {
            throw new ReglaNegocioException("Ya existe otra arma registrada con el nombre: '" + nuevoNombre + "'.");
        }

        armaExistente.setNombre(nuevoNombre);
        armaExistente.setPrecio(datosNuevos.getPrecio());
        armaExistente.setDano(datosNuevos.getDano());
        armaExistente.setPeso(datosNuevos.getPeso());
        armaExistente.setMunicionMax(datosNuevos.getMunicionMax());

        if (datosNuevos.getMunicionActual() > 0 && datosNuevos.getMunicionActual() <= datosNuevos.getMunicionMax()) {
            armaExistente.setMunicionActual(datosNuevos.getMunicionActual());
        } else {
            armaExistente.recargar();
        }

        // Actualizar campos específicos si coinciden los subtipos
        if (armaExistente instanceof Francotirador fExistente && datosNuevos instanceof Francotirador fNuevo) {
            fExistente.setZoom(fNuevo.getZoom());
        } else if (armaExistente instanceof Pistola pExistente && datosNuevos instanceof Pistola pNuevo) {
            pExistente.setCargador(pNuevo.getCargador());
            pExistente.setModoDisparo(pNuevo.getModoDisparo());
        }

        return armaRepository.save(armaExistente);
    }

    /**
     * Actualiza parcialmente un conjunto de campos de un arma (semántica PATCH).
     */
    @Transactional
    public Arma actualizarParcial(Long id, Map<String, Object> campos) {
        Arma arma = buscarPorId(id);

        if (campos == null || campos.isEmpty()) {
            return arma;
        }

        if (campos.containsKey("nombre")) {
            String nuevoNombre = String.valueOf(campos.get("nombre")).trim();
            if (nuevoNombre.isBlank()) {
                throw new ReglaNegocioException("El nombre del arma no puede estar vacío.");
            }
            if (armaRepository.existsByNombreIgnoreCaseAndIdNot(nuevoNombre, id)) {
                throw new ReglaNegocioException("Ya existe otra arma registrada con el nombre: '" + nuevoNombre + "'.");
            }
            arma.setNombre(nuevoNombre);
        }

        if (campos.containsKey("precio")) {
            float nuevoPrecio = Float.parseFloat(campos.get("precio").toString());
            if (nuevoPrecio < 0 || nuevoPrecio > PRECIO_MAXIMO_CS2) {
                throw new EconomiaInvalidaException(nuevoPrecio);
            }
            arma.setPrecio(nuevoPrecio);
        }

        if (campos.containsKey("dano") || campos.containsKey("danio")) {
            Object val = campos.containsKey("dano") ? campos.get("dano") : campos.get("danio");
            int nuevoDano = Integer.parseInt(val.toString());
            if (nuevoDano <= 0 || nuevoDano > DANO_MAXIMO_CS2) {
                throw new ReglaNegocioException("El daño debe estar entre 1 y " + DANO_MAXIMO_CS2 + ".");
            }
            arma.setDano(nuevoDano);
        }

        if (campos.containsKey("peso")) {
            float nuevoPeso = Float.parseFloat(campos.get("peso").toString());
            if (nuevoPeso <= 0) {
                throw new ReglaNegocioException("El peso debe ser mayor a 0.");
            }
            arma.setPeso(nuevoPeso);
        }

        if (campos.containsKey("municionMax")) {
            int nuevaMax = Integer.parseInt(campos.get("municionMax").toString());
            if (nuevaMax <= 0) {
                throw new ReglaNegocioException("La capacidad máxima debe ser mayor a 0.");
            }
            arma.setMunicionMax(nuevaMax);
        }

        if (campos.containsKey("municionActual")) {
            int nuevaActual = Integer.parseInt(campos.get("municionActual").toString());
            arma.setMunicionActual(nuevaActual);
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

        return armaRepository.save(arma);
    }

    /**
     * Elimina un arma de la base de datos por su ID.
     */
    @Transactional
    public void eliminar(Long id) {
        Arma arma = buscarPorId(id);
        armaRepository.delete(arma);
    }

    /**
     * Operación de negocio: Recarga la munición de un arma persistida.
     */
    @Transactional
    public Arma recargarArma(Long id) {
        Arma arma = buscarPorId(id);
        arma.recargar();
        return armaRepository.save(arma);
    }

    /**
     * Operación de negocio: Ejecuta un disparo sobre el arma persistida y actualiza su munición en BD.
     */
    @Transactional
    public String dispararArma(Long id, Integer distancia, Boolean headshot) {
        Arma arma = buscarPorId(id);
        String resultadoDisparo;

        if (distancia == null) {
            resultadoDisparo = arma.disparar();
        } else if (headshot == null) {
            resultadoDisparo = arma.disparar(distancia);
        } else {
            resultadoDisparo = arma.disparar(distancia, headshot);
        }

        armaRepository.save(arma); // Persiste el nuevo estado de munición
        return resultadoDisparo;
    }

    /**
     * Validaciones exhaustivas de dominio y reglas de negocio del CS2.
     */
    private void validarDatosArma(Arma arma) {
        if (arma.getNombre() == null || arma.getNombre().isBlank()) {
            throw new ReglaNegocioException("El nombre del arma es obligatorio y no puede estar vacío.");
        }
        if (arma.getPrecio() < 0) {
            throw new IllegalArgumentException("El precio del arma no puede ser negativo.");
        }
        if (arma.getPrecio() > PRECIO_MAXIMO_CS2) {
            throw new EconomiaInvalidaException("El precio ($" + arma.getPrecio()
                    + ") excede el tope económico máximo de Counter-Strike 2 ($" + PRECIO_MAXIMO_CS2 + ").",
                    arma.getPrecio(), PRECIO_MAXIMO_CS2);
        }
        if (arma.getDano() <= 0) {
            throw new ReglaNegocioException("El daño base del arma debe ser estrictamente mayor a cero.");
        }
        if (arma.getDano() > DANO_MAXIMO_CS2) {
            throw new ReglaNegocioException("El daño base (" + arma.getDano()
                    + ") supera el límite balístico permitido (" + DANO_MAXIMO_CS2 + ").");
        }
        if (arma.getPeso() <= 0) {
            throw new ReglaNegocioException("El peso del arma debe ser mayor a cero kg.");
        }
        if (arma.getMunicionMax() <= 0) {
            throw new ReglaNegocioException("La capacidad máxima de munición debe ser mayor a cero.");
        }
    }
}
