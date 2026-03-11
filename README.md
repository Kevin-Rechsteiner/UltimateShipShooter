# spring-boot-security-db-template

## WebSocket-Demo

Dieses Projekt enthält ein minimales WebSocket-Beispiel:

- Backend-Endpunkt: `/ws`
- Demo-Frontend: `/` bzw. `/index.html`

### So funktioniert das Beispiel

1. Das Frontend baut beim Laden der Seite eine WebSocket-Verbindung zu `/ws` auf.
2. Du gibst Text in das Eingabefeld ein und klickst auf **Senden**.
3. Der Text wird an das Spring-Backend gesendet.
4. Das Backend antwortet mit `Backend hat empfangen: ...`.

### Relevante Dateien

- `src/main/java/dev/zwazel/game/game/websocket/WebSocketConfig.java`
- `src/main/java/dev/zwazel/game/game/websocket/WebSocketEchoHandler.java`
- `src/main/resources/static/index.html`
- `src/main/java/dev/zwazel/game/security/config/SecurityConfiguration.java`

### Lokal testen

Starte die Spring-Boot-Anwendung und öffne danach im Browser:

- `http://localhost:8080/`

Hinweis: Laut `application.properties` ist eine lokale MySQL-Datenbank konfiguriert. Falls die Anwendung beim Starten an der Datenbank scheitert, musst du entweder MySQL lokal bereitstellen oder die Datasource-Konfiguration für den Test anpassen.

