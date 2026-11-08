package py.edu.uc.lp3.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import py.edu.uc.lp3.domain.Arma;

import java.util.List;
import java.util.Optional;

/**
 * Capa de Persistencia / Acceso a Datos (Spring Data JPA) para la entidad Arma.
 * Provee operaciones CRUD estándar y métodos derivados para reglas de negocio.
 */
@Repository
public interface ArmaRepository extends JpaRepository<Arma, Long> {

    /**
     * Busca un arma por su nombre exacto ignorando mayúsculas y minúsculas.
     */
    Optional<Arma> findByNombreIgnoreCase(String nombre);

    /**
     * Verifica si ya existe un arma registrada con el nombre especificado.
     */
    boolean existsByNombreIgnoreCase(String nombre);

    /**
     * Verifica si ya existe otra arma registrada con el mismo nombre y distinto ID.
     */
    boolean existsByNombreIgnoreCaseAndIdNot(String nombre, Long id);

    /**
     * Filtra armas por un precio máximo permitido (ej. armas económicas para CS2).
     */
    List<Arma> findByPrecioLessThanEqual(float precioMax);
}
