package ru.skuranov.move.moveapples;


import ru.skuranov.GameController;
import ru.skuranov.move.AppleMovement;

import java.awt.*;


public class SimpleAppleMovement extends AppleMovement {
    @Override
    public int getLifeCycle() {
        return 0;
    }

    @Override
    public Color getColor() {
        return new Color(65, 200, 67);
    }

    public SimpleAppleMovement(GameController game) {
        super(game);
    }
}
