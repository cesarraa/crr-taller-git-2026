# Bitácora de Asistencia de Inteligencia Artificial (IA)

**Asignatura:** Lenguajes de Programación 3 (CYT646)  
**Ejercicios:** `ejercicio-poo-08-crud-h2-insomnia-2026-10-28` y `ejercicio-poo-09-apis-cruzadas-2026-11-16`  
**Alumno:** César Ramírez Román (`cesarraa`)  
**Repositorio:** `crr-taller-git-2026`  

---

## 1. Asistente y Modelo de Lenguaje Utilizado

- **Marca del asistente o agente de IA (entrega original):** Google Antigravity / Gemini CLI
- **Modelo exacto del LLM (entrega original):** `Gemini 3.8 Flash`
- **Marca del asistente o agente de IA (corrección POO-08):** opencode CLI
- **Modelo exacto del LLM (corrección POO-08):** `opencode/big-pickle`
- **Marca del asistente o agente de IA (POO-09):** opencode CLI
- **Modelo exacto del LLM (POO-09):** `opencode/big-pickle`

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

---

## 6. Sesión 5: POO-09, parte local de APIs cruzadas

1. **Consigna y materiales revisados:**
   - Se pidió retomar el plan del challenge POO-09 desde el estado local de la rama `poo09-apis-cruzadas`, dejando listo todo lo que no depende de los compañeros.
   - Se leyeron los documentos públicos `ejercicio-poo-09-apis-cruzadas-2026-11-16.md`, `ejercicio-poo-09-grupos-2026-11-16.md` y `ejercicio-poo-09-ejemplo-uno-2026-11-16.md`, además de la rúbrica general.
   - Se verificó el estado real del repo: commit `e05f0ab` con clientes HTTP, ocho archivos sin commitear y 19 de 24 tests fallando por falta del bean `RestClient.Builder`.

2. **Resumen de prompts utilizados en la sesión:**
   - Verificar la última acción de la conversación anterior y contrastarla con el estado del repositorio.
   - Volver a planificar la ejecución completa de POO-09, incluida la parte colaborativa, y compararla con el estado local.
   - Aclarar el orden de los merges: primero `correccion-poo8` en `main` y después POO-09, revisando qué trabajo vive en cada rama.
   - Continuar solo con las partes independientes de los compañeros: arranque, escenario persistido, OpenAPI propio, documentación y colección base de Insomnia.

3. **Cambios realizados:**
   - `ApiClientConfig` construye cada `RestClient` con `RestClient.builder()` y timeouts configurables; `application.properties` expone ocho variables nuevas para connect/read timeout.
   - `pom.xml` incorpora `spring-boot-starter-json` porque Spring Boot 4.1.1 separó Jackson del starter `webmvc`; el test usa `tools.jackson.databind.ObjectMapper` (Jackson 3).
   - `EscenarioService` devuelve 404 con `RecursoNoEncontradoException` para IDs inexistentes; `EscenarioController` documenta ese código y el cuerpo del error 422 con un ejemplo.
   - `EscenarioValidado` describe cada campo en el OpenAPI; `OpenApiConfig` explica las dos reglas propias y el rechazo ajeno atribuido.
   - `EscenarioCruzadoTests` pasó de 3 a 8 pruebas: rechazos de A2 y A3 por HTTP, API caída con `statusExterno = 0`, payloads enviados, lectura de lo persistido, validación local sin consultar clientes y 404.
   - README y esta bitácora quedan al día con la parte local; la parte grupal sigue marcada como pendiente.

4. **Verificación:**
   - `./mvnw clean test`: 29 de 29 pruebas aprobadas.
   - Servicio arrancado con `./mvnw spring-boot:run`: `/v3/api-docs` responde con 16 rutas, incluidas `/api/escenarios` y `/api/escenarios/{id}`, los esquemas `EscenarioRequest` y `EscenarioValidado` y el ejemplo del error 422.

---

## 7. Sesión 6: integración de A3 (Luis) y cierre de contratos de grupo

1. **Consigna y materiales revisados:**
   - Se recibió el mensaje del referente del grupo con la asignación de puertos: Luis 8080, Giovanni 8081 y César 8082, además de la versión de springdoc que él usaba (2.8.6).
   - Se verificó que ese número de springdoc corresponde a Spring Boot 3.x y se mantuvo 3.2.0, ya probada con Boot 4.1.1 en este repo.
   - Se leyó el repo público de Luis (`https://github.com/LTP22/ltauber-tp-lp3-2026`) y su mensaje con el contrato de A3.

2. **Resumen de prompts utilizados en la sesión:**
   - Verificar hasta dónde había llegado la integración después del primer mensaje del referente.
   - Aplicar los datos de Luis: base URL, OpenAPI, regla en una frase, ruta y respuestas 200/409.
   - Pedir ejemplos reales de la API propia para responderle a Luis.

3. **Cambios realizados:**
   - `application.properties`: `server.port=8082`; A2 apunta a Giovanni en 8081 y A3 a Luis en 8080 con `/simulaciones/combate` e IDs configurables `A3_ARMA_DE_FUEGO_ID` / `A3_GRANADA_ID`.
   - `ClienteExterno` agrega `validarConQuery` y deja que un 200 sin campo `aprobado` cuente como aceptación; `ValidacionExterna` tolera la respuesta real de Luis (`danioInfligido`, `balasRestantes`, `granadaLanzada`).
   - `ClienteA3` traduce el payload interno a los query params `armaDeFuegoId` y `granadaId` y arma un mensaje legible con el resultado de Luis.
   - README, especificaciones y colección de Insomnia quedan con el mapa A1/A2/A3, los tres comandos de arranque, los IDs del H2 de Luis y tres respuestas de ejemplo guardadas.

4. **Verificación:**
   - `./mvnw test`: 31 de 31 pruebas aprobadas, incluidos dos tests nuevos de parseo de la respuesta de Luis.
   - Se arrancó el servicio en 8082 y se capturaron las respuestas reales propias: `POST /api/armas` con precio 4750 devuelve 201 y con precio 25000 devuelve 422 `CS2_ECONOMIA_EXCEDIDA`.
   - La colección `insomnia/insomnia_collection_lp3_poo09.json` sigue siendo JSON válido y ahora tiene 10 requests y 3 responses de ejemplo.

---

## 8. Sesión 7: integración de A2 (Giovanni) y cierre del round robin

1. **Consigna y materiales revisados:**
   - Giovanni confirmó el puerto 8081, sus URLs de OpenAPI y Swagger, y que usa springdoc 3.2.1 con Boot 4.1.1.
   - Avisó que el HEAD público `f7a060b` todavía no incluía springdoc; en el working tree local el `pom.xml` ya tiene 3.2.0, pendiente de publicación.
   - Confirmó el endpoint definitivo `POST /api/simulacion/disparar?rifleId={id}&jugadorId={id}&distancia={valor}`, con IDs sembrados 108/100 y respuestas 200/409 reales.

2. **Resumen de prompts utilizados en la sesión:**
   - Redactar el mensaje para Giovanni con los datos propios y lo que faltaba de su regla.
   - Integrar su respuesta con el mismo patrón de query params usado para Luis.
   - Actualizar documentación, respuestas de ejemplo de Insomnia y bitácora.

3. **Cambios realizados:**
   - `ClienteA2` traduce el payload interno a `rifleId`, `jugadorId` y `distancia`; los defaults se configuran con `A2_RIFLE_ID`, `A2_JUGADOR_ID` y `A2_DISTANCIA`.
   - `ValidacionExternaTest` incorpora el ejemplo real de Giovanni: un 200 sin `aprobado` conserva el `mensaje` y cuenta como aceptación.
   - README y especificaciones dejan el mapa A1/A2/A3 completamente confirmado, con los tres comandos de arranque y el procedimiento para provocar el 409 de cada compañero.
   - La colección de Insomnia reemplaza el ejemplo genérico de rechazo de A2 por `No queda munición en AK-47 VACIO CESAR` y guarda el mensaje real del camino feliz.

4. **Verificación:**
   - `./mvnw test`: 32 de 32 pruebas aprobadas.
   - Giovanni verificó los tres servicios juntos desde su lado: camino feliz 201 con ambas aceptaciones, rechazo de A1 con `codigoHttpCompanero: 422` y rechazo de Luis con `codigoHttpCompanero: 200`, sin jugadas guardadas en los rechazos.
