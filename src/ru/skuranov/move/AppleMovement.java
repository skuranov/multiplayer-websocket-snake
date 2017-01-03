package ru.skuranov.moveanimals;


import ru.skuranov.direction.Direction;
import ru.skuranov.GameController;


import java.awt.*;

public abstract class AppleMovement extends Movement {

    protected Direction direction;

    protected int cycleCounter;
    private boolean isCancelled;

    public abstract int getLifeCycle();

    abstract public Color getColor();


    public void incCycleCount() {
        cycleCounter++;
    }

    public int getCycleCount() {
        return cycleCounter;
    }


    public AppleMovement(GameController gameController) {
        super(gameController);
        direction = new Direction();
        cycleCounter = 0;
        getBody().getCoordX().add(gameController.getRandom().nextInt(gameController.getBaseParams().get("width") - 1) + 1);
        getBody().getCoordY().add(gameController.getRandom().nextInt(gameController.getBaseParams().get("height") - 1) + 1);
    }

    @Override
    public void move() {
        int changeDIrection = getGame().getRandom().nextInt(10);

        if (changeDIrection == 0) {
            direction.toLeft();
        } else if (changeDIrection == 1) {
            direction.toRight();
        }


        if (getBody().getCoordX().get(0) + direction.getCurDir()[0] > 0 &&
                getBody().getCoordX().get(0) + direction.getCurDir()[0] <
                        getGame().getBaseParams().get("width") - 1) {
            getBody().getCoordX().set(0, getBody().getCoordX().get(0) + direction.getCurDir()[0]);
        }


        if (getBody().getCoordY().get(0) + direction.getCurDir()[1] > 0 &&
                getBody().getCoordY().get(0) + direction.getCurDir()[1] <
                        getGame().getBaseParams().get("height") - 1) {
            getBody().getCoordY().set(0, getBody().getCoordY().get(0) + direction.getCurDir()[1]);
        }

    }


    @Override
    public Object call() {
        while (true) {
            try {
                Thread.sleep(5000 / getGame().getBaseParams().get("gameSpeed"));
                move();
            } catch (InterruptedException e) {
                break;
            }
        }
        return null;
    }


    public void cancel() {
        isCancelled = true;
    }
}
