# Bitácora de Asistencia de Inteligencia Artificial (IA)

**Asignatura:** Lenguajes de Programación 3 (CYT646)  
**Ejercicio:** `ejercicio-poo-08-crud-h2-insomnia-2026-10-28`  
**Alumno:** César Ramírez Román (`cesarraa`)  
**Repositorio:** `crr-taller-git-2026`  

---

## 1. Asistente y Modelo de Lenguaje Utilizado

- **Marca del asistente o agente de IA:** Google Antigravity / Gemini CLI
- **Modelo exacto del LLM (*Large Language Model*):** `Gemini 3.8 Flash`

---

## 2. Sesión 1: Modelado POO Inicial y Revisión de Paquetes (POO-06)

1. **Planteamiento de la consigna y análisis de requerimientos:**
   - *Prompt inicial:* Se solicitó estructurar el modelado orientado a objetos para Counter-Strike 2 (CS2) siguiendo el estándar oficial del template (`py.edu.uc.lp3.domain` y `py.edu.uc.lp3.rest.controller`), incorporando constructores simples y sobrecargados encadenados con `super(...)`, sobrecarga de mensajes del dominio (`disparar`), sobreescritura de métodos polimórficos (`describirComportamiento`) y servicios REST en Spring Boot.
2. **Encapsulamiento e Invariantes:**
   - Se protegieron atributos con visibilidad `protected` y `private`, restringiendo la mutación del estado a las acciones de dominio y validando invariantes con `IllegalArgumentException`.

---

## 3. Sesión 2: Interfaces y Métodos por Defecto (`default` methods) - POO-07

1. **Consigna:**
   - Modelar una capacidad compartida entre dos clases de familias distintas mediante una interfaz de dominio (`UICapable`).
2. **Solución Implementada:**
   - Interfaz `UICapable` con `renderizarUI()` como `default method`.
   - **Familia Arma:** `Francotirador` implementa `UICapable` y **reemplaza el default** (`@Override`) con HUD óptico.
   - **Familia Jugador:** `Jugador` implementa `UICapable` y **aprovecha la implementación default** de `renderizarUI()`.
   - Exposición en `UIController` (`/ui/elementos` y `/ui/renderizar`) manipulando las instancias polimórficamente a través del tipo de la interfaz.

---

## 4. Sesión 3: CRUD REST, H2, Jerarquía de Excepciones, Reglas de Juego y Colección Insomnia (POO-08)

1. **Planteamiento de la consigna POO-08:**
   - Publicar un servicio HTTP con Spring Boot sobre CS2 que persista su estado en **H2**.
   - Persistir **tres clases de una familia** (`Arma`, `Francotirador`, `Pistola`) más la clase independiente `Jugador`.
   - CRUD REST completo con **discriminación polimórfica en JSON con Jackson** (`tipo`).
   - Implementar **dos reglas de negocio del juego** que alimenten una **simulación persistida** de combate (`/api/simulacion/combate`).
   - Crear una **jerarquía de excepciones del dominio** (una clase base y dos hijas) con un **handler HTTP dedicado por tipo**.
   - Generar y versionar una colección completa de **Insomnia** en `insomnia/` y `docs/insomnia/`.

2. **Diseño de la Jerarquía de Excepciones del Dominio:**
   - *Prompt:* Definir la familia de excepciones del juego, la elección entre comprobadas o no comprobadas, y su vinculación con `@RestControllerAdvice`.
   - *Solución:*
     - Se creó la clase base `DominioCS2Exception` extendiendo `RuntimeException` (no comprobada / *unchecked*), asegurando que Spring ejecute rollback transaccional automático sin contaminar firmas de métodos.
     - **Hija 1 (`MunicionAgotadaException`):** Lanzada por el dominio cuando se intenta disparar un arma sin cartuchos. Su handler responde **HTTP 409 Conflict** indicando que el estado físico del recurso colisiona con la acción.
     - **Hija 2 (`EconomiaInvalidaException`):** Lanzada cuando el precio viola los topes reglamentarios de CS2 ($0 a $16,000). Su handler responde **HTTP 422 Unprocessable Entity**.
     - Handler para `RecursoNoEncontradoException` (404 Not Found) y `IllegalArgumentException` (400 Bad Request por invariantes de construcción).

3. **Simulación Persistida con las Dos Reglas de Negocio:**
   - *Prompt:* Diseñar la simulación persistida articulando las 3 clases del dominio con 2 reglas del juego.
   - *Solución:*
     - **Regla 1 (Balística y Consumo de Munición):** En `Arma`, `Francotirador` y `Pistola`, el disparo consume munición del cargador y calcula el daño efectivo con atenuación y zoom; lanza `MunicionAgotadaException` si no hay munición.
     - **Regla 2 (Impacto y Salud Vital):** En `Jugador`, el combatiente recibe el daño acumulado hasta un piso de cero (`salud = Math.max(0, salud - danio)`), determinando si continúa con vida (`estaVivo()`).
     - Orquestado en `SimulacionService` y expuesto en `POST /api/simulacion/combate` y `GET /api/simulacion/estado`, persistiendo el estado en H2 mediante `ArmaRepository` y `JugadorRepository`.

4. **Discriminación Polimórfica Jackson y CRUD:**
   - Se configuraron anotaciones `@JsonTypeInfo` y `@JsonSubTypes` en `Arma.java`. Al hacer `POST /api/armas` con `"tipo": "FRANCOTIRADOR"`, Jackson instancia la clase hija correspondiente con sus atributos específicos.
   - Se añadieron operaciones `PUT` (reemplazo total) y `PATCH` (actualización parcial).

5. **Colección de Insomnia Versionada:**
   - Se construyó y exportó la colección en formato v4 con 16 solicitudes agrupadas en carpetas lógicas: Arranque, CRUD, Errores/Excepciones (incluyendo un pedido por cada hija), Protocolo de Interfaz y Simulación Persistida.

6. **Verificación Automatizada:**
   - Se ampliaron las pruebas de integración en `ArmaCrudIntegrationTests`.
   - Ejecución de `mvn clean test`: **20 de 20 pruebas aprobadas exitosamente al 100%** (0 errores, 0 fallos).
