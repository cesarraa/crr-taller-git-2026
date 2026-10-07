# Especificaciones del Trabajo Práctico: Dominio Counter-Strike 2 (CS2)

**Asignatura:** Lenguajes de Programación 3 (CYT646)  
**Ejercicio:** POO-06 — Revisión, paquetes, constructores y sobrecarga (`ejercicio-poo-06-revision-paquetes-constructores-2026-09-30`)  
**Alumno:** César Ramírez Román  
**Usuario de GitHub:** `cesarraa`  
**Repositorio GitHub:** `https://github.com/cesarraa/crr-taller-git-2026`  
**Enlace al Commit de la Solución:** https://github.com/cesarraa/crr-taller-git-2026/commit/62066c81e70ff5432d503aabdc4122af595ff1e7  

---

## 1. Objetivo General

Publicar un microservicio HTTP con Spring Boot (Java 21, Maven) sobre el modelado orientado a objetos de **Counter-Strike 2 (CS2)**. La solución demuestra el dominio de los principios de diseño de software:
- Organización modular en paquetes estándar (`py.edu.uc.lp3.domain` y `py.edu.uc.lp3.rest.controller`).
- Ocultamiento de la información y preservación estricta de invariantes.
- Herencia y **sobreescritura** de métodos abstractos (polimorfismo dinámico).
- Constructores simples y sobrecargados con encadenamiento seguro.
- **Sobrecarga** de mensajes del dominio en tiempo de compilación.
- Consumo mediante servicios REST que exponen respuestas en formato JSON.

---

## 2. Consignas Aplicadas al Dominio de CS2

### 2.1. Organización de Paquetes
Las clases se desacoplan conforme a la plantilla oficial de la cátedra:
- `py.edu.uc.lp3.domain`: Contiene las clases del modelo balístico (`Arma`, `Francotirador`, `Pistola`, `Escopeta`, `Subfusil`, `Granada`, `GranadaTipo`, etc.).
- `py.edu.uc.lp3.rest.controller`: Contiene la capa web de servicios REST (`IndexController`, `ArmaController`).
- `py.edu.uc.lp3`: Contiene la clase de arranque `Cs2Application`.

### 2.2. Ocultamiento de la Información e Invariantes
- Los atributos de las clases son `protected` y `private`.
- Se responde a la pregunta de anclaje de la cátedra: **ninguna capa externa ni controlador puede manipular directamente la munición o dejar el arma en estado inválido**. La munición se modifica únicamente mediante los mensajes de negocio `disparar()` y `recargar()`.
- Validaciones en constructores y operaciones: rechazo inmediato mediante `IllegalArgumentException` si el precio es negativo, el daño es menor o igual a cero, el nivel de zoom es menor o igual a cero, o el cargador es no positivo.

### 2.3. Jerarquía y Sobreescritura (Overriding)
- **Método abstracto:** La clase base `Arma` declara `public abstract String describirComportamiento();`.
- **Implementación en clases hijas independientes:**
  - `Francotirador` sobreescribe `describirComportamiento()` detallando la doctrina de tiro a larga distancia, la magnificación óptica del zoom (`xZoom`) y la penalización de precisión sin apuntar.
  - `Pistola` sobreescribe `describirComportamiento()` describiendo el combate cerrado, el modo de disparo y la capacidad del cargador de mano.
- **Tratamiento polimórfico:** El endpoint `/armas/comportamientos` maneja una lista `List<Arma>`, invocando el método abstracto sobreescrito sin utilizar operadores `instanceof` ni bifurcaciones `if/else`.

### 2.4. Constructores Simples y Sobrecargados
- `Francotirador`:
  - *Constructor Simple:* `Francotirador(nombre, precio, dano, zoom)` (delega al constructor de la clase base con peso y munición estándar).
  - *Constructor Sobrecargado Completo:* `Francotirador(nombre, precio, dano, peso, municionMax, zoom)` (invoca `super(...)` y valida todos los atributos).
  - *Constructor por Defecto:* `Francotirador()` (inicializa un AWP con especificaciones canónicas de CS2).
- `Pistola`:
  - *Constructor Simple:* `Pistola(nombre, precio, dano)`.
  - *Constructor Sobrecargado Completo:* `Pistola(nombre, precio, dano, peso, municionMax, modoDisparo, cargador)`.
  - *Constructor por Defecto:* `Pistola()` (Glock-18 estándar).

### 2.5. Sobrecarga de Mensajes del Dominio (Overloading)
En la clase `Arma`, el mensaje `disparar` presenta tres firmas sobrecargadas con distinta lista de argumentos:
1. `disparar()`: Disparo simple sin argumentos a quemarropa.
2. `disparar(int distanciaMetros)`: Disparo a distancia con cálculo de atenuación balística sobre el daño infligido.
3. `disparar(int distanciaMetros, boolean tiroALaCabeza)`: Disparo de precisión con factor multiplicador crítico (Headshot).

---

## 3. Servicios REST Expuestos

1. **`GET /` (Confirmación de Servicio Activo):**
   - Retorna estado `UP`, datos del alumno, asignatura y descripción del dominio CS2.
2. **`GET /armas/construir` (Construcción con Parámetros de URL):**
   - Permite instanciar armas dinámicamente usando constructores simples o sobrecargados.
   - En caso de violación de invariantes (ej. precio negativo), captura `IllegalArgumentException` y retorna código de estado HTTP `400 Bad Request` en formato JSON.
3. **`GET /armas/comportamientos` (Polimorfismo en JSON):**
   - Retorna en JSON el resultado de enviar el mensaje `describirComportamiento()` a las clases hijas tratadas como tipo base `Arma`.
4. **`GET /armas/disparar` (Demostración de Sobrecarga):**
   - Permite ejecutar las distintas firmas sobrecargadas de disparo con parámetros opcionales `distancia` y `headshot`.

---

## 4. Guía de Ejecución y Verificación

### Arranque de la Aplicación en la Máquina Virtual Lubuntu
```bash
cd ~/Documentos/lp3/crr-taller-git-2026   # o directorio de trabajo correspondiente
./mvnw spring-boot:run
```

### Comandos de Prueba (cURL)

1. **Verificar estado de salud:**
   ```bash
   curl -s http://localhost:8080/ | jq .
   ```

2. **Construir Francotirador con constructor simple:**
   ```bash
   curl -s "http://localhost:8080/armas/construir?tipo=francotirador&nombre=AWP&precio=4750&dano=115&zoom=2" | jq .
   ```

3. **Construir Francotirador con constructor sobrecargado:**
   ```bash
   curl -s "http://localhost:8080/armas/construir?tipo=francotirador&nombre=AWP&precio=4750&dano=115&peso=4.3&municionMax=10&zoom=2" | jq .
   ```

4. **Comprobar rechazo de invariantes rotas (Error 400):**
   ```bash
   curl -s "http://localhost:8080/armas/construir?tipo=francotirador&nombre=AWP&precio=-100&dano=115" | jq .
   ```

5. **Consultar comportamientos polimórficos de las clases hijas:**
   ```bash
   curl -s http://localhost:8080/armas/comportamientos | jq .
   ```

6. **Probar sobrecarga de disparo:**
   - Disparo simple: `curl -s "http://localhost:8080/armas/disparar" | jq .`
   - Disparo a 40 metros: `curl -s "http://localhost:8080/armas/disparar?distancia=40" | jq .`
   - Disparo a 40 metros a la cabeza: `curl -s "http://localhost:8080/armas/disparar?distancia=40&headshot=true" | jq .`

7. **Ejecución de Pruebas Unitarias Automatizadas:**
   ```bash
   ./mvnw test
   ```
   *Resultado: 6 tests ejecutados, 0 fallos, 0 errores (BUILD SUCCESS).*
