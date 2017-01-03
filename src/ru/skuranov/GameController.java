package ru.skuranov;

import ru.skuranov.move.Movement;
import ru.skuranov.move.AppleMovement;
import ru.skuranov.move.SnakeMovement;
import ru.skuranov.move.moveapples.SimpleAppleMovement;

import javax.websocket.Session;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GameController {

    private final HashMap<String, Integer> baseParams = new HashMap<>();

    private boolean active;

    private Random random = new Random();

    private CopyOnWriteArraySet<AppleMovement> appleMovements = new CopyOnWriteArraySet<>();
    private ExecutorService executor;
    private ConcurrentHashMap<Session, SnakeMovement> sessionSnakeMovementsMap = new ConcurrentHashMap<>();
    private CopyOnWriteArraySet<Movement> movementsSet = new CopyOnWriteArraySet<>();
    DataSender dataSender;

    {
        Properties prop = new Properties();
        InputStream input = null;
        try {
            String filename = "SnakeGame.properties";
            input = GameController.class.getClassLoader().getResourceAsStream(filename);
            if (input == null) {
                Logger.getLogger(GameController.class.getName())
                        .log(Level.SEVERE, "Sorry, unable to find " + filename, new Exception());
            }

            prop.load(input);

            this.setBaseParams(prop.getProperty("width"),
                    prop.getProperty("height"),
                    prop.getProperty("snakeLenth"),
                    prop.getProperty("appleCount"),
                    prop.getProperty("gameSpeed"));

        } catch (IOException ex) {
            ex.printStackTrace();
            Logger.getLogger(GameController.class.getName())
                    .log(Level.SEVERE, "Cannor read properties", new Exception(ex));
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    Logger.getLogger(GameController.class.getName())
                            .log(Level.SEVERE, "Cannot close input stream", new Exception(e));
                }
            }
        }
    }

    public void setBaseParams(String width,
                              String height,
                              String snakeLenth,
                              String appleCount,
                              String gameSpeed) {
        baseParams.put("width", Integer.parseInt(width));
        baseParams.put("height", Integer.parseInt(height));
        baseParams.put("snakeLenth", Integer.parseInt(snakeLenth));
        baseParams.put("appleCount", Integer.parseInt(appleCount));
        baseParams.put("gameSpeed", Integer.parseInt(gameSpeed));
    }

    public HashMap<String, Integer> getBaseParams() {
        return baseParams;
    }

    public CopyOnWriteArraySet<AppleMovement> getAppleMovements() {
        return appleMovements;
    }

    public ExecutorService getExecutor() {
        return executor;
    }

    public void startGame(Session session) {
        active = true;
        executor = Executors.newFixedThreadPool(20);
        SnakeMovement snakeMovement = new SnakeMovement(this);
        sessionSnakeMovementsMap.put(session, snakeMovement);
        executor.submit(snakeMovement);
        appleMovements = new CopyOnWriteArraySet<>();
        for (int i = 0; i < this.baseParams.get("appleCount"); i++) {
            AppleMovement appleMovement = getNewApple();
            appleMovements.add(appleMovement);
            executor.submit(appleMovement);
        }
        movementsSet.addAll(appleMovements);
        movementsSet.add(snakeMovement);
        dataSender = new DataSender(this);
        executor.submit(dataSender);
    }

    public void runAdditionalSnake(Session session) {
        SnakeMovement snakeMovement = new SnakeMovement(this);
        executor.submit(snakeMovement);
        sessionSnakeMovementsMap.put(session, snakeMovement);
        movementsSet.add(snakeMovement);
    }


    public AppleMovement getNewApple() {
        return new SimpleAppleMovement(this);
    }

    public Random getRandom() {
        return random;
    }

    public void stopGame() {
        active = false;
        sessionSnakeMovementsMap.clear();
        appleMovements.clear();
        movementsSet.clear();
        dataSender = null;
        this.executor.shutdownNow();
    }

    public ConcurrentHashMap<Session, SnakeMovement> getSessionSnakeMovementsMap() {
        return sessionSnakeMovementsMap;
    }

    public boolean isActive() {
        return active;
    }

    public CopyOnWriteArraySet<Movement> getMovementsSet() {
        return movementsSet;
    }
}
