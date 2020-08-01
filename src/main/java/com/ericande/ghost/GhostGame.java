package com.ericande.ghost;

import com.ericande.FileImporter;
import com.ericande.Game;
import com.ericande.Main;
import org.apache.commons.collections4.Trie;

import java.util.Random;
import java.util.SortedMap;

public class GhostGame implements Game {
    private static final String GIVE_UP = "give up";
    private final Random theRandom;
    private final Trie<String, Object> theDictionary;
    private final GhostGameStrategy theStrategy;
    private GhostGameState theGameState;
    private StringBuilder theLetters;

    public GhostGame() {
        this(new FileImporter().setFilter(GhostGame::validWordForGhost).loadWordTrie(),
                new BasicGhostGameStrategy(),
                new Random());
    }

    public GhostGame(Trie<String, Object> aDictionary, GhostGameStrategy aStrategy, Random aRandom) {
        theDictionary = aDictionary;
        theStrategy = aStrategy;
        theRandom = aRandom;
        theGameState = GhostGameState.GAME_OVER;
        theLetters = new StringBuilder();
    }

    @Override
    public String start() {
        theGameState = GhostGameState.AWAITING_FIRST_OR_SECOND;
        theLetters = new StringBuilder();
        return "Let's play Ghost! Type \"show rules\" for the rules of the game Do you want to go first or second?" +
                "\n\t1. First" +
                "\n\t2. Second";
    }

    @Override
    public String move(String aMove) {
        if (theGameState == GhostGameState.AWAITING_FIRST_OR_SECOND) {
            if (aMove.length() > 0) {
                switch (aMove.charAt(0)) {
                    case '1':
                    case 'f':
                        theGameState = GhostGameState.PLAYING;
                        theLetters = new StringBuilder();
                        return "Go ahead and pick the first letter then!";
                    case '2':
                    case 's':
                        theGameState = GhostGameState.PLAYING;
                        String myMove = firstMove();
                        theLetters = new StringBuilder(myMove);
                        return formatMove(myMove);
                }
            }
            return "Sorry, I don't understand you. Choose whether you go first" +
                    "\n 1. First" +
                    "\n 2. Second";
        } else if (theGameState == GhostGameState.PLAYING) {
            if (legalMove(aMove)) {
                if (isChallenge(aMove)) {
                    SortedMap<String, Object> myPrefixMap = theDictionary.prefixMap(theLetters.toString());
                    if (myPrefixMap.isEmpty()) {
                        theGameState = GhostGameState.GAME_OVER;
                        return "I've got nothing - you win!";
                    } else {
                        theGameState = GhostGameState.GAME_OVER;
                        return myPrefixMap.firstKey() + " starts with the letters " + theLetters.toString() +
                                "\nYou lost as your challenge was answered.";
                    }
                } else {
                    if (aMove.length() > 1) {
                        aMove = aMove.substring(aMove.length() - 1);
                    }
                    theLetters.append(aMove);
                    if (theDictionary.containsKey(theLetters.toString())) {
                        theGameState = GhostGameState.GAME_OVER;
                        return "That spells " + theLetters.toString() + ", you lose!";
                    }
                    SortedMap<String, Object> myPrefixMap = theDictionary.prefixMap(theLetters.toString());
                    char myMove = theStrategy.acceptMove(theLetters.toString(), myPrefixMap);
                    if (isChallenge(String.valueOf(myMove))) {
                        return aiChallenge();
                    } else {
                        theLetters.append(myMove);
                        return "The computer picked \'" + myMove + "\'" +
                                "\nThe letters are now: " + theLetters.toString() + ". Your move!";
                    }
                }
            } else {
                return "That wasn't a valid move - try again or type \"" +
                        Main.SHOW_HELP +
                        "\" or \"" +
                        Main.SHOW_RULES +
                        "\" for help!";
            }
        } else { //theGameState == GameState.AWAITING_CHALLENGE_RESPONSE
            if (aMove.equals(GIVE_UP)) {
                theGameState = GhostGameState.GAME_OVER;
                return "";
            }
            if (!aMove.startsWith(theLetters.toString())) {
                return "That doesn't start with the letters \"" +
                        theLetters.toString() +
                        "\"! Try again, or type \"" +
                        GIVE_UP +
                        "\" to quit.";
            } else {
                if (theDictionary.containsKey(aMove)) {
                    theGameState = GhostGameState.GAME_OVER;
                    return "Yep, that works. You win!";
                } else {
                    theGameState = GhostGameState.GAME_OVER;
                    return "I don't know that word, but if you say so...";
                }
            }
        }
    }

    private boolean legalMove(String aMove) {
        if (isChallenge(aMove)) {
            return true;
        } else if (aMove.length() == 1 && Character.isLowerCase(aMove.charAt(0))) {
            return true;
        } else {
            return aMove.length() == theLetters.length() + 1 &&
                    aMove.startsWith(theLetters.toString()) &&
                    Character.isLowerCase(aMove.charAt(theLetters.length()));
        }
    }

    private boolean isChallenge(String aMove) {
        return aMove.equals("!") || aMove.equals("challenge");
    }

    private String firstMove() {
        //Pick any letter with equal probability
        return String.valueOf((char) ('a' + theRandom.nextInt(26)));
    }

    @Override
    public void quit() {
        theGameState = GhostGameState.GAME_OVER;
    }

    @Override
    public String rules() {
        return RULES_DESC;
    }

    @Override
    public boolean inProgress() {
        return theGameState != GhostGameState.GAME_OVER;
    }

    public GhostGameState getGameState() {
        return theGameState;
    }

    public String getLetters() {
        return theLetters.toString();
    }

    private String formatMove(String aMove) {
        return "The computer picked \'" + aMove + "\'!\n" +
                "Current letters are: " + theLetters.toString() +
                "\nYour move!";
    }

    private String aiChallenge() {
        theGameState = GhostGameState.AWAITING_CHALLENGE_RESPONSE;
        return "The computer decided to challenge you!" +
                "\nCurrent letters are " + theLetters.toString() +
                ".\nDo you have a word? (Or type \"" + GIVE_UP + "\" if you don't have one!";
    }

    private static boolean validWordForGhost(String aWord) {
        return aWord.length() > 2;
    }

    private static final String RULES_DESC =
            "Ghost consists of any number of players taking turns adding letters\n" +
                    "to the end of a sequence to spell out an english word.\n\n" +
                    "Players are trying to AVOID finishing a valid word while forcing their opponent to finish a word\n" +
                    "A valid move consists of either adding a letter, or alternately challenging the previous\n" +
                    "player, asserting that there are no english words starting with those letters.\n\n" +
                    "The game ends either when a player's move spells a valid word of 3 or more characters, or on a challenge.\n" +
                    "The challenged player loses if they cannot come up with a word, but wins if they can!\n\n" +
                    "Play examples:" +
                    "\n\tP1: G" +
                    "\n\tP2: A" +
                    "\n\tP1: S - Player 1 loses, as GAS is an english word" +
                    "\n\n\tP1: G" +
                    "\n\tP2: A" +
                    "\n\tP1: Q" +
                    "\n\tP2: Challenge! - Player 1 wins if they can come up with a word starting with GAQ, else they lose" +
                    "\n\n\tP1: G" +
                    "\n\tP2: O - Game continues, only words of 3 or more letters count" +
                    "\n\tP1: T - Player 1 loses, as while GO was only 2 letters, GOT is 3 and long enough to count";

}
