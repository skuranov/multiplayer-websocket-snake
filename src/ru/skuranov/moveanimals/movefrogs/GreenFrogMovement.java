package ru.skuranov.moveanimals.movefrogs;


import ru.skuranov.GameController;
import ru.skuranov.moveanimals.FrogMovement;

import java.awt.*;


public class GreenFrogMovement extends FrogMovement {
    @Override
    public int getLifeCycle() {
        return 0;
    }

    @Override
    public Color getColor() {
        return new Color(65, 200, 67);
    }

    public GreenFrogMovement(GameController game) {
        super(game);
    }
}
