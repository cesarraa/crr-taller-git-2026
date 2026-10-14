# Bitácora de Asistencia de Inteligencia Artificial (IA)

**Asignatura:** Lenguajes de Programación 3 (CYT646)  
**Ejercicio:** `ejercicio-poo-06-revision-paquetes-constructores-2026-09-30`  
**Alumno:** César Ramírez Román (`cesarraa`)  
**Repositorio:** `crr-taller-git-2026`  

---

## 1. Asistente y Modelo de Lenguaje Utilizado

- **Marca del asistente o agente de IA:** Google Antigravity / Gemini CLI
- **Modelo exacto del LLM (*Large Language Model*):** `Gemini 3.8 Flash`

---

## 2. Resumen de Prompts y Diálogo de Trabajo

A continuación se resume la interacción técnica y el flujo de trabajo desarrollado con el asistente de IA para resolver la práctica:

1. **Planteamiento de la consigna y análisis de requerimientos:**
   - *Prompt / Solicitud inicial:* Se proporcionó el enunciado completo del ejercicio POO-06, solicitando implementar la revisión del modelado orientado a objetos para Counter-Strike 2 (CS2), migrar la estructura de paquetes al estándar oficial del template de la cátedra (`py.edu.uc.lp3.domain` y `py.edu.uc.lp3.rest.controller`), e incorporar constructores simples y sobrecargados, sobrecarga de mensajes del dominio, sobreescritura de métodos abstractos y servicios REST en Spring Boot.
   - *Contexto de ejecución:* Se indicó que el desarrollo y las pruebas operativas finales se ejecutan sobre la máquina virtual Lubuntu (`LP3`).

2. **Diseño del modelo de dominio y encapsulamiento:**
   - *Prompt:* Se solicitó estructurar la clase base abstracta `Arma` y sus especializaciones (`Francotirador`, `Pistola`, `Escopeta`, `Subfusil`, `Granada`), asegurando el cumplimiento de la pregunta de anclaje de la cátedra: evitar que capas externas puedan dejar los objetos en estados ilegales o imposibles mediante manipulación indebida de vida o munición.
   - *Solución:* Se encapsularon los atributos (`protected`/`private`), se eliminó cualquier setter público permisivo para la munición, y se concentró la mutación del estado exclusivamente en las acciones del dominio (`disparar` y `recargar`), validando rigurosamente invariantes con `IllegalArgumentException` en constructores simples y sobrecargados.

3. **Sobrecarga y Sobreescritura:**
   - *Prompt:* Implementar constructores sobrecargados que invoquen a `super(...)` asegurando estados legales, sobrecargar el mensaje de dominio `disparar` (versión estándar sin parámetros, versión con distancia y versión con impacto crítico), y sobreescribir el método abstracto `describirComportamiento()` en las clases hijas concretas.
   - *Solución:* Se programaron las tres firmas de `disparar`, constructores simples y completos encadenados, y las implementaciones polimórficas de `describirComportamiento()` en cada subclase.

4. **Controladores REST y Verificación:**
   - *Prompt:* Adaptar los controladores REST en `py.edu.uc.lp3.rest.controller` (`IndexController` en `GET /` y `ArmaController` en `/armas/construir`, `/armas/comportamientos` y `/armas/disparar`) para exponer los mecanismos POO y responder en JSON.
   - *Solución:* Se implementaron endpoints que prueban la construcción simple y sobrecargada desde la URL con manejo de excepciones (400 Bad Request), el listado polimórfico de comportamientos en colección homogénea `List<Arma>`, y la ejecución de la sobrecarga de disparo. Se añadieron pruebas unitarias automatizadas (`DominioPoOTests`) que pasan al 100% con `./mvnw test`.

5. **Documentación y Entrega Inicial (POO-06):**
   - *Prompt:* Elaborar el `README.md` actualizado con diagrama Mermaid completo y explicación teórica de sobrecarga vs sobreescritura, la bitácora obligatoria `BITACORA.md` y el documento de especificaciones para Classroom.

---

## 3. Sesión 2: Taller de Interfaces y Métodos por Defecto (`default` methods) - 14 de Octubre de 2026

1. **Planteamiento de la consigna del día (Ale Feltes):**
   - *Consigna:* Elegir una capacidad que compartan dos clases del modelo que pertenezcan a familias distintas. Declararla como interfaz en `py.edu.uc.lp3.domain`, con un mensaje que devuelva texto. Cada clase la implementa a su modo; una de las dos puede reemplazar un `default`. Un servicio REST pide ese mensaje a través del tipo de la interfaz y responde JSON. Ejemplo de pizarra: `UICapable { imagen; posicion; sonido; velocidad; }`.

2. **Diseño de la Interfaz y Familias Distintas:**
   - *Prompt / Diálogo:* Definir cómo modelar la capacidad transversal sin romper la jerarquía existente de `Arma`.
   - *Solución:*
     - Se creó la interface `UICapable` en `py.edu.uc.lp3.domain` con el mensaje `renderizarUI()`, implementado como `default method` (Java 8+) que compone `imagen`, `posicion`, `sonido` y `velocidad`.
     - **Familia 1 (Arma):** La clase `Francotirador` implementa `UICapable` y **reemplaza el default** (`@Override`), generando un renderizado especializado de mira telescópica, zoom óptico, coordenadas del HUD inferior derecho y munición activa.
     - **Familia 2 (Jugador / Entidad):** Se creó la clase `Jugador` en `py.edu.uc.lp3.domain`, completamente independiente de `Arma` (otra familia). Implementa `UICapable` **aprovechando la implementación default** de `renderizarUI()`, personalizando el icono de radar y posición HUD.

3. **Servicio REST Polimórfico a través de la Interfaz:**
   - *Solución:* Se programó `UIController` en `py.edu.uc.lp3.rest.controller` con los endpoints `/ui/elementos` y `/ui/renderizar`. Las instancias se declaran y manipulan estrictamente a través del tipo de la interfaz (`UICapable elemento`), invocando `elemento.renderizarUI()` y respondiendo en JSON.

4. **Verificación y Pruebas Automatizadas:**
   - Se añadieron tests unitarios en `DominioPoOTests` (`testInterfazUICapableFamiliasDistintasYDefaultMethod` y `testInvariantesJugador`), alcanzando 8 tests aprobados al 100% con `./mvnw test`. Se verificaron las respuestas JSON en ejecución real con Spring Boot.

