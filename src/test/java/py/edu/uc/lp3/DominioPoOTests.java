package py.edu.uc.lp3;

import org.junit.jupiter.api.Test;
import py.edu.uc.lp3.domain.Arma;
import py.edu.uc.lp3.domain.Francotirador;
import py.edu.uc.lp3.domain.Pistola;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias que verifican el cumplimiento de las consignas de POO:
 * 1. Constructores simples y sobrecargados.
 * 2. Sobreescritura de métodos abstractos (polimorfismo).
 * 3. Sobrecarga de métodos del dominio.
 * 4. Ocultamiento y protección de invariantes.
 */
class DominioPoOTests {

    @Test
    void testConstructoresFrancotirador() {
        // Constructor simple
        Francotirador sniperSimple = new Francotirador("AWP", 4750f, 115, 2);
        assertEquals("AWP", sniperSimple.obtenerNombre());
        assertEquals(4750, sniperSimple.obtenerPrecio());
        assertEquals(115, sniperSimple.getDano());
        assertEquals(2, sniperSimple.getZoom());
        assertEquals(10, sniperSimple.getMunicionMax());

        // Constructor sobrecargado completo
        Francotirador sniperCompleto = new Francotirador("SSG 08", 1700f, 88, 3.6f, 10, 4);
        assertEquals("SSG 08", sniperCompleto.obtenerNombre());
        assertEquals(1700, sniperCompleto.obtenerPrecio());
        assertEquals(3.6f, sniperCompleto.getPeso());
        assertEquals(4, sniperCompleto.getZoom());

        // Constructor por defecto
        Francotirador sniperDefecto = new Francotirador();
        assertEquals("AWP", sniperDefecto.obtenerNombre());
    }

    @Test
    void testConstructoresPistola() {
        // Constructor simple
        Pistola pistolaSimple = new Pistola("Desert Eagle", 700f, 53);
        assertEquals("Desert Eagle", pistolaSimple.obtenerNombre());
        assertEquals(700, pistolaSimple.obtenerPrecio());
        assertEquals(12, pistolaSimple.getCargador());

        // Constructor sobrecargado completo
        Pistola pistolaCompleta = new Pistola("USP-S", 200f, 35, 1.2f, 12, "Silenciado", 12);
        assertEquals("USP-S", pistolaCompleta.obtenerNombre());
        assertEquals("Silenciado", pistolaCompleta.getModoDisparo());

        // Constructor por defecto
        Pistola pistolaDefecto = new Pistola();
        assertEquals("Glock-18", pistolaDefecto.obtenerNombre());
    }

    @Test
    void testSobreescrituraPolimorfica() {
        // Tratar a ambas hijas a través del tipo base Arma
        Arma arma1 = new Pistola("P250", 300f, 38);
        Arma arma2 = new Francotirador("SCAR-20", 5000f, 80, 2);

        String textoPistola = arma1.describirComportamiento();
        String textoSniper = arma2.describirComportamiento();

        assertNotNull(textoPistola);
        assertTrue(textoPistola.contains("Pistola"));
        assertNotNull(textoSniper);
        assertTrue(textoSniper.contains("Francotirador"));
    }

    @Test
    void testSobrecargaMensajeDisparar() {
        Francotirador sniper = new Francotirador("AWP", 4750f, 115, 2);
        int municionInicial = sniper.getMunicionActual();

        // Sobrecarga 1: disparar() sin parámetros
        String res1 = sniper.disparar();
        assertEquals(municionInicial - 1, sniper.getMunicionActual());
        assertTrue(res1.contains("disparo simple"));

        // Sobrecarga 2: disparar(distanciaMetros)
        String res2 = sniper.disparar(50);
        assertEquals(municionInicial - 2, sniper.getMunicionActual());
        assertTrue(res2.contains("50 metros"));

        // Sobrecarga 3: disparar(distanciaMetros, headshot)
        String res3 = sniper.disparar(50, true);
        assertEquals(municionInicial - 3, sniper.getMunicionActual());
        assertTrue(res3.contains("HEADSHOT"));

        // Recarga de dominio
        sniper.recargar();
        assertEquals(sniper.getMunicionMax(), sniper.getMunicionActual());
    }

    @Test
    void testInvariantesYRechazoValoresInvalidos() {
        // Precio negativo
        assertThrows(IllegalArgumentException.class, () -> {
            new Francotirador("AWP", -100f, 115, 2);
        });

        // Daño menor o igual a cero
        assertThrows(IllegalArgumentException.class, () -> {
            new Pistola("Glock", 200f, 0);
        });

        // Zoom menor o igual a cero
        assertThrows(IllegalArgumentException.class, () -> {
            new Francotirador("AWP", 4750f, 115, 0);
        });

        // Nombre en blanco
        assertThrows(IllegalArgumentException.class, () -> {
            new Pistola("   ", 200f, 30);
        });

        // Distancia negativa en sobrecarga de disparar
        Francotirador sniper = new Francotirador();
        assertThrows(IllegalArgumentException.class, () -> {
            sniper.disparar(-10);
        });
    }
}
