
package ru.skuranov.websocket;


import com.google.gson.Gson;
import ru.skuranov.GameController;

import javax.enterprise.context.ApplicationScoped;
import javax.json.JsonObject;
import javax.json.spi.JsonProvider;
import javax.websocket.Session;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;


@ApplicationScoped
public class EventSessionHandler {

    final private GameController game = new GameController();

    final private Map<Session, Lock> sessionsLockMap= new ConcurrentHashMap<>();

    final private static EventSessionHandler sessionHandler = new EventSessionHandler();

    private EventSessionHandler() {
        super();
    }

    public static EventSessionHandler getInstance() {
        return sessionHandler;
    }

    public void addSession(Session session) {
        switch (sessionsLockMap.size()) {
            case 0:
                if (!game.isActive()) game.startGame(session);
                break;
            case 1:
                game.runAdditionalSnake(session);
                break;
            default:
                Logger.getLogger(EventSessionHandler.class.getName())
                        .log(Level.SEVERE, "Maximum of sessions reached", new Exception());
                return;
        }
        sessionsLockMap.put(session, new ReentrantLock());
    }

    public void removeSession(Session session) {
        sessionsLockMap.remove(session);
    }


    private JsonObject generateNewPositionJSON() {
        JsonProvider provider = JsonProvider.provider();

        Set bodySet = game.getMovementsSet().stream()
                .map(animalMovement -> animalMovement.getBody().getArrayView())
                .collect(Collectors.toSet());
        List<Integer> scores = sessionsLockMap.keySet().stream().map(ses
                -> game.getSessionSnakeMovementsMap().get(ses).getScore()).collect(Collectors.toList());

        JsonObject addMessage = provider.createObjectBuilder()
                .add("action", "drawNewPosition")
                .add("scores", new Gson().toJson(scores))
                .add("bodies", new Gson().toJson(bodySet))
                .build();
        return addMessage;
    }


    private void sendToSession(Session session, JsonObject message) {
        if (message != null) {
            Lock lock = sessionsLockMap.get(session);
            session.getAsyncRemote().sendText(message.toString());
        }
    }

    public void sendNewPositionsToClients() {
        sessionsLockMap.keySet().forEach(session -> sendToSession(session, generateNewPositionJSON()));
    }

    public void sendGameOver() {
        JsonProvider provider = JsonProvider.provider();

        JsonObject addMessage = provider.createObjectBuilder()
                .add("action", "gameOver")
                .build();
        sessionsLockMap.keySet().forEach(session -> sendToSession(session, addMessage));
    }


    public void clearSessionList(Session surviveSession) {
        sessionsLockMap.clear();
        sessionsLockMap.put(surviveSession, new ReentrantLock());
    }


    public GameController getGame() {
        return game;
    }
}