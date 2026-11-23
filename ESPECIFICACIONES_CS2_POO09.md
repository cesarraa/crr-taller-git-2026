# Especificaciones POO-09 — APIs cruzadas (CS2)

**Alumno:** César Ramírez Román (`cesarraa`)  
**Repositorio:** `https://github.com/cesarraa/crr-taller-git-2026`  
**Rama de trabajo:** `main` (desarrollo en `poo09-apis-cruzadas`, ya integrado)
**Enunciado:** `ejercicio-poo-09-apis-cruzadas-2026-11-16`

---

## 1. Objetivo

Publicar el OpenAPI de la API CS2 propia, consumir las APIs de dos compañeros usando solo sus documentos y exponer un escenario que solo se persista en H2 cuando ambas validaciones ajenas acepten. La entrega es individual; el grupo sirve para acordar a quién se consulta.

## 2. Mapa del grupo

| Rol | Servicio | Base URL | OpenAPI | Regla publicada en una frase |
|---|---|---|---|---|
| A1 | CS2 de César (este repo) | `http://localhost:8082` | `http://localhost:8082/v3/api-docs` | El precio del arma debe estar entre $0 y $16.000; ruta `POST /api/armas` |
| A2 | CS2 de Giovanni Monteggia | `http://localhost:8081` | `http://localhost:8081/v3/api-docs` | Munición stateless: `POST /api/reglas/disparo` con `{"municionActual":29,"danoBase":36,"precision":0.85,"distancia":35}`; 200 con `aceptada:true` o 400 con `aceptada:false` |
| A3 | CS2 de Luis Tauber | `http://localhost:8080` | `http://localhost:8080/v3/api-docs` | Munición y granada stateless: `POST /api/reglas/combate` con `{"tieneBalas":true,"granadaLanzada":false}`; 200 con `aceptada:true` o `aceptada:false` |

El orden de consulta es A2 → A3: si A2 rechaza, no se llama a A3 y la base queda intacta.

## 3. Escenario en una frase

Un disparo de francotirador a una distancia dada sobre un jugador local: A2 valida la munición de Giovanni y A3 valida la munición y la granada de Luis; solo si las dos aceptan se guarda la jugada en el H2 de A1 y un `GET` posterior la devuelve. A2 responde 200 con `{"aceptada":true,"regla":"DISPARO","mensaje":"La regla acepta el disparo.","danoEfectivo":15}` o 400 con `{"aceptada":false,"regla":"DISPARO","mensaje":"El rifle no tiene munición.","danoEfectivo":null}`; A3 responde 200 con `{"aceptada":true,"mensaje":"La regla acepta el disparo y el lanzamiento.","danioEfectivo":11,"balasTransferidas":null}` o 200 con `{"aceptada":false,"mensaje":"El arma no tiene balas en el cargador."}`.

## 4. Cómo probarlo

1. Arrancar los tres servicios en este orden: Luis en 8080, Giovanni en 8081 y A1 en 8082, cada uno con `./mvnw spring-boot:run`.
2. Abrir `http://localhost:8082/swagger-ui/index.html` e importar además los OpenAPI de A2 y A3.
3. Camino feliz: `POST /api/escenarios` con `armaId`, `jugadorId`, `distancia`, `headshot`, `nombreEscenario` y `descripcion`; esperar 201 y verificar con `GET /api/escenarios` que la jugada quedó guardada.
4. Rechazo de A2: iniciar A1 con `A2_MUNICION_ACTUAL=0`; Giovanni responde 400 con `aceptada:false` y acá debe dar 422 con `codigo=CS2_VALIDACION_A2_RECHAZADA`, `origen=A2`, `statusExterno=400` y la base sin cambios.
5. Rechazo de A3: iniciar A1 con `A3_TIENE_BALAS=false` (o `A3_GRANADA_LANZADA=true`); Luis responde 200 con `aceptada:false` y acá debe dar 422 con `codigo=CS2_VALIDACION_A3_RECHAZADA`, `origen=A3`, `statusExterno=200` y la base sin cambios.
6. Sin servicios ajenos: `./mvnw clean test` ejecuta las 33 pruebas con los clientes mockeados, cubriendo los tres casos anteriores, payloads, API caída, lectura de lo persistido y el parseo de las respuestas stateless de Giovanni y Luis.

## 5. Configuración

Las URLs, paths y timeouts se definen en `application.properties` y se sobrescriben con `A2_BASE_URL`, `A2_OPENAPI_URL`, `A2_VALIDACION_PATH`, `A2_CONNECT_TIMEOUT_MS`, `A2_READ_TIMEOUT_MS` y las equivalentes para `A3`. Giovanni recibe `A2_MUNICION_ACTUAL` / `A2_DANO_BASE` / `A2_PRECISION` (defaults `29` / `36` / `0.85`) y `A2_DISTANCIA` (default `35`). Luis recibe `A3_TIENE_BALAS` / `A3_GRANADA_LANZADA` (defaults `true` / `false`).

## 6. Estado de la entrega

- Parte local terminada: arranque en 8082, endpoint de escenario, OpenAPI propio, 33 tests, README, bitácora y colección de Insomnia con 10 pedidos y 3 respuestas de ejemplo.
- A2 (Giovanni) y A3 (Luis) integrados con sus contratos stateless `/api/reglas` y sus respuestas 200/400 con `aceptada`.
- Commit de la solución (código final verificado por los tres integrantes): https://github.com/cesarraa/crr-taller-git-2026/commit/24921ac4bd3c9dd8db183c60b5daf5c778375a22
- Commit inicial de la solución POO-09: https://github.com/cesarraa/crr-taller-git-2026/commit/a4e881172645a27915260219fda023e78b2ea65f
- Commit de documentación: https://github.com/cesarraa/crr-taller-git-2026/commit/376ea13ef07979e458d3f766206efc655b967cc0
