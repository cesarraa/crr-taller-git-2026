# Bitácora de Asistencia de Inteligencia Artificial (IA)

**Asignatura:** Lenguajes de Programación 3 (CYT646)  
**Ejercicio:** `ejercicio-poo-08-crud-h2-insomnia-2026-10-28`  
**Alumno:** César Ramírez Román (`cesarraa`)  
**Repositorio:** `crr-taller-git-2026`  

---

## 1. Asistente y Modelo de Lenguaje Utilizado

- **Marca del asistente o agente de IA (entrega original):** Google Antigravity / Gemini CLI
- **Modelo exacto del LLM (entrega original):** `Gemini 3.8 Flash`
- **Marca del asistente o agente de IA (corrección POO-08):** opencode CLI
- **Modelo exacto del LLM (corrección POO-08):** `opencode/big-pickle`

---

## 2. Sesión 1: Modelado POO Inicial y Revisión de Paquetes (POO-06)

1. **Planteamiento de la consigna y análisis de requerimientos:**
   - *Prompt inicial:* Se solicitó estructurar el modelado orientado a objetos para Counter-Strike 2 (CS2) siguiendo el estándar oficial del template (`py.edu.uc.lp3.domain` y `py.edu.uc.lp3.rest.controller`), incorporando constructores simples y sobrecargados encadenados con `super(...)`, sobrecarga de mensajes del dominio (`disparar`), sobreescritura de métodos polimórficos (`describirComportamiento`) y servicios REST en Spring Boot.
2. **Encapsulamiento e Invariantes:**
   - Inicialmente se protegieron atributos con visibilidad `protected` y `private`, restringiendo la mutación del estado a las acciones de dominio y validando invariantes con `IllegalArgumentException`. Este enfoque fue corregido en la Sesión 4.

---

## 3. Sesión 2: Interfaces y Métodos por Defecto (`default` methods) - POO-07

1. **Consigna:**
   - Modelar una capacidad compartida entre dos clases de familias distintas mediante una interfaz de dominio (`UICapable`).
2. **Solución Implementada:**
   - Interfaz `UICapable` con `renderizarUI()` como `default method`.
   - **Familia Arma:** `Francotirador` implementa `UICapable` y reemplaza el default (`@Override`) con HUD óptico.
   - **Familia Jugador:** `Jugador` implementa `UICapable` y aprovecha la implementación por defecto de `renderizarUI()`.
   - Exposición en `UIController` (`/ui/elementos` y `/ui/renderizar`) manipulando las instancias polimórficamente a través del tipo de la interfaz. En la corrección se ajustó para que esas instancias se lean de H2 en lugar de construirse dentro del controller.

---

## 4. Sesión 3: CRUD REST, H2, Jerarquía de Excepciones, Reglas de Juego y Colección Insomnia (POO-08, entrega original)

1. **Planteamiento de la consigna POO-08:**
   - Publicar un servicio HTTP con Spring Boot sobre CS2 que persista su estado en **H2**.
   - Persistir tres clases de una familia (`Arma`, `Francotirador`, `Pistola`) más la clase independiente `Jugador`.
   - CRUD REST completo con discriminación polimórfica en JSON con Jackson (`tipo`).
   - Implementar dos reglas de negocio del juego que alimenten una simulación persistida de combate (`/api/simulacion/combate`).
   - Crear una jerarquía de excepciones del dominio con un handler HTTP dedicado por tipo.
   - Generar y versionar una colección de Insomnia en `insomnia/` y `docs/insomnia/`.

2. **Diseño de la Jerarquía de Excepciones del Dominio:**
   - Se creó la clase base `DominioCS2Exception` extendiendo `RuntimeException`, con `MunicionAgotadaException` (409) y `EconomiaInvalidaException` (422) como hijas, además de handlers para 404 y 400.

3. **Simulación Persistida con las Dos Reglas de Negocio:**
   - La entrega original ubicó parte del cálculo y de las excepciones dentro de las entidades. Esa decisión fue observada por el docente y corregida en la Sesión 4.

4. **Discriminación Polimórfica Jackson y CRUD:**
   - Se configuraron `@JsonTypeInfo` y `@JsonSubTypes` en `Arma.java` y se añadieron `PUT` y `PATCH`. En la corrección se evitó el `tipo` duplicado y se movió la construcción a DTOs y services.

5. **Colección de Insomnia Versionada:**
   - Se construyó y exportó la colección en formato v4 con 16 solicitudes. Después de la corrección quedó en 17.

6. **Verificación Automatizada:**
   - Ejecución de `mvn clean test`: **20 de 20 pruebas aprobadas** en la entrega original.

---

## 5. Sesión 4: Corrección de POO-08 Según la Evaluación del Docente

1. **Consigna y materiales revisados:**
   - Se solicitó analizar el PDF `Corrección POO-08 - César Ramírez.pdf`, contrastarlo con los enunciados y la rúbrica oficiales, proponer un plan y ejecutar los cambios sin crear un documento markdown nuevo.
   - Se revisaron los archivos públicos `ejercicio-poo-08-crud-h2-insomnia-2026-10-28.md`, `RUBRICA-ejercicios-lp3-2026.md` y `ejercicio-poo-09-apis-cruzadas-2026-11-16.md` del repositorio de la cátedra.
   - Se comprobó que el repositorio local estaba detrás de `origin/main` (`f7a060bc07a0f5ea03bd346a374068f328ffc81e`), se hizo `git fetch origin` y se trabajó en la rama `correccion-poo8`.

2. **Observaciones cerradas:**
   - **Tres capas reales:** se eliminó el uso directo de `JugadorRepository` desde `JugadorController` y se creó `JugadorService`. Ningún controller inyecta repositorios.
   - **Lógica fuera del dominio:** `Arma`, sus hijas y `Jugador` quedaron como entidades de estado con campos `private`, accessors simples y constructores JPA `protected`. Las validaciones, reglas y excepciones viven en `ArmaService`, `JugadorService` y `SimulacionService`.
   - **Validación unificada:** todo precio fuera de `$0..$16,000` lanza `EconomiaInvalidaException` en POST, PUT y PATCH (422), con el mismo código `CS2_ECONOMIA_EXCEDIDA`.
   - **Herencia:** `Arma` volvió a ser `abstract`; se agregó `Fusil` para AK-47 y M4A4; el constructor simple de `Francotirador` delega en el completo; `Granada()` usa un daño inicial válido.
   - **JSON:** `getTipo()` lleva `@JsonIgnore`, por lo que aparece una sola clave `tipo`, generada por el discriminador de Jackson a partir del DTO.
   - **UI persistida:** `UIController` lee jugador y arma desde los services y H2, en lugar de crear instancias temporales dentro del método.
   - **Insomnia:** se agregó el pedido `10b - PATCH /api/armas/1` que deja `municionActual: 0`; el pedido `11` produce el 409 con un solo disparo; se documentó el pedido `14 - GET /ui/renderizar`.
   - **Documentación:** se actualizaron `README.md`, `ESPECIFICACIONES_CS2_POO08.md` y esta bitácora con las respuestas que faltaban ("recurso y verbo", "identidad en JSON", justificación de `SINGLE_TABLE`, relación entre interfaz y repositorio) y el recorrido completo de 17 pedidos.

3. **Verificación:**
   - `./mvnw -q compile`: correcto.
   - `./mvnw test`: **21 de 21 pruebas aprobadas** (0 errores, 0 fallos), incluyendo nuevos tests de ocultamiento, herencia, DTOs, services, handlers de excepción y UI leyendo estado persistido.

4. **Entrega:**
   - Se realizó un commit de corrección con mensaje que identifica el cambio y un segundo commit que actualiza el enlace al hash de la solución corregida en la documentación.
