package ru.skuranov.direction;

public class Direction {

    private static final int[] left = {-1, 0};
    private static final int[] right = {1, 0};
    private static final int[] up = {0, -1};
    private static final int[] down = {0, 1};

    public int[] getCurDir() {
        return curDir;
    }

    private int[] curDir = Direction.down;

    public void toLeft() {
        if (curDir == left) {
            curDir = down;
        } else if (curDir == right) {
            curDir = up;
        } else if (curDir == up) {
            curDir = left;
        } else if (curDir == down) {
            curDir = right;
        }
    }

    public void toRight() {
        if (curDir == left) {
            curDir = up;
        } else if (curDir == right) {
            curDir = down;
        } else if (curDir == up) {
            curDir = right;
        } else if (curDir == down) {
            curDir = left;
        }
    }

    public void setLeft() {
        curDir = left;
    }

    public void setRight() {
        curDir = right;
    }

    public void setUp() {
        curDir = up;
    }

    public void setDown() {
        curDir = down;
    }

}