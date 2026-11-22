package py.edu.uc.lp3;

import org.junit.jupiter.api.Test;
import py.edu.uc.lp3.domain.Arma;
import py.edu.uc.lp3.domain.Francotirador;
import py.edu.uc.lp3.domain.Fusil;
import py.edu.uc.lp3.domain.Jugador;
import py.edu.uc.lp3.domain.Pistola;
import py.edu.uc.lp3.domain.UICapable;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.*;

class DominioPoOTests {

    @Test
    void testConstructoresFrancotirador() {
        Francotirador simple = new Francotirador("AWP", 4750f, 115, 2);
        assertEquals("AWP", simple.obtenerNombre());
        assertEquals(4750, simple.obtenerPrecio());
        assertEquals(115, simple.getDano());
        assertEquals(2, simple.getZoom());
        assertEquals(10, simple.getMunicionMax());
        assertEquals(10, simple.getMunicionActual());

        Francotirador completo = new Francotirador("SSG 08", 1700f, 88, 3.6f, 10, 4);
        assertEquals("SSG 08", completo.obtenerNombre());
        assertEquals(1700, completo.obtenerPrecio());
        assertEquals(3.6f, completo.getPeso());
        assertEquals(4, completo.getZoom());
    }

    @Test
    void testConstructoresPistola() {
        Pistola simple = new Pistola("Desert Eagle", 700f, 53);
        assertEquals("Desert Eagle", simple.obtenerNombre());
        assertEquals(700, simple.obtenerPrecio());
        assertEquals(12, simple.getCargador());

        Pistola completa = new Pistola("USP-S", 200f, 35, 1.2f, 12, "Silenciado", 12);
        assertEquals("USP-S", completa.obtenerNombre());
        assertEquals("Silenciado", completa.getModoDisparo());
    }

    @Test
    void testSobreescrituraPolimorfica() {
        Arma arma1 = new Pistola("P250", 300f, 38);
        Arma arma2 = new Francotirador("SCAR-20", 5000f, 80, 2);
        Arma arma3 = new Fusil("AK-47", 2700f, 36);

        assertTrue(arma1.describirComportamiento().contains("Pistola"));
        assertTrue(arma2.describirComportamiento().contains("Francotirador"));
        assertTrue(arma3.describirComportamiento().contains("Fusil"));
    }

    @Test
    void testInterfazUICapableFamiliasDistintasYDefaultMethod() {
        UICapable elementoArma = new Francotirador("AWP", 4750f, 115, 2);
        UICapable elementoJugador = new Jugador("s1mple", "Antiterrorista", 100, 16000);

        assertTrue(elementoArma instanceof Arma);
        assertFalse(elementoJugador instanceof Arma);

        String textoArma = elementoArma.renderizarUI();
        String textoJugador = elementoJugador.renderizarUI();

        assertTrue(textoArma.contains("HUD Francotirador [AWP]"));
        assertTrue(textoArma.contains("Zoom óptico: x2"));
        assertTrue(textoJugador.contains("Renderizado HUD genérico CS2"));
        assertTrue(textoJugador.contains("dot_ct.png"));
        assertTrue(textoJugador.contains("HUD_RADAR_TOP_LEFT"));
    }

    @Test
    void testEntidadesSoloEstadoYOcultamiento() throws Exception {
        assertTrue(Modifier.isAbstract(Arma.class.getModifiers()));
        assertTrue(Modifier.isProtected(Arma.class.getDeclaredConstructor().getModifiers()));
        assertThrows(NoSuchMethodException.class, () -> Arma.class.getMethod("disparar"));
        assertThrows(NoSuchMethodException.class, () -> Jugador.class.getMethod("recibirDanio", int.class));

        for (Class<?> tipo : new Class<?>[]{Arma.class, Jugador.class, Francotirador.class, Pistola.class}) {
            for (Field campo : tipo.getDeclaredFields()) {
                if (!Modifier.isStatic(campo.getModifiers())) {
                    assertTrue(Modifier.isPrivate(campo.getModifiers()),
                            tipo.getSimpleName() + "." + campo.getName() + " debe ser private");
                }
            }
        }
    }
}
