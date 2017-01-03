package ru.skuranov.move;

import ru.skuranov.direction.Direction;
import ru.skuranov.GameController;
import ru.skuranov.move.moveapples.SimpleAppleMovement;
import ru.skuranov.websocket.EventSessionHandler;

import java.util.List;

public class SnakeMovement extends Movement {
    private int snakeLenght;
    private boolean flInc;
    private int score;

    public SnakeMovement(GameController game) {
        super(game);

        setDirection(new Direction());
        this.snakeLenght = game.getBaseParams().get("snakeLenth");

        for (int i = 0; i < game.getBaseParams().get("snakeLenth"); i++) {
            getBody().getCoordX().add(1);
            getBody().getCoordY().add(game.getBaseParams().get("snakeLenth") - i - 1);
        }
    }

    public int getSnakeLenght() {
        return snakeLenght;
    }

    @Override
    public Object call() {
        while (true) try {
            Thread.sleep(1000 / getGame().getBaseParams().get("gameSpeed"));
            move();
        } catch (InterruptedException e) {
            return null;
        }
    }


    @Override
    public void move() {
        List<Integer> snakeBodyX = getBody().getCoordX();
        List<Integer> snakeBodyY = getBody().getCoordY();
        if (flInc) {
            snakeLenght++;
            getBody().getCoordX().add(1);
            getBody().getCoordY().add(1);
            flInc = false;
        }

        for (int i = snakeLenght - 1; i >= 0; i--) {
            if (i >= 1) {//Moving snake's body
                snakeBodyX.set(i, snakeBodyX.get(i - 1));
                snakeBodyY.set(i, snakeBodyY.get(i - 1));
            } else {
                snakeBodyX.set(i, snakeBodyX.get(i + 1) + getDirection().getCurDir()[0]);//Moving snake's head
                snakeBodyY.set(i, snakeBodyY.get(i + 1) + getDirection().getCurDir()[1]);
                if (snakeBodyX.get(0) < 0) {
                    snakeBodyX.set(0, getGame().getBaseParams().get("width") - 1);
                } //"Transparent walls"
                if (snakeBodyY.get(0) < 0) {
                    snakeBodyY.set(0, getGame().getBaseParams().get("height") - 1);
                }
                if (snakeBodyX.get(0) > getGame().getBaseParams().get("width") - 1) {
                    snakeBodyX.set(0, 0);
                }

                if (snakeBodyY.get(0) > getGame().getBaseParams().get("height") - 1) {
                    snakeBodyY.set(0, 0);
                }

                for (AppleMovement appleMovement : getGame().getAppleMovements()) {//Eating frogs by snake
                    if ((snakeBodyX.get(i) == appleMovement.getBody().getCoordX().get(0)) && (snakeBodyY.get(i) ==
                            appleMovement.getBody().getCoordY().get(0))) {
                        if (appleMovement instanceof SimpleAppleMovement) {
                            flInc = true;
                            score++;
                        }
                        appleMovement.cancel();
                        getGame().getAppleMovements().remove(appleMovement);
                        getGame().getMovementsSet().remove(appleMovement);
                    }
                }

                while (getGame().getAppleMovements().size() < getGame().getBaseParams().get("appleCount")) {//AppleMovement respawning
                    AppleMovement appleMovement = getGame().getNewApple();
                    getGame().getAppleMovements().add(appleMovement);
                    getGame().getMovementsSet().add(appleMovement);
                    getGame().getExecutor().submit(appleMovement);
                }

                for (int j = 1; j < snakeLenght; j++) {//Exit by eating itself
                    if ((snakeBodyX.get(j) == snakeBodyX.get(0)) && (snakeBodyY.get(j) == snakeBodyY.get(0))) {
                        EventSessionHandler.getInstance().sendGameOver();
                        getGame().stopGame();
                        return;
                    }
                }


            }
        }

        for (SnakeMovement snake : getGame().getSessionSnakeMovementsMap().values()) {
            if (!snake.equals(this) && checkBiteAnotherSnake(snake) != null) {
                if (checkBiteAnotherSnake(snake)==0){
                    EventSessionHandler.getInstance().sendGameOver();
                    getGame().stopGame();
                    break;
                }
                snake.losePart(checkBiteAnotherSnake(snake));
            }
        }
    }


    private Integer checkBiteAnotherSnake(SnakeMovement snake) {
        Integer breakPoint = null;
        List<Integer[]> anotherSnakeCoords = snake.getBody().getArrayView();
        for (int i = 0; i < anotherSnakeCoords.size(); i++) {
            if (anotherSnakeCoords.get(i)[0].equals(getBody().getCoordX().get(0))
                    && anotherSnakeCoords.get(i)[1].equals(getBody().getCoordY().get(0))) {
                breakPoint = i;
            }
        }
        return breakPoint;
    }

    private void losePart(Integer breakPoint) {
        score = score - (snakeLenght - breakPoint);
        for (int i = getBody().getCoordX().size() - 1; i > breakPoint; i--) {
            getBody().getCoordX().remove(i);
            getBody().getCoordY().remove(i);
        }
        snakeLenght = getBody().getCoordX().size();
    }

    public int getScore() {
        return score;
    }
}
