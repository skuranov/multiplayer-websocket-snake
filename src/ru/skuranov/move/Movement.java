package ru.skuranov.moveanimals;

import ru.skuranov.direction.Direction;
import ru.skuranov.drawable.Body;
import ru.skuranov.GameController;

import java.util.concurrent.Callable;

public abstract class Movement implements Callable {
    private GameController game;
    private Body body;
    private Direction direction;

    public Movement(GameController game) {
        this.game = game;
        this.body = new Body();
        this.direction = new Direction();
    }

    public Body getBody() {
        return body;
    }

    public GameController getGame() {
        return game;
    }

    public abstract void move();

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }
}