package ru.skuranov;

import ru.skuranov.websocket.EventSessionHandler;

import java.util.concurrent.Callable;

/**
 * Created by Zivert on 1/3/2017.
 */
public class DataSender implements Callable {

    GameController game;

    public DataSender(GameController game) {
        this.game = game;
    }

    @Override
    public Object call() throws Exception {
        while (true) {
            try {
                Thread.sleep(1000 / game.getBaseParams().get("gameSpeed"));
                EventSessionHandler.getInstance().sendNewPositionsToClients();
            } catch (InterruptedException e) {
                return null;
            }
        }
    }
}
