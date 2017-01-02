package ru.skuranov.websocket;


import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.StringReader;
import java.util.logging.Level;
import java.util.logging.Logger;

@ApplicationScoped
@ServerEndpoint("/snakeServer")
public class WebSocketServer {


    @OnOpen
    public void open(Session session) {
        EventSessionHandler.getInstance().addSession(session);
    }

    @OnClose
    public void close(Session session) {
        EventSessionHandler.getInstance().removeSession(session);
    }

    @OnError
    public void onError(Throwable error) {
        Logger.getLogger(WebSocketServer.class.getName()).log(Level.SEVERE, null, error);
    }

    @OnMessage
    public void handleMessage(String message, Session session) {
        try (JsonReader reader = Json.createReader(new StringReader(message))) {
            JsonObject jsonMessage = reader.readObject();
            if ("changeDirection".equals(jsonMessage.getString("action"))) {
                if ("left".equals(jsonMessage.getString("direction")) && EventSessionHandler.getInstance().getGame().isActive()) {
                    EventSessionHandler.getInstance().getGame().getSessionSnakeMovementsMap().get(session).getDirection().toLeft();
                }
                if ("right".equals(jsonMessage.getString("direction")) && EventSessionHandler.getInstance().getGame().isActive()) {
                    EventSessionHandler.getInstance().getGame().getSessionSnakeMovementsMap().get(session).getDirection().toRight();
                }
            }

            if ("restart".equals(jsonMessage.getString("action"))) {
                EventSessionHandler.getInstance().getGame().stopGame();
                EventSessionHandler.getInstance().getGame().startGame(session);
                EventSessionHandler.getInstance().clearSessionList(session);
            }
        }
    }
}