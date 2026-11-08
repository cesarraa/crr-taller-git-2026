package py.edu.uc.lp3.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import py.edu.uc.lp3.domain.Jugador;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio de Spring Data JPA para la entidad persistente Jugador.
 */
@Repository
public interface JugadorRepository extends JpaRepository<Jugador, Long> {

    Optional<Jugador> findByAliasIgnoreCase(String alias);

    boolean existsByAliasIgnoreCase(String alias);

    List<Jugador> findByBandoIgnoreCase(String bando);
}
