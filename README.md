# Vuelos Management — Spring Boot, Arquitectura Hexagonal, DDD y CQRS

API REST para la gestión de **vuelos** y **usuarios**, construida con Java 17 y Spring Boot
siguiendo los principios de **arquitectura hexagonal (puertos y adaptadores)**, **Domain-Driven
Design (DDD)** y **CQRS** (separación de comandos y consultas).

El proyecto toma como base el microservicio `users-management-microservice` y extiende su
arquitectura para incorporar el ciclo CRUDL completo de la entidad **Vuelo** como entregable de la
Unidad 3.

---

## Enlaces del proyecto

| Recurso                | Enlace                                                                 |
| ---------------------- | ---------------------------------------------------------------------- |
| Repositorio            | <https://github.com/johannmoreno/users-management-microservice>         |
| Aplicación desplegada  | <https://vuelos-management-api.onrender.com>                            |
| Health check           | <https://vuelos-management-api.onrender.com/health>                     |
| Swagger UI             | <https://vuelos-management-api.onrender.com/swagger-ui.html>            |
| OpenAPI JSON           | <https://vuelos-management-api.onrender.com/v3/api-docs>                |
| Colección Postman      | <https://github.com/johannmoreno/users-management-microservice/tree/main/postman> |
| Integración continua   | <https://github.com/johannmoreno/users-management-microservice/actions> |

> El servicio corre en el plan gratuito de Render: se suspende tras 15 minutos de inactividad y la
> primera petición posterior puede tardar ~50 segundos. Abra `/health` antes de la demostración.

---

## Tabla de contenido

1. [Enlaces del proyecto](#enlaces-del-proyecto)
2. [Stack tecnológico](#stack-tecnológico)
3. [Arquitectura](#arquitectura)
4. [Estructura del proyecto](#estructura-del-proyecto)
5. [Requisitos](#requisitos)
6. [Configuración](#configuración)
7. [Base de datos](#base-de-datos)
8. [Ejecución local](#ejecución-local)
9. [Pruebas](#pruebas)
10. [API REST](#api-rest)
11. [Modelo de dominio: Vuelo](#modelo-de-dominio-vuelo)
12. [Seguridad y roles](#seguridad-y-roles)
13. [Documentación OpenAPI](#documentación-openapi)
14. [Colección Postman](#colección-postman)
15. [Docker](#docker)
16. [Integración continua](#integración-continua)
17. [Despliegue en la nube](#despliegue-en-la-nube)
18. [Decisiones de diseño](#decisiones-de-diseño)

---

## Stack tecnológico

| Componente        | Versión / tecnología                          |
| ----------------- | --------------------------------------------- |
| Lenguaje          | Java 17                                       |
| Framework         | Spring Boot 3.3.5                             |
| Persistencia      | JDBC + MySQL 8.4 (`PreparedStatement`)        |
| Seguridad         | Spring Security + JWT (JJWT)                  |
| Validación        | Jakarta Bean Validation                       |
| Documentación API | springdoc-openapi / Swagger UI                |
| Pruebas           | JUnit 5, Mockito, MockMvc, AssertJ            |
| Build             | Maven Wrapper (`mvnw`)                        |
| Contenedores      | Docker multi-stage + Docker Compose           |
| CI                | GitHub Actions                                |
| Despliegue        | Render (Blueprint `render.yaml`)              |

---

## Arquitectura

El código respeta la **regla de dependencia**: las capas externas dependen de las internas, nunca al
contrario.

```
┌─────────────────────────────────────────────────────────────┐
│  Infrastructure (adaptadores)                               │
│  REST · Persistencia MySQL · Seguridad JWT · Configuración  │
└───────────────▲───────────────────────────────▲─────────────┘
                │ implementa puertos            │ usa puertos
┌───────────────┴───────────────────────────────┴─────────────┐
│  Application (casos de uso / CQRS)                          │
│  Commands · Queries · Handlers · Puertos de entrada/salida   │
└───────────────▲─────────────────────────────────────────────┘
                │ depende de
┌───────────────┴─────────────────────────────────────────────┐
│  Domain (núcleo)                                            │
│  Modelos · Value Objects · Enums · Excepciones de dominio   │
└─────────────────────────────────────────────────────────────┘
```

- **Dominio**: no conoce Spring, JPA, JDBC ni HTTP. Contiene las reglas de negocio, validaciones,
  *value objects*, enums y excepciones propias.
- **Aplicación**: implementa los casos de uso. Cada operación tiene su *command* o *query* y su
  puerto de entrada; la persistencia se consume a través de puertos de salida.
- **Infraestructura**: implementa los puertos. El controlador REST traduce DTOs, el repositorio JDBC
  traduce filas y la seguridad resuelve la autenticación/autorización.

### CQRS

| Escritura (commands)        | Lectura (queries)             |
| --------------------------- | ----------------------------- |
| `CreateVueloCommand`        | `GetVueloByIdQuery`           |
| `UpdateVueloCommand`        | `GetAllVuelosUseCase`         |
| `DeleteVueloCommand`        | `GetVueloByIdUseCase`         |

Los *handlers* son clases `*Service` que implementan las interfaces `*UseCase`, manteniendo el mismo
estilo del proyecto base.

---

## Estructura del proyecto

```
src/main/java/com/jcaa/usersmanagement
├── domain
│   ├── enums            # EstadoVuelo, UserRole, UserStatus
│   ├── exception        # excepciones de dominio (404, 409, validaciones)
│   ├── model            # VueloModel, UserModel
│   └── valueobject      # VueloId, NumeroVuelo, PuestoVuelo, ValorVuelo, Aeropuerto
├── application
│   ├── port
│   │   ├── in           # casos de uso (entrada)
│   │   └── out          # repositorios y servicios externos (salida)
│   └── service
│       ├── dto
│       │   ├── command  # comandos de escritura
│       │   └── query    # consultas de lectura
│       ├── mapper       # command/query → modelo de dominio
│       └── *Service     # implementación de los casos de uso
└── infrastructure
    ├── adapter/persistence  # entidad, DTO, mapper y repositorio JDBC
    ├── config               # OpenAPI, datasource, asincronía
    ├── entrypoint/rest      # controladores, DTOs REST, mapper y manejo de errores
    └── security             # filtro JWT, configuración y manejadores de error
```

---

## Requisitos

- JDK 17 o superior
- MySQL 8.x (local con Laragon, Docker o servicio en la nube)
- Maven Wrapper incluido (no requiere Maven instalado)
- Opcional: Docker Desktop para ejecutar con contenedores
- Opcional: Postman para ejecutar la colección incluida

---

## Configuración

La aplicación lee las variables de entorno desde el archivo `.env` (ignorado por git) o desde el
entorno del sistema. Use `.env.example` como plantilla:

```bash
cp .env.example .env
```

| Variable                  | Descripción                                  | Valor por defecto |
| ------------------------- | -------------------------------------------- | ----------------- |
| `PORT`                    | Puerto HTTP de la aplicación                 | `8080`            |
| `DB_HOST`                 | Host de MySQL                                | `localhost`       |
| `DB_PORT`                 | Puerto de MySQL                              | `3306`            |
| `DB_NAME`                 | Nombre de la base de datos                   | `vuelos_management` |
| `DB_USERNAME`             | Usuario de MySQL                             | `root`            |
| `DB_PASSWORD`             | Contraseña de MySQL                          | *(vacío)*         |
| `DB_SSL_MODE`             | Modo SSL de la conexión JDBC                 | `DISABLED`        |
| `SMTP_HOST`               | Servidor SMTP para notificaciones            | `smtp.gmail.com`  |
| `SMTP_PORT`               | Puerto SMTP                                  | `587`             |
| `SMTP_USERNAME`           | Usuario SMTP                                 | *(vacío)*         |
| `SMTP_PASSWORD`           | Contraseña SMTP                              | *(vacío)*         |
| `SMTP_FROM_ADDRESS`       | Remitente de las notificaciones              | *(vacío)*         |
| `SMTP_FROM_NAME`          | Nombre del remitente                         | `Gestion de Vuelos` |
| `JWT_SECRET`              | Secreto Base64 para firmar los tokens        | valor de desarrollo |
| `JWT_EXPIRATION_SECONDS`  | Vigencia del token en segundos               | `900`             |

> **Importante:** nunca guarde credenciales reales en `application.properties`, `compose.yaml` ni
> `render.yaml`. En producción defina `JWT_SECRET` con un valor aleatorio de al menos 32 bytes en
> Base64 y proporcione las credenciales de base de datos como secretos.

---

## Base de datos

El script `src/main/resources/schema.sql` crea la base `vuelos_management` y las tablas `users` y
`vuelos`. La tabla `vuelos` incluye:

- `estado` como `ENUM('PROGRAMADO', 'CONFIRMADO', 'EN_VUELO', 'ATERRIZADO', 'CANCELADO', 'RETRASADO')`.
- `valor` como `DECIMAL(14,2)`.
- Restricción única `uk_vuelos_numero_fecha_salida_puesto` sobre `(numero, fecha_salida, puesto)`.

Ejecución manual en MySQL:

```bash
mysql -u root -p < src/main/resources/schema.sql
```

El script siembra un usuario administrador para pruebas:

| Campo    | Valor             |
| -------- | ----------------- |
| Email    | `admin@example.com` |
| Password | `Admin1234!`      |
| Rol      | `ADMIN`           |

> Cambie esta contraseña antes de exponer la aplicación en producción.

---

## Ejecución local

1. Cree la base de datos y cargue el esquema (ver sección anterior).
2. Cree el archivo `.env` a partir de `.env.example` y ajuste `DB_*`.
3. Ejecute la aplicación:

```bash
./mvnw spring-boot:run
```

En Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

La API queda disponible en `http://localhost:8080` y Swagger UI en
`http://localhost:8080/swagger-ui.html`.

Verificación rápida:

```bash
curl http://localhost:8080/health
# {"status":"UP","name":"users-management","version":"2.2.0"}
```

---

## Pruebas

La suite cubre dominio, aplicación, persistencia, seguridad y capa REST:

```bash
./mvnw clean test
```

Estado actual: **337 pruebas, 0 fallos, 0 errores**.

También se puede generar el paquete ejecutable:

```bash
./mvnw clean package
java -jar target/users-management-2.2.0.jar
```

---

## API REST

Base URL local: `http://localhost:8080`
Base URL producción: `https://vuelos-management-api.onrender.com`

### Salud

| Método | Ruta      | Acceso  | Descripción                          |
| ------ | --------- | ------- | ------------------------------------ |
| GET    | `/health` | Público | Estado de la aplicación y versión    |

### Autenticación

| Método | Ruta              | Acceso  | Descripción                          |
| ------ | ----------------- | ------- | ------------------------------------ |
| POST   | `/api/auth/login` | Público | Retorna un token JWT (`accessToken`) |

```json
{
  "email": "admin@example.com",
  "password": "Admin1234!"
}
```

### Usuarios

| Método | Ruta               | Roles              | Descripción              |
| ------ | ------------------ | ------------------ | ------------------------ |
| POST   | `/api/users`       | Público            | Registrar usuario        |
| GET    | `/api/users`       | `ADMIN`, `REVIEWER`| Listar usuarios          |
| GET    | `/api/users/{id}`  | `ADMIN`, `REVIEWER`| Consultar usuario por ID |
| PUT    | `/api/users/{id}`  | `ADMIN`            | Actualizar usuario       |
| DELETE | `/api/users/{id}`  | `ADMIN`            | Eliminar usuario         |

### Vuelos

| Método | Ruta                | Roles               | Descripción             |
| ------ | ------------------- | ------------------- | ----------------------- |
| POST   | `/api/vuelos`       | `ADMIN`             | Crear vuelo             |
| GET    | `/api/vuelos`       | `ADMIN`, `REVIEWER` | Listar vuelos           |
| GET    | `/api/vuelos/{id}`  | `ADMIN`, `REVIEWER` | Consultar vuelo por ID  |
| PUT    | `/api/vuelos/{id}`  | `ADMIN`             | Actualizar vuelo        |
| DELETE | `/api/vuelos/{id}`  | `ADMIN`             | Eliminar vuelo          |

Ejemplo de creación:

```json
{
  "id": "vuelo-001",
  "fechaCompra": "2026-01-10T08:00:00",
  "fechaSalida": "2026-12-15T10:30:00",
  "fechaLlegada": "2026-12-15T13:45:00",
  "agenciaViajes": "Agencia Caribe",
  "aerolinea": "Avianca",
  "numero": "AV9001",
  "valor": 450000.00,
  "cliente": "Carlos Perez",
  "puesto": "12A",
  "avion": "Airbus A320",
  "aeropuertoSalida": "Aeropuerto Rafael Nunez",
  "aeropuertoLlegada": "Aeropuerto El Dorado",
  "piloto": "Capitan Juan Gomez"
}
```

### Códigos de respuesta

| Código | Significado                                                        |
| ------ | ------------------------------------------------------------------ |
| `200`  | Consulta o actualización exitosa                                   |
| `201`  | Recurso creado                                                     |
| `204`  | Recurso eliminado                                                  |
| `400`  | Datos de entrada inválidos (Bean Validation o reglas de dominio)   |
| `401`  | Token ausente, inválido o credenciales incorrectas                 |
| `403`  | El rol autenticado no tiene permisos                               |
| `404`  | Recurso no encontrado                                              |
| `409`  | Conflicto: ID o clave única `(numero, fecha_salida, puesto)` en uso |
| `500`  | Error interno                                                      |

---

## Modelo de dominio: Vuelo

`VueloModel` es un agregado inmutable (`@Value`) que valida sus invariantes al construirse:

- `fechaCompra` no puede ser posterior a `fechaSalida`.
- `fechaLlegada` debe ser estrictamente posterior a `fechaSalida`.
- `aeropuertoSalida` y `aeropuertoLlegada` no pueden ser iguales.
- Los textos no pueden estar vacíos ni exceder su longitud máxima.

Value Objects:

| Value Object  | Regla                                              |
| ------------- | -------------------------------------------------- |
| `VueloId`     | No vacío                                           |
| `NumeroVuelo` | `^[A-Z0-9]{2,10}$`                                 |
| `PuestoVuelo` | `^[0-9]{1,2}[A-Z]$` (por ejemplo `12A`)            |
| `ValorVuelo`  | Mayor que cero y máximo 2 decimales                |
| `Aeropuerto`  | No vacío y máximo 100 caracteres                   |

Estados (`EstadoVuelo`): `PROGRAMADO`, `CONFIRMADO`, `EN_VUELO`, `ATERRIZADO`, `CANCELADO`,
`RETRASADO`. El modelo expone transiciones explícitas: `confirmar()`, `cancelar()`, `despegar()`,
`aterrizar()` y `retrasar()`.

---

## Seguridad y roles

La autenticación es **stateless** mediante JWT enviado en la cabecera:

```
Authorization: Bearer <accessToken>
```

Matriz de permisos:

| Recurso            | `ADMIN` | `REVIEWER` | `MEMBER` | Público |
| ------------------ | ------- | ---------- | -------- | ------- |
| `POST /api/users`  | ✓       | ✓          | ✓        | ✓       |
| `GET /api/users/**`| ✓       | ✓          | —        | —       |
| `PUT/DELETE users` | ✓       | —          | —        | —       |
| `GET /api/vuelos/**` | ✓      | ✓          | —        | —       |
| `POST/PUT/DELETE vuelos`| ✓  | —          | —        | —       |
| `/health`, login, Swagger | ✓ | ✓       | ✓        | ✓       |

---

## Documentación OpenAPI

Con la aplicación en ejecución:

| Entorno     | Swagger UI                                          | OpenAPI JSON                                   |
| ----------- | --------------------------------------------------- | ---------------------------------------------- |
| Local       | `http://localhost:8080/swagger-ui.html`             | `http://localhost:8080/v3/api-docs`            |
| Producción  | <https://vuelos-management-api.onrender.com/swagger-ui.html> | <https://vuelos-management-api.onrender.com/v3/api-docs> |

El contrato de cada controlador está separado en interfaces `*RestControllerDocs` para mantener los
controladores enfocados en el despacho HTTP.

---

## Colección Postman

La carpeta `postman/` contiene:

- `vuelos-management.postman_collection.json`
- `vuelos-management.postman_environment.json`

Pasos:

1. Importar ambos archivos en Postman.
2. Seleccionar el entorno **Vuelos Management - Local**.
3. Ajustar `baseUrl` si la API no corre en `http://localhost:8080`.
4. Ejecutar la colección en orden. El login guarda automáticamente el token en la variable `token`.

La colección incluye flujos de salud, login válido/inválido, CRUDL de usuarios, CRUDL de vuelos,
conflicto `409`, recurso eliminado `404` y acceso sin token `401`.

---

## Docker

El `Dockerfile` usa una construcción multi-stage (Maven + JRE 17) y ejecuta la aplicación con un
usuario sin privilegios.

Para levantar API + MySQL:

```bash
docker compose up --build
```

Servicios:

- API: `http://localhost:8080`
- MySQL: `localhost:3306`, base `vuelos_management`
- El esquema se carga automáticamente la primera vez que se crea el volumen.

Variables opcionales para correo y JWT:

```bash
SMTP_USERNAME=... SMTP_PASSWORD=... SMTP_FROM_ADDRESS=... docker compose up --build
```

Detener y eliminar datos:

```bash
docker compose down -v
```

---

## Integración continua

El flujo `.github/workflows/ci.yml` se ejecuta en cada push a `main` o ramas `feature/**` y en cada
pull request hacia `main`:

1. **Compilar y probar**: configura JDK 17 (Temurin), ejecuta `./mvnw clean verify` y publica los
   reportes Surefire como artefacto.
2. **Validar imagen Docker**: construye la imagen sin publicarla para comprobar el `Dockerfile`.

---

## Despliegue en la nube

Despliegue realizado con **TiDB Cloud Starter** como base de datos (compatible con MySQL) y
**Render** como plataforma de ejecución del contenedor.

| Componente | Servicio | Detalle |
| ---------- | -------- | ------- |
| Base de datos | TiDB Cloud Starter | Región `us-west-2`, puerto `4000`, TLS obligatorio |
| Aplicación | Render (Docker, plan Free) | Servicio `vuelos-management-api`, rama `main` |
| URL pública | <https://vuelos-management-api.onrender.com> | Health check en `/health` |

### Base de datos (TiDB Cloud)

1. Crear un clúster **Starter** en <https://tidbcloud.com> en la región más cercana a Render
   (`us-west-2` para la región por defecto de Render).
2. En **Settings → Networking**, verificar que el *Public Endpoint* esté habilitado y que la regla de
   red permita las IP de salida de Render (`0.0.0.0/0` en el plan gratuito, ya que Render no expone
   IP fijas).
3. En **Connect**, generar la contraseña y copiar `HOST`, `PORT` (`4000`) y `USERNAME`
   (con formato `<cluster-id>.root`).
4. Aplicar el esquema con un cliente MySQL 8:

   ```powershell
   Get-Content .\src\main\resources\schema.sql -Raw |
     & "C:\laragon\bin\mysql\mysql-8.4.3-winx64\bin\mysql.exe" `
       --host=<HOST> --port=4000 --user="<USERNAME>" --password --ssl-mode=REQUIRED
   ```

5. Verificar las tablas:

   ```sql
   USE vuelos_management;
   SHOW TABLES;
   SELECT id, email, role, status FROM users;
   ```

> TiDB es compatible con MySQL, pero usa el puerto `4000` y exige TLS: `DB_SSL_MODE=REQUIRED`.
> La aplicación no ejecuta `schema.sql` automáticamente, por lo que este paso es obligatorio.

### Aplicación (Render)

El archivo `render.yaml` define un Blueprint con:

- Runtime Docker y despliegue automático desde `main`.
- Health check en `/health`.
- `DB_NAME=vuelos_management` y `DB_SSL_MODE=REQUIRED` ya definidos.
- Variables sensibles marcadas como `sync: false` para completarlas en el panel.
- `JWT_SECRET` generado automáticamente.

Pasos:

1. En Render, **New + → Blueprint** y seleccionar este repositorio.
2. Completar los secretos: `DB_HOST`, `DB_PORT`, `DB_USERNAME`, `DB_PASSWORD`,
   `SMTP_USERNAME`, `SMTP_PASSWORD` y `SMTP_FROM_ADDRESS` (el correo es opcional; si se deja vacío
   solo se omite el envío de notificaciones).
3. Aplicar el Blueprint. Render compila la imagen Docker y arranca la aplicación en el puerto
   asignado.
4. Verificar el despliegue:

   ```bash
   curl https://vuelos-management-api.onrender.com/health
   # {"status":"UP","name":"users-management","version":"2.2.0"}
   ```

5. Abrir <https://vuelos-management-api.onrender.com/swagger-ui.html>, autenticarse con el usuario
   administrador y ejecutar el CRUDL de vuelos. También se puede importar la colección Postman y
   cambiar `baseUrl` por la URL pública.

> El plan gratuito de Render suspende el servicio tras 15 minutos sin tráfico; la primera petición
> posterior puede tardar alrededor de 50 segundos.

---

## Decisiones de diseño

- **Sin JPA/Flyway**: la persistencia usa JDBC y `schema.sql`, igual que el proyecto base, para
  mantener coherencia académica y control explícito del SQL.
- **IDs provistos por el cliente**: el `id` llega en el `POST`, replicando el comportamiento de
  `Usuario`.
- **Sin clave foránea a usuarios**: `cliente` y `piloto` son campos de texto; el alcance del
  ejercicio no exige relación con la tabla `users`.
- **Inmutabilidad**: los modelos de dominio son inmutables y las transiciones de estado retornan
  nuevas instancias.
- **Errores centralizados**: `GlobalExceptionHandler` traduce excepciones de dominio a respuestas
  HTTP consistentes (`400`, `401`, `403`, `404`, `409`).
- **Contrato OpenAPI separado**: las anotaciones viven en interfaces `*Docs` para no contaminar los
  controladores.

---

## Autor

Proyecto académico — Unidad 3.
Arquitectura Hexagonal, DDD y CQRS con Spring Boot.
