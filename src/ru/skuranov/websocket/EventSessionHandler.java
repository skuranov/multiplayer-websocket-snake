
package ru.skuranov.websocket;


import com.google.gson.Gson;
import ru.skuranov.GameController;

import javax.enterprise.context.ApplicationScoped;
import javax.json.JsonObject;
import javax.json.spi.JsonProvider;
import javax.websocket.Session;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;


@ApplicationScoped
public class EventSessionHandler {

    final private GameController game = new GameController();

    final private List<Session> sessions = new CopyOnWriteArrayList<>();

    final private static EventSessionHandler sessionHandler = new EventSessionHandler();

    private EventSessionHandler() {
        super();
    }

    public static EventSessionHandler getInstance() {
        return sessionHandler;
    }

    public void addSession(Session session) {
        switch (sessions.size()) {
            case 0:
                if (!game.isActive())game.startGame(session);
                break;
            case 1:
                game.runAdditionalSnake(session);
                break;
            default:
                System.out.println("Maximum of sessions reached");
                return;
        }
        sessions.add(session);
    }

    public void removeSession(Session session) {
        sessions.remove(session);
        game.stopGame();
    }


    private JsonObject generateNewPositionJSON() {
        JsonProvider provider = JsonProvider.provider();

        Set bodySet = game.getMovementsSet().stream()
                .map(animalMovement -> animalMovement.getBody().getArrayView())
                .collect(Collectors.toSet());
        List<Integer> scores = sessions.stream().map(ses
                -> game.getSessionSnakeMovementsMap().get(ses).getScore()).collect(Collectors.toList());

        JsonObject addMessage = provider.createObjectBuilder()
                .add("action", "drawNewPosition")
                .add("scores", new Gson().toJson(scores))
                .add("bodies", new Gson().toJson(bodySet))
                .build();
        return addMessage;
    }

    private void sendToAllConnectedSessions(JsonObject message) {
        sessions.forEach(session -> sendToSession(session, message));
    }

    private void sendToSession(Session session, JsonObject message) {
        if (message != null) {
            try {
                session.getBasicRemote().sendText(message.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendNewPositionsToClients() {
        sessions.forEach(session -> sendToSession(session, generateNewPositionJSON()));
    }

    public void sendGameOver() {
        JsonProvider provider = JsonProvider.provider();

        JsonObject addMessage = provider.createObjectBuilder()
                .add("action", "gameOver")
                .build();
        sessions.forEach(session -> sendToSession(session, addMessage));
    }


    public void clearSessionList(Session surviveSession) {
        sessions.clear();
        sessions.add(surviveSession);
    }


    public GameController getGame() {
        return game;
    }
}