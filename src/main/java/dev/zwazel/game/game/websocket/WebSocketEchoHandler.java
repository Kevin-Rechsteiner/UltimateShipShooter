package dev.zwazel.game.game.websocket;

import java.io.IOException;

import org.jspecify.annotations.NonNull;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public class WebSocketEchoHandler extends TextWebSocketHandler {

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws IOException {
        session.sendMessage(new TextMessage("Verbunden mit dem Backend. Schreib etwas in das Eingabefeld."));
    }

    @Override
    protected void handleTextMessage(@NonNull WebSocketSession session, @NonNull TextMessage message) throws IOException {
        String payload = message.getPayload().trim();

        if (payload.isEmpty()) {
            session.sendMessage(new TextMessage("Bitte gib zuerst einen Text ein."));
            return;
        }

        session.sendMessage(new TextMessage("Backend hat empfangen: " + payload));
    }

    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus status) {
        // Kein zusätzlicher Cleanup nötig für dieses Minimalbeispiel.
    }
}

