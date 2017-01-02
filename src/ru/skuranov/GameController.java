package ru.skuranov;

import ru.skuranov.moveanimals.AnimalMovement;
import ru.skuranov.moveanimals.FrogMovement;
import ru.skuranov.moveanimals.SnakeMovement;
import ru.skuranov.moveanimals.movefrogs.GreenFrogMovement;

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

public class GameController {

    private final HashMap<String, Integer> baseParams = new HashMap<>();

    private boolean active;

    private Random random = new Random();

    private CopyOnWriteArraySet<FrogMovement> frogMovements = new CopyOnWriteArraySet<>();
    private ExecutorService executor;
    //Number of threads in executor service = number of apples + maximum number of snakes on the field at the same time
    private ConcurrentHashMap<Session, SnakeMovement> sessionSnakeMovementsMap = new ConcurrentHashMap<>();
    private CopyOnWriteArraySet<AnimalMovement> movementsSet = new CopyOnWriteArraySet<>();

    {
        Properties prop = new Properties();
        InputStream input = null;
        try {
            String filename = "SnakeGame.properties";
            input = GameController.class.getClassLoader().getResourceAsStream(filename);
            if (input == null) {
                System.out.println("Sorry, unable to find " + filename);
            }

            prop.load(input);

            this.setBaseParams(prop.getProperty("width"),
                    prop.getProperty("height"),
                    prop.getProperty("snakeLenth"),
                    prop.getProperty("frogCount"),
                    prop.getProperty("gameSpeed"));

        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void setBaseParams(String width,
                              String height,
                              String snakeLenth,
                              String frogCount,
                              String gameSpeed) {
        baseParams.put("width", Integer.parseInt(width));
        baseParams.put("height", Integer.parseInt(height));
        baseParams.put("snakeLenth", Integer.parseInt(snakeLenth));
        baseParams.put("frogCount", Integer.parseInt(frogCount));
        baseParams.put("gameSpeed", Integer.parseInt(gameSpeed));
    }

    public HashMap<String, Integer> getBaseParams() {
        return baseParams;
    }

    public CopyOnWriteArraySet<FrogMovement> getFrogMovements() {
        return frogMovements;
    }

    public ExecutorService getExecutor() {
        return executor;
    }

    public void startGame(Session session) {
        active = true;
        executor = Executors.newFixedThreadPool(10);
        SnakeMovement snakeMovement = new SnakeMovement(this);
        sessionSnakeMovementsMap.put(session, snakeMovement);
        executor.submit(snakeMovement);
        frogMovements = new CopyOnWriteArraySet<>();
        for (int i = 0; i < this.baseParams.get("frogCount"); i++) {
            FrogMovement frogMovement = getNewFrog();
            frogMovements.add(frogMovement);
            executor.submit(frogMovement);
        }
        movementsSet.addAll(frogMovements);
        movementsSet.add(snakeMovement);
    }

    public void runAdditionalSnake(Session session) {
        SnakeMovement snakeMovement = new SnakeMovement(this);
        executor.submit(snakeMovement);
        sessionSnakeMovementsMap.put(session, snakeMovement);
        movementsSet.add(snakeMovement);
    }


    public FrogMovement getNewFrog() {
        return new GreenFrogMovement(this);
    }

    public Random getRandom() {
        return random;
    }

    public void stopGame() {
        active = false;
        sessionSnakeMovementsMap.clear();
        frogMovements.clear();
        movementsSet.clear();
        this.executor.shutdown();
    }

    public ConcurrentHashMap<Session, SnakeMovement> getSessionSnakeMovementsMap() {
        return sessionSnakeMovementsMap;
    }

    public boolean isActive() {
        return active;
    }

    public CopyOnWriteArraySet<AnimalMovement> getMovementsSet() {
        return movementsSet;
    }
}
