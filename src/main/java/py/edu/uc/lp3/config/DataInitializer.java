package py.edu.uc.lp3.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import py.edu.uc.lp3.domain.Arma;
import py.edu.uc.lp3.domain.Francotirador;
import py.edu.uc.lp3.domain.Jugador;
import py.edu.uc.lp3.domain.Pistola;
import py.edu.uc.lp3.repository.ArmaRepository;
import py.edu.uc.lp3.repository.JugadorRepository;

/**
 * Inicializador de datos para la base de datos en memoria H2.
 * Carga armas y combatientes reglamentarios de Counter-Strike 2 al arrancar el microservicio.
 */
@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(ArmaRepository armaRepository, JugadorRepository jugadorRepository) {
        return args -> {
            if (armaRepository.count() == 0) {
                // Fusil de asalto icónico
                armaRepository.save(new Arma("AK-47", 2700f, 36, 3.47f, 30));

                // Fusil contraterrorista
                armaRepository.save(new Arma("M4A4", 3100f, 33, 3.40f, 30));

                // Francotirador de precisión (Hija 1)
                armaRepository.save(new Francotirador("AWP", 4750f, 115, 4.30f, 10, 2));

                // Pistola pesada (Hija 2)
                armaRepository.save(new Pistola("Desert Eagle", 700f, 53, 1.80f, 7, "Semiautomático", 7));
            }

            if (jugadorRepository.count() == 0) {
                // Operador Contraterrorista
                jugadorRepository.save(new Jugador("s1mple", "Antiterrorista", 100, 16000));

                // Operador Terrorista
                jugadorRepository.save(new Jugador("ZywOo", "Terrorista", 100, 14500));
            }
        };
    }
}
