# Especificaciones POO-09 — APIs cruzadas (CS2)

**Alumno:** César Ramírez Román (`cesarraa`)  
**Repositorio:** `https://github.com/cesarraa/crr-taller-git-2026`  
**Rama de trabajo:** `poo09-apis-cruzadas`  
**Enunciado:** `ejercicio-poo-09-apis-cruzadas-2026-11-16`

---

## 1. Objetivo

Publicar el OpenAPI de la API CS2 propia, consumir las APIs de dos compañeros usando solo sus documentos y exponer un escenario que solo se persista en H2 cuando ambas validaciones ajenas acepten. La entrega es individual; el grupo sirve para acordar a quién se consulta.

## 2. Mapa del grupo

| Rol | Servicio | Base URL | OpenAPI | Regla publicada en una frase |
|---|---|---|---|---|
| A1 | CS2 de César (este repo) | `http://localhost:8082` | `http://localhost:8082/v3/api-docs` | El precio del arma debe estar entre $0 y $16.000; ruta `POST /api/armas` |
| A2 | CS2 de Giovanni Monteggia | `http://localhost:8081` | `http://localhost:8081/v3/api-docs` | Munición: el rifle necesita balas para disparar; `POST /api/simulacion/disparar?rifleId={id}&jugadorId={id}&distancia={valor}` responde 409 si no quedan balas |
| A3 | CS2 de Luis Tauber | `http://localhost:8080` | `http://localhost:8080/v3/api-docs` | Un arma de fuego necesita munición para disparar y una granada no se puede lanzar dos veces; `POST /simulaciones/combate?armaDeFuegoId={id}&granadaId={id}` |

El orden de consulta es A2 → A3: si A2 rechaza, no se llama a A3 y la base queda intacta.

## 3. Escenario en una frase

Un disparo de francotirador a una distancia dada sobre un jugador local: A2 valida la munición de Giovanni y A3 valida la munición y la granada de Luis; solo si las dos aceptan se guarda la jugada en el H2 de A1 y un `GET` posterior la devuelve. A2 responde 200 con `{"mensaje":"AK-47 ROUND ROBIN dispara a 35 m y causa 20 de daño.","rifleId":108,"municionRestante":28,"jugadorId":100,"danoRecibido":20,"vidaRestante":60}` o 409 con `{"error":"No queda munición en AK-47 VACIO CESAR"}`; A3 responde 200 con `{"danioInfligido":19,"balasRestantes":11,"granadaLanzada":true}` o 409 con `{"error":"La granada con id 2 ya fue lanzada"}`.

## 4. Cómo probarlo

1. Arrancar los tres servicios en este orden: Luis en 8080, Giovanni en 8081 y A1 en 8082, cada uno con `./mvnw spring-boot:run`.
2. Abrir `http://localhost:8082/swagger-ui/index.html` e importar además los OpenAPI de A2 y A3.
3. Camino feliz: `POST /api/escenarios` con `armaId`, `jugadorId`, `distancia`, `headshot`, `nombreEscenario` y `descripcion`; esperar 201 y verificar con `GET /api/escenarios` que la jugada quedó guardada.
4. Rechazo de A2: crear en Giovanni un rifle `{"tipo":"rifle","nombre":"AK-47 VACIO CESAR","precio":2700,"dano":36,"cargador":1}`, disparar una vez con ese id y repetir el disparo; esperar 422 con `codigo=CS2_VALIDACION_A2_RECHAZADA`, `origen=A2`, `statusExterno=409` y la base sin cambios.
5. Rechazo de A3: llamar dos veces a `POST /simulaciones/combate?armaDeFuegoId=1&granadaId=2` en el servicio de Luis; la segunda responde 409 y acá debe dar 422 con `codigo=CS2_VALIDACION_A3_RECHAZADA`, `origen=A3`, `statusExterno=409` y la base sin cambios.
6. Sin servicios ajenos: `./mvnw clean test` ejecuta las 32 pruebas con los clientes mockeados, cubriendo los tres casos anteriores, payloads, API caída, lectura de lo persistido y el parseo de las respuestas de Giovanni y Luis.

## 5. Configuración

Las URLs, paths y timeouts se definen en `application.properties` y se sobrescriben con `A2_BASE_URL`, `A2_OPENAPI_URL`, `A2_VALIDACION_PATH`, `A2_CONNECT_TIMEOUT_MS`, `A2_READ_TIMEOUT_MS` y las equivalentes para `A3`. Los IDs de Giovanni se configuran con `A2_RIFLE_ID` / `A2_JUGADOR_ID` (defaults `108` / `100`) y la distancia con `A2_DISTANCIA` (default `35`); los de Luis con `A3_ARMA_DE_FUEGO_ID` / `A3_GRANADA_ID` (defaults `1` / `2`).

## 6. Estado de la entrega

- Parte local terminada: arranque en 8082, endpoint de escenario, OpenAPI propio, 32 tests, README, bitácora y colección de Insomnia con 10 pedidos y 3 respuestas de ejemplo.
- A2 (Giovanni) y A3 (Luis) integrados con sus contratos reales de query params y sus respuestas 200/409.
- Pendiente: publicar el working tree (el HEAD público `f7a060b` todavía no trae springdoc) y actualizar el enlace al commit final.
