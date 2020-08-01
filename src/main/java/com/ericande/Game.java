package com.ericande;

public interface Game {
    String start();

    //move should assume inputs are normalized to lower case
    String move(String aMove);

    void quit();

    String rules();

    boolean inProgress();
}
