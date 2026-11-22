# Especificaciones Técnicas — Ejercicio POO-08 (Corregido)
## CRUD REST, Persistencia H2, Jerarquía de Excepciones y Simulación CS2

**Asignatura:** Lenguaje de Programación 3 (CYT646) — Facultad de Ciencias y Tecnología (UCA)  
**Slug del Ejercicio:** `ejercicio-poo-08-crud-h2-insomnia-2026-10-28`  
**Alumno:** César Ramírez Román (`cesarraa`)  
**Repositorio:** `crr-taller-git-2026`  
**Dominio:** Counter-Strike 2 (CS2)  
**Enlace al Commit de la Solución Original:** https://github.com/cesarraa/crr-taller-git-2026/commit/3e361a4c9d3f3d5ebb1ba7ad69a7e85854c2d0d7

---

## 1. Objetivo del Trabajo

Publicar un microservicio HTTP con Spring Boot sobre el modelo de **Counter-Strike 2 (CS2)** que:

1. Persiste el estado de las entidades en una base de datos relacional en memoria **H2** mediante **Spring Data JPA**.
2. Modela y persiste una familia de armas con `Arma` como clase base abstracta y `Fusil`, `Francotirador`, `Pistola`, `Escopeta`, `Subfusil` y granadas como hijas, más la entidad `Jugador` de otra familia.
3. Expone un **CRUD REST completo** sobre la familia `Arma`, con un único campo `tipo` en el JSON y discriminación polimórfica controlada por DTOs y Jackson.
4. Implementa **dos reglas de negocio de juego** en la capa `service` que alimentan una **simulación balística persistida** (`/api/simulacion/combate`).
5. Implementa una **jerarquía de excepciones del dominio** lanzada por los services y un **handler HTTP dedicado por tipo** en `@RestControllerAdvice`, para distinguir el error por código HTTP (409 vs 422) y por JSON.
6. Mantiene y expone el protocolo de dominio `UICapable` de POO-07 consumido polimórficamente desde `/ui/elementos` y `/ui/renderizar` sobre entidades leídas de H2.
7. Versiona una colección completa de **Insomnia** en `insomnia/` y `docs/insomnia/`, con el caso de munición agotada en un solo disparo y el pedido de renderizado incluido.

---

## 2. Arquitectura en 3 Capas

```text
┌────────────────────────────────────────────────────────────────────────┐
│                    1. Capa de Presentación / REST                      │
│  - ArmaCrudController / JugadorController: CRUD en /api/armas y /api/jugadores │
│  - SimulacionController: /api/simulacion/combate y /estado             │
│  - UIController: consumo polimórfico de UICapable desde H2             │
│  - ArmaController: endpoints históricos de demostración POO-06         │
│  - IndexController: metadatos y catálogo de endpoints en GET /         │
│  - GlobalExceptionHandler: handlers dedicados por cada tipo de excepción │
│  - rest/dto: ArmaRequest y JugadorRequest (sin lógica de negocio)      │
└───────────────────────────────────┬────────────────────────────────────┘
                                    │ DTOs / Mapas de PATCH
                                    ▼
┌────────────────────────────────────────────────────────────────────────┐
│                  2. Capa de Lógica de Negocio (Service)                │
│  - ArmaService: construcción de subtipos, validaciones y balística     │
│  - JugadorService: validaciones y CRUD de combatientes                 │
│  - SimulacionService: orquesta las dos reglas y persiste sus efectos   │
│  - Excepciones lanzadas aquí: DominioCS2Exception y sus ramas          │
└───────────────────────────────────┬────────────────────────────────────┘
                                    │ Entidades / Interfaces JpaRepository
                                    ▼
┌────────────────────────────────────────────────────────────────────────┐
│                   3. Capa de Persistencia (Repository)                 │
│  - ArmaRepository (extends JpaRepository<Arma, Long>)                  │
│  - JugadorRepository (extends JpaRepository<Jugador, Long>)            │
│  - Mapeo JPA: Single Table (@Entity, @DiscriminatorColumn)             │
│  - Base de Datos H2 en Memoria: jdbc:h2:mem:cs2db (/h2-console)        │
└────────────────────────────────────────────────────────────────────────┘
```

Los controllers no inyectan repositorios. Las entidades no validan, no lanzan excepciones y no ejecutan reglas: son estado, accessors y constructores, con campos `private` y constructor JPA `protected`.

---

## 3. Entidades Persistidas y Polimorfismo

1. **`Arma` (Clase Base Abstracta):** entidad JPA con `@Inheritance(strategy = InheritanceType.SINGLE_TABLE)` y discriminador `tipo_arma`. Tiene los atributos comunes (`id`, `nombre`, `precio`, `dano`, `peso`, `municionMax`, `municionActual`), declara `describirComportamiento()` como abstracto y usa `@JsonTypeInfo(property = "tipo")`. `getTipo()` lleva `@JsonIgnore` para no repetir la clave discriminadora.
2. **Hijas concretas:** `Fusil` (AK-47 y M4A4), `Francotirador` (`zoom`), `Pistola` (`cargador` y `modoDisparo`), `Escopeta` (`cartuchos` y `dispersión`), `Subfusil` (`cargador`) y la familia `Granada` con `GranadaFlash`, `GranadaHumo` y `GranadaIncendiaria`.
3. **`Jugador` (Familia Independiente):** entidad en la tabla `jugadores` con `id`, `alias`, `bando`, `salud` y `dinero`. Implementa `UICapable` aprovechando el `default method` `renderizarUI()`.
4. **Construcción:** el cliente envía un `ArmaRequest` con `tipo`; `ArmaService` elige el subtipo y llama a su constructor de negocio. Jackson serializa el arma con un único `tipo`.
5. **Reglas fuera de la entidad:** el daño efectivo, el consumo de munición y la baja de salud los calcula `ArmaService` o `SimulacionService`, nunca `Arma`, `Francotirador`, `Pistola` ni `Jugador`.

---

## 4. Jerarquía de Excepciones del Dominio y Handlers HTTP

```text
                   RuntimeException (Java)
                             ▲
                             │
                   DominioCS2Exception (Base del Dominio)
                    (Handler fallback: HTTP 400 Bad Request)
                             ▲
              ┌──────────────┴──────────────┬─────────────────┐
              │                             │                 │
 MunicionAgotadaException       EconomiaInvalidaException   ReglaNegocioException
 (Handler: HTTP 409 Conflict)   (Handler: HTTP 422 Unprocessable) (HTTP 400)
              │
 RecursoNoEncontradoException (HTTP 404)
```

- **Clase Base `DominioCS2Exception`:** unchecked, para rollback transaccional automático sin contaminar firmas con `throws`.
- **Hija 1 `MunicionAgotadaException`:** lanzada por `ArmaService.consumirMunicion` cuando el cargador está vacío. Retorna **409 Conflict**, código `CS2_ARMA_SIN_MUNICION`.
- **Hija 2 `EconomiaInvalidaException`:** lanzada por `ArmaService` ante cualquier precio fuera de `$0..$16,000`, tanto en POST, PUT o PATCH. Retorna **422 Unprocessable Entity**, código `CS2_ECONOMIA_EXCEDIDA`.
- **`RecursoNoEncontradoException`:** lanzada por los services si el ID no existe. Retorna **404 Not Found**, código `CS2_NOT_FOUND`.
- **`ReglaNegocioException`:** lanzada por los services ante daño, peso, munición, salud o dinero fuera de rango. Retorna **400 Bad Request**, código `CS2_REGLA_NEGOCIO`.
- **`IllegalArgumentException`:** se mantiene su handler por si aparece desde librerías, pero las validaciones propias ya no la usan en el dominio.

---

## 5. Las Dos Reglas de Negocio de la Simulación

- **Regla 1 (Balística y Consumo de Munición — `ArmaService`):**
  > *Al disparar, el service calcula el daño efectivo según distancia y subtipo, consume una bala del cargador del arma persistida y lanza `MunicionAgotadaException` si no queda munición; el estado se guarda en H2.*

- **Regla 2 (Impacto y Salud Vital — `SimulacionService`):**
  > *El service suma el daño de ambos disparos y asigna al jugador persistido `salud = max(0, salud - danioTotal)`, determinando en el resultado si sobrevive; no existe un método `recibirDanio()` ni `estaVivo()` en la entidad.*

`SimulacionService.ejecutarSimulacionCombate` usa `ArmaService` para la regla 1 y `JugadorService` para localizar al combatiente, y persiste los tres cambios.

---

## 6. Catálogo de Endpoints REST

| Verbo | URL / Recurso | Descripción | Código Éxito | Códigos Error |
|---|---|---|---|---|
| `GET` | `/` | Confirma servicio vivo y metadatos | `200 OK` | - |
| `GET` | `/api/armas` | Lista todas las armas persistidas en H2 con un único `tipo` | `200 OK` | - |
| `GET` | `/api/armas/{id}` | Obtiene un arma persistida por ID | `200 OK` | `404 Not Found` |
| `POST` | `/api/armas` | Crea el subtipo indicado por `ArmaRequest.tipo` | `201 Created` | `400` / `422` |
| `PUT` | `/api/armas/{id}` | Reemplazo completo; exige todos los campos obligatorios | `200 OK` | `400` / `404` / `422` |
| `PATCH` | `/api/armas/{id}` | Actualización parcial; conserva los campos omitidos | `200 OK` | `400` / `404` / `422` |
| `DELETE` | `/api/armas/{id}` | Elimina físicamente el arma de H2 | `200 OK` | `404 Not Found` |
| `POST` | `/api/armas/{id}/disparar` | Dispara el arma persistida descontando munición en H2 | `200 OK` | `404` / `409 Conflict` |
| `POST` | `/api/armas/{id}/recargar` | Recarga la munición del arma al 100% en H2 | `200 OK` | `404 Not Found` |
| `POST` | `/api/jugadores` | Crea un jugador validado por `JugadorService` | `201 Created` | `400 Bad Request` |
| `GET` | `/api/jugadores` | Lista jugadores persistidos | `200 OK` | - |
| `GET` | `/api/jugadores/{id}` | Obtiene un jugador por ID | `200 OK` | `404 Not Found` |
| `POST` | `/api/simulacion/combate` | Ejecuta las 2 reglas y persiste munición y salud | `200 OK` | `404` / `409 Conflict` |
| `GET` | `/api/simulacion/estado` | Comprueba el estado persistido post-simulación | `200 OK` | - |
| `GET` | `/ui/elementos` | Consumo polimórfico de `UICapable` sobre entidades leídas de H2 | `200 OK` | - |
| `GET` | `/ui/renderizar` | Renderiza el HUD de entidades persistidas vía la interfaz | `200 OK` | `400 Bad Request` |

---

## 7. Invariantes del Dominio

Todas las invariantes se validan en los services, nunca en las entidades:

- **Nombre y Alias:** no nulos ni vacíos; nombres de armas únicos (ignorando mayúsculas).
- **Precio Económico CS2:** `$0.00 <= precio <= $16,000.00` en POST, PUT y PATCH; cualquier violación es `EconomiaInvalidaException` (422).
- **Daño Balístico:** `1 <= dano <= 500`.
- **Peso y Munición:** `peso > 0`, `municionMax > 0` y `0 <= municionActual <= municionMax`.
- **Salud del Jugador:** `0 <= salud <= 100`; la simulación nunca deja valores negativos.
- **Dinero del Jugador:** `0 <= dinero <= 16000`.

---

## 8. Cómo Probar la Solución (Guía de Evaluación)

### Paso 1: Arrancar la Aplicación
```bash
./mvnw spring-boot:run
```
El servicio inicia en el puerto 8080; H2 está en `http://localhost:8080/h2-console` (`jdbc:h2:mem:cs2db`, usuario `sa`, clave vacía).

### Paso 2: Importar la Colección de Insomnia
1. Abrir Insomnia.
2. **Import Data → From File**.
3. Seleccionar `insomnia/insomnia_collection_lp3_poo08.json` o `docs/insomnia/insomnia_collection_lp3_poo08.json`.
4. El entorno `Base Environment` trae `base_url = http://localhost:8080`.

### Paso 3: Recorrido de Pruebas en Insomnia (17 pedidos)

1. **01 - GET / (Servicio Vivo):** metadatos y catálogo.
2. **02 - POST /api/armas (Crear Francotirador):** `tipo: "FRANCOTIRADOR"`, `zoom: 2` → 201.
3. **03 - POST /api/armas (Crear Pistola):** `tipo: "PISTOLA"`, `cargador: 12` → 201.
4. **04 - GET /api/armas:** verifica los subtipos discriminados.
5. **05 - GET /api/armas/1:** arma precargada.
6. **06 - PUT /api/armas/1:** reemplazo completo ("AK-47 Vulcan").
7. **07 - PATCH /api/armas/1:** solo precio y daño; un GET confirma el resto intacto.
8. **08 - DELETE /api/armas/2:** borrado físico; el GET posterior da 404.
9. **09 - POST Ilegal:** `dano: -15` → 400 `CS2_REGLA_NEGOCIO`; la BD no crece.
10. **10 - GET ID Inexistente:** `/api/armas/999` → 404 `CS2_NOT_FOUND`.
11. **10b - PATCH /api/armas/1:** deja `municionActual: 0` en un solo paso.
12. **11 - Excepción Hija 1:** un único disparo sobre el arma del paso 10b → 409 `CS2_ARMA_SIN_MUNICION`.
13. **12 - Excepción Hija 2:** precio `25000` → 422 `CS2_ECONOMIA_EXCEDIDA`.
14. **13 - GET /ui/elementos:** consume `UICapable` con un jugador y un arma leídos de H2.
15. **14 - GET /ui/renderizar:** renderiza el HUD de esas mismas entidades persistidas.
16. **15 - POST /api/simulacion/combate:** aplica Regla 1 y Regla 2.
17. **16 - GET /api/simulacion/estado:** comprueba munición y salud persistentes.

### Paso 4: Ejecutar Pruebas Automatizadas
```bash
./mvnw clean test
```
Ejecuta **21 pruebas** (persistencia, CRUD, validaciones en services, excepciones, simulación, interfaz, ocultamiento y contexto Spring), todas aprobadas con 0 errores y 0 fallos.
