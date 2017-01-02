package ru.skuranov.drawable;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;


public class Body {
    private CopyOnWriteArrayList<Integer> coordX;
    private CopyOnWriteArrayList<Integer> coordY;

    public Body(){
        coordX = new CopyOnWriteArrayList<>();
        coordY = new CopyOnWriteArrayList<>();
    }

    public CopyOnWriteArrayList<Integer> getCoordY() {
        return coordY;
    }

    public void setCoordY(CopyOnWriteArrayList<Integer> coordY) {
        this.coordY = coordY;
    }

    public CopyOnWriteArrayList<Integer> getCoordX() {
        return coordX;
    }

    public void setCoordX(CopyOnWriteArrayList<Integer> coordX) {
        this.coordX = coordX;
    }

    public ArrayList<Integer[]> getArrayView(){
        ArrayList <Integer[]> arrayBody = new ArrayList<>();
        for(int i = 0; i < getCoordX().size(); i++){
            Integer[] innerAray = new Integer[2];
            innerAray[0]=coordX.get(i);
            innerAray[1]=coordY.get(i);
            arrayBody.add(innerAray);
        }
        return arrayBody;
    }
}
