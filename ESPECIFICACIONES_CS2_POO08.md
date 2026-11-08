# Especificaciones Técnicas — Ejercicio POO-08
## CRUD REST, Persistencia H2, Jerarquía de Excepciones y Simulación CS2

**Asignatura:** Lenguaje de Programación 3 (CYT646) — Facultad de Ciencias y Tecnología (UCA)  
**Slug del Ejercicio:** `ejercicio-poo-08-crud-h2-insomnia-2026-10-28`  
**Alumno:** César Ramírez Román (`cesarraa`)  
**Repositorio:** `crr-taller-git-2026`  
**Dominio:** Counter-Strike 2 (CS2)  
**Enlace al Commit de la Solución:** https://github.com/cesarraa/crr-taller-git-2026/commit/3e361a4c9d3f3d5ebb1ba7ad69a7e85854c2d0d7  

---

## 1. Objetivo del Trabajo

Publicar un microservicio HTTP con Spring Boot sobre el modelo de **Counter-Strike 2 (CS2)** que:
1. Persiste el estado de las entidades en una base de datos relacional en memoria **H2** mediante **Spring Data JPA**.
2. Modela y persiste tres clases de una misma familia: la clase base `Arma` y sus dos clases hijas directas `Francotirador` y `Pistola` (más la entidad `Jugador` de otra familia).
3. Expone un **CRUD REST completo** sobre la familia `Arma`, soportando **discriminación polimórfica en JSON con Jackson** (`tipo`).
4. Implementa **dos reglas de negocio de juego** que alimentan una **simulación balística persistida** (`/api/simulacion/combate`), cuyos efectos modifican de forma duradera la munición de las armas y la salud del jugador en H2.
5. Desarrolla una **jerarquía de excepciones del dominio** (una clase base `DominioCS2Exception` y dos hijas: `MunicionAgotadaException` y `EconomiaInvalidaException`) con un **handler HTTP dedicado por tipo** en `@RestControllerAdvice` para que el cliente distinga el tipo de error tanto por el código HTTP (409 vs 422) como por el JSON.
6. Mantiene y expone el protocolo de dominio con la interfaz `UICapable` de POO-07 consumida polimórficamente desde `/ui/elementos`.
7. Versiona una colección completa de **Insomnia** en `insomnia/` y `docs/insomnia/`.

---

## 2. Arquitectura en 3 Capas

```text
┌────────────────────────────────────────────────────────────────────────┐
│                      1. Capa de Presentación / REST                    │
│  - ArmaCrudController: CRUD y acciones en /api/armas                   │
│  - SimulacionController: Simulación balística en /api/simulacion       │
│  - JugadorController: Consulta de combatientes en /api/jugadores       │
│  - UIController: Consumo polimórfico de interfaz en /ui               │
│  - IndexController: Metadatos y catálogo de endpoints en GET /        │
│  - GlobalExceptionHandler: Handlers HTTP por tipo de excepción         │
└───────────────────────────────────┬────────────────────────────────────┘
                                    │ DTOs / Entidades HTTP
                                    ▼
┌────────────────────────────────────────────────────────────────────────┐
│                   2. Capa de Lógica de Negocio (Service)               │
│  - ArmaService (@Service, @Transactional): Validaciones e invariantes  │
│  - SimulacionService (@Service): Orquesta Regla 1 y Regla 2            │
│  - Jerarquía de Excepciones del Dominio (DominioCS2Exception y ramas)  │
└───────────────────────────────────┬────────────────────────────────────┘
                                    │ Operaciones JPA / Entidades
                                    ▼
┌────────────────────────────────────────────────────────────────────────┐
│                    3. Capa de Persistencia (Repository)                │
│  - ArmaRepository (extends JpaRepository<Arma, Long>)                  │
│  - JugadorRepository (extends JpaRepository<Jugador, Long>)            │
│  - Mapeo JPA: Single Table (@Entity, @DiscriminatorColumn)             │
│  - Base de Datos H2 en Memoria: jdbc:h2:mem:cs2db (/h2-console)        │
└────────────────────────────────────────────────────────────────────────┘
```

---

## 3. Entidades Persistidas y Polimorfismo

1. **`Arma` (Clase Base):** Entidad JPA con `@Inheritance(strategy = InheritanceType.SINGLE_TABLE)` y discriminador `tipo_arma`. Define atributos generales (`id`, `nombre`, `precio`, `dano`, `peso`, `municionMax`, `municionActual`) e implementa la discriminación de Jackson con `@JsonTypeInfo(property = "tipo")` y `@JsonSubTypes`.
2. **`Francotirador` (Clase Hija 1):** Especialización que agrega el atributo específico `zoom` (magnificación óptica), sobreescribe `describirComportamiento()`, implementa su propia fórmula de daño balístico `calcularDanioEfectivo(...)` y reemplaza el método default de la interfaz `UICapable`.
3. **`Pistola` (Clase Hija 2):** Especialización de arma secundaria con atributos `cargador` y `modoDisparo`, sobreescribe `describirComportamiento()` y aplica atenuación por distancia en `calcularDanioEfectivo(...)`.
4. **`Jugador` (Familia Independiente):** Entidad persistida en la tabla `jugadores` con atributos `id`, `alias`, `bando`, `salud` (0 a 100) y `dinero`. Implementa la interfaz `UICapable` aprovechando la implementación por defecto de `renderizarUI()`.

---

## 4. Jerarquía de Excepciones del Dominio y Handlers HTTP

La jerarquía modela formalmente situaciones del juego que impiden continuar el flujo de ejecución normal:

```text
                  RuntimeException (Java)
                            ▲
                            │
                  DominioCS2Exception (Base del Dominio)
                   (Handler fallback: HTTP 400 Bad Request)
                            ▲
             ┌──────────────┴──────────────┐
             │                             │
MunicionAgotadaException       EconomiaInvalidaException
(Handler: HTTP 409 Conflict)   (Handler: HTTP 422 Unprocessable)
```

- **Clase Base `DominioCS2Exception`:** Excepción no comprobada (*unchecked*). Permite que Spring ejecute rollback transaccional automático sin ensuciar la firma de los métodos del dominio con `throws`.
- **Hija 1 `MunicionAgotadaException`:** Lanzada cuando un arma intenta disparar pero su cargador está agotado (`municionActual <= 0`). Su handler retorna **HTTP 409 Conflict** indicando que el estado actual del recurso físico en el juego entra en conflicto con la acción.
- **Hija 2 `EconomiaInvalidaException`:** Lanzada cuando se intenta crear o adquirir un arma cuyo precio excede el tope reglamentario de $16,000 en CS2 o saldo insuficiente. Su handler retorna **HTTP 422 Unprocessable Entity**.
- **`RecursoNoEncontradoException`:** Lanzada cuando se busca un recurso inexistente por ID. Su handler retorna **HTTP 404 Not Found**.
- **`IllegalArgumentException`:** Lanzada ante violaciones de invariantes de construcción (ej: daño <= 0, peso <= 0). Su handler retorna **HTTP 400 Bad Request**.

---

## 5. Las Dos Reglas de Negocio de la Simulación

- **Regla 1 (Balística y Consumo de Munición del Arma):**
  > *Al disparar, el arma consume munición de su cargador y calcula el daño efectivo con atenuación por distancia y multiplicadores según el tipo de arma (`Francotirador` letal a larga distancia con zoom; `Pistola` de apoyo a corta distancia); si la munición llega a cero, el arma bloquea el disparo y lanza `MunicionAgotadaException`.*

- **Regla 2 (Impacto y Salud Vital del Combatiente):**
  > *Cuando un `Jugador` recibe el impacto del proyectil disparado por un arma, su nivel de salud se reduce en la cantidad exacta del daño recibido hasta un piso inquebrantable de cero (`salud = Math.max(0, salud - danio)`), cambiando su estado vital a eliminado (`estaVivo() == false`) si la salud llega a cero.*

---

## 6. Catálogo de Endpoints REST

| Verbo | URL / Recurso | Descripción | Código Éxito | Códigos Error |
|---|---|---|---|---|
| `GET` | `/` | Confirma servicio vivo y metadatos | `200 OK` | - |
| `GET` | `/api/armas` | Lista todas las armas persistidas en H2 con discriminación polimórfica | `200 OK` | - |
| `GET` | `/api/armas/{id}` | Obtiene un arma persistida por ID | `200 OK` | `404 Not Found` |
| `POST` | `/api/armas` | Registra nueva arma (instancia automáticamente `Francotirador`, `Pistola` o `Arma`) | `201 Created` | `400 Bad Request` / `422 Unprocessable` |
| `PUT` | `/api/armas/{id}` | Actualización completa de todos los atributos del arma | `200 OK` | `400` / `404` / `422` |
| `PATCH` | `/api/armas/{id}` | Actualización parcial de un subconjunto de campos | `200 OK` | `400` / `404` / `422` |
| `DELETE` | `/api/armas/{id}` | Elimina físicamente el arma de la base de datos | `200 OK` | `404 Not Found` |
| `POST` | `/api/armas/{id}/disparar` | Dispara el arma persistida descontando munición en H2 | `200 OK` | `404` / `409 Conflict` (sin balas) |
| `POST` | `/api/armas/{id}/recargar` | Recarga la munición del arma al 100% en H2 | `200 OK` | `404 Not Found` |
| `POST` | `/api/simulacion/combate` | Ejecuta la simulación balística de las 2 reglas persistiendo en H2 | `200 OK` | `404` / `409 Conflict` |
| `GET` | `/api/simulacion/estado` | Comprueba el estado persistido post-simulación en H2 | `200 OK` | - |
| `GET` | `/api/jugadores` | Lista todos los jugadores persistidos en H2 | `200 OK` | - |
| `GET` | `/api/jugadores/{id}` | Obtiene un jugador por ID | `200 OK` | `404 Not Found` |
| `GET` | `/ui/elementos` | Consumo polimórfico a través de la interfaz `UICapable` | `200 OK` | - |
| `GET` | `/ui/renderizar` | Renderiza HUD individual invocando `renderizarUI()` | `200 OK` | `400 Bad Request` |

---

## 7. Invariantes del Dominio

- **Nombre y Alias:** No pueden ser nulos ni cadenas vacías. No se permiten nombres de armas duplicados en la base de datos.
- **Precio Económico CS2:** Rango reglamentario estricto: `$0.00 <= precio <= $16,000.00`. Precios fuera de este rango son rechazados inmediatamente por el dominio y servicio con `EconomiaInvalidaException`.
- **Daño Balístico:** Debe ser estrictamente positivo (`dano > 0`) y no exceder `500 HP`.
- **Capacidad de Munición:** `municionMax > 0`. La munición actual debe cumplir `0 <= municionActual <= municionMax`.
- **Salud del Jugador:** Rango vital inquebrantable `0 <= salud <= 100`. Al recibir daño, nunca toma valores negativos (`Math.max(0, salud - danio)`).

---

## 8. Cómo Probar la Solución (Guía de Evaluación)

### Paso 1: Arrancar la Aplicación
```bash
./mvnw spring-boot:run
```
El servicio iniciará en el puerto 8080 y la consola H2 estará accesible en `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:cs2db`, usuario: `sa`, clave vacía).

### Paso 2: Importar la Colección de Insomnia
1. Abrir Insomnia.
2. Hacer clic en **Import** -> **From File**.
3. Seleccionar el archivo `insomnia/insomnia_collection_lp3_poo08.json` o `docs/insomnia/insomnia_collection_lp3_poo08.json`.
4. El entorno `Base Environment` viene preconfigurado con `base_url = http://localhost:8080`.

### Paso 3: Recorrido de Pruebas en Insomnia
1. **01 - GET / (Servicio Vivo):** Retorna status 200 con metadatos y catálogo de endpoints.
2. **02 - POST /api/armas (Crear Francotirador):** Envía JSON con `"tipo": "FRANCOTIRADOR"` y `"zoom": 2`. Retorna 201 Created con el ID asignado por H2.
3. **03 - POST /api/armas (Crear Pistola):** Envía JSON con `"tipo": "PISTOLA"` y `"cargador": 12`. Retorna 201 Created.
4. **04 - GET /api/armas (Listar Colección):** Comprueba que aparecen las armas precargadas y las nuevas creadas con sus subtipos discriminados.
5. **05 - GET /api/armas/1 (Buscar por ID):** Retorna el arma AK-47.
6. **06 - PUT /api/armas/1 (Actualización Completa):** Modifica el arma 1 a "AK-47 Vulcan" y retorna 200 OK.
7. **07 - PATCH /api/armas/1 (Actualización Parcial):** Modifica sólo precio y daño; un GET posterior confirma los cambios.
8. **08 - DELETE /api/armas/2:** Elimina el arma 2; una consulta posterior a `/api/armas/2` retorna 404.
9. **09 - POST Ilegal (Invariante Roto):** Envía daño negativo; responde 400 Bad Request y no ensucia la BD.
10. **10 - GET ID Inexistente:** Consulta `/api/armas/999` y responde 404 Not Found.
11. **11 - Excepción Hija 1 (MunicionAgotadaException):** Dispara un arma hasta dejarla en 0 balas; el siguiente disparo retorna HTTP 409 Conflict.
12. **12 - Excepción Hija 2 (EconomiaInvalidaException):** Intenta registrar un arma con precio $25,000; responde HTTP 422 Unprocessable Entity.
13. **13 - GET /ui/elementos:** Demuestra el consumo polimórfico de clases en familias distintas (`Francotirador` y `Jugador`) a través de la interfaz `UICapable`.
14. **15 - POST /api/simulacion/combate:** Ejecuta la simulación de combate aplicando Regla 1 y Regla 2.
15. **16 - GET /api/simulacion/estado:** Comprueba que la munición y la salud han quedado persistidas de verdad en la base de datos H2.

### Paso 4: Ejecutar Pruebas Automatizadas
```bash
./mvnw clean test
```
Ejecuta 20 pruebas automatizadas (12 de persistencia H2, CRUD, excepciones y simulación + 7 de POO + 1 de Spring Boot), todas aprobadas al 100% con 0 errores y 0 fallos.
