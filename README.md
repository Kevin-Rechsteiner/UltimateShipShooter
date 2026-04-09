# UltimateShipShooter (Spring Boot)

Link Frontend: https://github.com/Kevin-Rechsteiner/frontend_for_asteroid

**Schulprojekt** – Backend für ein kleines Arcade-Spiel mit WebSocket-Game-Loop, JWT-Authentifizierung und Highscore-API.

## Features

- Echtzeit-Spielzustand per WebSocket (`/ws/game`)
- JWT-basierte Anmeldung mit Cookie-Unterstützung
- Rollen-/Rechte-geschützte REST-Endpunkte
- Highscore-Endpoints (`/api/v1/highscore/...`)
- Persistenz über Spring Data JPA + MySQL

## Tech-Stack

- Java 17
- Spring Boot 4
- Spring Security + JWT (`jjwt`)
- Spring WebSocket
- Spring Data JPA + MySQL
- Maven Wrapper (`mvnw`, `mvnw.cmd`)

## Voraussetzungen

- JDK 17 installiert
- Laufende MySQL-Instanz (Standard: `localhost:3306`)
- Optional: IntelliJ IDEA

## Konfiguration

Zentrale Konfiguration: `src/main/resources/application.properties`

Wichtige Werte:

- `spring.datasource.url=jdbc:mysql://localhost:3306/ultimateshipshooter?createDatabaseIfNotExist=true`
- `spring.datasource.username=root`
- `spring.datasource.password=`
- `spring.jpa.hibernate.ddl-auto=create-drop`
- `jwt.expiration=900000` (15 Minuten)

> Hinweis: Mit `create-drop` wird das Schema beim Start erzeugt und beim Stoppen gelöscht.

## Schnellstart (Windows PowerShell)

```powershell
.\mvnw.cmd spring-boot:run
```

Danach:

- App: `http://localhost:8080/`
- Demo-Frontend: `http://localhost:8080/index.html`

Beim Start werden Demo-User angelegt (`GameApplication`):

- `student@example.com` / `StrongP@ssw0rd!`
- `admin@example.com` / `StrongP@ssw0rd!`

## Tests ausführen

```powershell
.\mvnw.cmd test
```

## REST-API (Auszug)

Authentifizierung (`/api/v1/auth`):

- `POST /register`
- `POST /authenticate`
- `POST /logout`

Autorisierung/Beispiele (`/api/v1`):

- `GET /admin/resource`
- `DELETE /admin/resource`
- `POST /user/resource`
- `PUT /user/resource`

Highscore (`/api/v1/highscore`):

- `GET /me`
- `GET /leaderboard?limit=10`

## WebSocket

- Endpoint: `/ws/game`
- Handler: `src/main/java/dev/zwazel/game/game/websocket/WebSocketEchoHandler.java`
- Typische Event-Typen:
  - `player_input`
  - `game_control`
  - `ping` / `pong`
  - `state` (Server -> Client)

## Projektstruktur (relevant)

- `src/main/java/dev/zwazel/game/GameApplication.java` - Startpunkt, Demo-User-Seeding
- `src/main/java/dev/zwazel/game/security/...` - Auth, JWT, Security-Konfiguration
- `src/main/java/dev/zwazel/game/game/websocket/...` - Spiel-Loop + Echtzeit-Logik
- `src/main/java/dev/zwazel/game/game/highscore/...` - Highscore-Logik
- `src/main/resources/static/index.html` - Einfaches Test-Frontend

## Troubleshooting

- **App startet nicht wegen DB**: MySQL starten oder `spring.datasource.*` anpassen.
- **401 bei geschützten Endpoints**: Erst über `/api/v1/auth/authenticate` einloggen.
- **WebSocket verbindet nicht**: Sicherstellen, dass auf `/ws/game` verbunden wird.
