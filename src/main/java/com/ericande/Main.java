package com.ericande;

import com.ericande.ghost.GhostGame;

import java.util.*;
import java.util.function.Supplier;

public class Main {
    private static final Scanner theScanner = new Scanner(System.in);
    private static final Map<String, Supplier<Game>> theGames = gameChoices();
    private static final Set<String> theQuitCommands = quitCommands();
    public static final String SHOW_HELP = "show help";
    public static final String SHOW_RULES = "show rules";
    public static final String SWITCH_GAME = "switch game";
    public static final String RESTART_GAME = "restart game";
    public static final String SHUT_DOWN = "shut down";
    public static final String GOODBYE = "Goodbye";


    public static void main(String[] args) {
        CachingGamePrinter myPrinter = new CachingGamePrinter();
        myPrinter.print(WELCOME_MESSAGE);
        Game myGame = null;
        while (true) {
            if (myGame == null) {
                myGame = selectGame();
                if (myGame == null) {
                    return;
                }
                myPrinter.print(myGame.start());
            }
            String myLine = theScanner.nextLine().toLowerCase();
            switch (myLine) {
                case SHOW_HELP:
                    myPrinter.printAndEchoPrev(HELP_MESSAGE);
                    break;
                case SHOW_RULES:
                    myPrinter.printAndEchoPrev(myGame.rules());
                    break;
                case RESTART_GAME:
                    myPrinter.print(myGame.start());
                    break;
                case SWITCH_GAME:
                    myGame.quit();
                    myGame = selectGame();
                    if (myGame == null) {
                        return;
                    }
                    myPrinter.print(myGame.start());
                    break;
                case SHUT_DOWN:
                    myGame.quit();
                    myPrinter.print(GOODBYE);
                    return;
                default:
                    if (myGame.inProgress()) {
                        String myMoveResult = myGame.move(myLine);
                        myPrinter.print(myMoveResult);
                        if (!myGame.inProgress()) {
                            myPrinter.print("Good game! Type \"" +
                                    RESTART_GAME +
                                    "\" to play again, or \""
                                    + SWITCH_GAME
                                    + "\" to try another game.");
                        }
                    } else {
                        myPrinter.print("I didn't recognize that, sorry! " + HELP_MESSAGE);
                    }
            }
        }
    }

    private static Game selectGame() {
        String myLine;
        System.out.println(GAME_CHOICES);
        while (!theQuitCommands.contains(myLine = theScanner.nextLine().toLowerCase())) {
            if (theGames.containsKey(myLine)) {
                return theGames.get(myLine).get();
            } else {
                System.out.println("I didn't recognize that game, try again or type \"exit\" to quit\n" + GAME_CHOICES);
            }
        }
        System.out.println("Goodbye!");
        return null;
    }

    private static Map<String, Supplier<Game>> gameChoices() {
        Map<String, Supplier<Game>> myMap = new HashMap<>();
        myMap.put("1", GhostGame::new);
        myMap.put("ghost", GhostGame::new);
        return myMap;
    }

    private static Set<String> quitCommands() {
        Set<String> myQuitCommands = new HashSet<>();
        myQuitCommands.add("q");
        myQuitCommands.add("quit");
        myQuitCommands.add("x");
        myQuitCommands.add("exit");
        myQuitCommands.add("");
        myQuitCommands.add("shut down");
        return myQuitCommands;
    }
    private static final String GAME_CHOICES = "Choose a game to play\n" +
            "1. Ghost";
    private static final String WELCOME_MESSAGE = "Welcome!";
    private static final String HELP_MESSAGE = "The following are valid commands:\n" +
            SHOW_HELP + "\n" +
            SHOW_RULES + "\n" +
            SWITCH_GAME + "\n" +
            RESTART_GAME + "\n" +
            SHUT_DOWN;
}
