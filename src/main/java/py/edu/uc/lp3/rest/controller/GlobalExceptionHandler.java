package py.edu.uc.lp3.rest.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import py.edu.uc.lp3.exception.*;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Manejador global de excepciones para la API REST.
 * Posee un handler HTTP dedicado por cada tipo de excepción de la jerarquía del dominio
 * para que el cliente (Insomnia / navegador) distinga a las hermanas por código HTTP y por JSON.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handler para Hija 1: MunicionAgotadaException.
     * Retorna HTTP 409 CONFLICT: Conflicto con el estado actual del arma en el juego (sin cartuchos).
     */
    @ExceptionHandler(MunicionAgotadaException.class)
    public ResponseEntity<Map<String, Object>> manejarMunicionAgotada(MunicionAgotadaException ex) {
        Map<String, Object> error = new LinkedHashMap<>();
        error.put("timestamp", LocalDateTime.now().toString());
        error.put("status", HttpStatus.CONFLICT.value());
        error.put("error", "Munición Agotada");
        error.put("tipoExcepcion", ex.getClass().getSimpleName());
        error.put("codigo", "CS2_ARMA_SIN_MUNICION");
        error.put("arma", ex.getNombreArma());
        error.put("municionActual", ex.getMunicionActual());
        error.put("municionMax", ex.getMunicionMax());
        error.put("mensaje", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    /**
     * Handler para Hija 2: EconomiaInvalidaException.
     * Retorna HTTP 422 UNPROCESSABLE ENTITY: Violación de reglas financieras competitivas de CS2.
     */
    @ExceptionHandler(EconomiaInvalidaException.class)
    public ResponseEntity<Map<String, Object>> manejarEconomiaInvalida(EconomiaInvalidaException ex) {
        Map<String, Object> error = new LinkedHashMap<>();
        error.put("timestamp", LocalDateTime.now().toString());
        error.put("status", HttpStatus.UNPROCESSABLE_ENTITY.value());
        error.put("error", "Regla Económica CS2 Violada");
        error.put("tipoExcepcion", ex.getClass().getSimpleName());
        error.put("codigo", "CS2_ECONOMIA_EXCEDIDA");
        error.put("precioSolicitado", ex.getPrecio());
        error.put("limiteMaximo", ex.getLimiteMaximo());
        error.put("mensaje", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(error);
    }

    /**
     * Handler para recurso inexistente por ID.
     * Retorna HTTP 404 NOT FOUND.
     */
    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> manejarRecursoNoEncontrado(RecursoNoEncontradoException ex) {
        Map<String, Object> error = new LinkedHashMap<>();
        error.put("timestamp", LocalDateTime.now().toString());
        error.put("status", HttpStatus.NOT_FOUND.value());
        error.put("error", "Recurso no encontrado");
        error.put("tipoExcepcion", ex.getClass().getSimpleName());
        error.put("codigo", "CS2_NOT_FOUND");
        error.put("mensaje", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /**
     * Handler para violaciones de reglas de negocio generales.
     * Retorna HTTP 400 BAD REQUEST.
     */
    @ExceptionHandler(ReglaNegocioException.class)
    public ResponseEntity<Map<String, Object>> manejarReglaNegocio(ReglaNegocioException ex) {
        Map<String, Object> error = new LinkedHashMap<>();
        error.put("timestamp", LocalDateTime.now().toString());
        error.put("status", HttpStatus.BAD_REQUEST.value());
        error.put("error", "Violación de regla de negocio");
        error.put("tipoExcepcion", ex.getClass().getSimpleName());
        error.put("codigo", "CS2_REGLA_NEGOCIO");
        error.put("mensaje", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Handler para invariantes de construcción en constructores o setters.
     * Retorna HTTP 400 BAD REQUEST.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> manejarArgumentoIlegal(IllegalArgumentException ex) {
        Map<String, Object> error = new LinkedHashMap<>();
        error.put("timestamp", LocalDateTime.now().toString());
        error.put("status", HttpStatus.BAD_REQUEST.value());
        error.put("error", "Invariante de construcción inválido");
        error.put("tipoExcepcion", ex.getClass().getSimpleName());
        error.put("codigo", "CS2_INVARIANTE_INVALIDO");
        error.put("mensaje", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Handler fallback para la clase base de la jerarquía de excepciones del dominio.
     */
    @ExceptionHandler(DominioCS2Exception.class)
    public ResponseEntity<Map<String, Object>> manejarDominioBase(DominioCS2Exception ex) {
        Map<String, Object> error = new LinkedHashMap<>();
        error.put("timestamp", LocalDateTime.now().toString());
        error.put("status", HttpStatus.BAD_REQUEST.value());
        error.put("error", "Excepción del dominio CS2");
        error.put("tipoExcepcion", ex.getClass().getSimpleName());
        error.put("codigo", "CS2_DOMINIO_ERROR");
        error.put("mensaje", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}
