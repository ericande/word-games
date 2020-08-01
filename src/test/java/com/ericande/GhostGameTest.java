package com.ericande;

import com.ericande.ghost.BasicGhostGameStrategy;
import com.ericande.ghost.GhostGame;
import com.ericande.ghost.GhostGameState;
import org.apache.commons.collections4.Trie;
import org.apache.commons.collections4.trie.PatriciaTrie;
import org.hamcrest.core.StringContains;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Random;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GhostGameTest {
    private static final String FIRST = "1";
    private GhostGame theVictim;


    @Before
    public void setUp() throws Exception {
        theVictim = new GhostGame(testDictionary(), new BasicGhostGameStrategy(), new Random());
    }

    @Test
    public void challengesWhenLettersNotInDictionary() {
        theVictim.start();
        theVictim.move(FIRST);
        theVictim.move("c");
        assertEquals(GhostGameState.AWAITING_CHALLENGE_RESPONSE, theVictim.getGameState());
    }

    @Test
    public void selectsValidLetterWhenAvailable() {
        theVictim.start();
        theVictim.move(FIRST);
        theVictim.move("p");
        assertEquals("pi", theVictim.getLetters());
        assertTrue(theVictim.inProgress());
    }

    @Test
    public void gameOverWhenWordSpelled() {
        theVictim.start();
        theVictim.move(FIRST);
        theVictim.move("p");
        assertEquals("pi", theVictim.getLetters());
        assertTrue(theVictim.inProgress());
        theVictim.move("n");
        assertFalse(theVictim.inProgress());
    }

    @Test
    public void rejectsNonLowerCaseLetters() {
        theVictim.start();
        theVictim.move(FIRST);
        theVictim.move("p");
        assertEquals("pi", theVictim.getLetters());
        theVictim.move("P");
        assertEquals("pi", theVictim.getLetters());
        assertEquals(GhostGameState.PLAYING, theVictim.getGameState());
        theVictim.move("$");
        assertEquals("pi", theVictim.getLetters());
        assertEquals(GhostGameState.PLAYING, theVictim.getGameState());
    }

    @Test
    public void respondsToChallenge() {
        theVictim.start();
        theVictim.move(FIRST);
        theVictim.move("p");
        theVictim.move("s");
        //Responds to challenge with the word pistol and ends game
        assertThat(theVictim.move("!"), new StringContains("pistol"));
        assertFalse(theVictim.inProgress());
    }

    @Test
    public void picksLowerCaseLetterWhenGoesFirst() {
        Random myRandom = mock(Random.class);
        when(myRandom.nextInt(26)).thenReturn(15);
        GhostGame myVictim = new GhostGame(testDictionary(), new BasicGhostGameStrategy(), myRandom);
        myVictim.start();
        myVictim.move("second");
        assertEquals("p", myVictim.getLetters());
    }

    @Test
    public void acceptsMoveAsCurrentLettersPlusNew() {
        theVictim.start();
        theVictim.move(FIRST);
        theVictim.move("p");
        theVictim.move("pis");
        assertEquals("pist", theVictim.getLetters());
    }

    private Trie<String, Object> testDictionary() {
        Trie<String, Object> myTrie = new PatriciaTrie<>();
        Object PRESENT = new Object();
        String[] myTestWords = new String[] {
                "pin",
                "pit",
                "pint",
                "pistol",
                "pill"
        };
        Arrays.stream(myTestWords).forEach(aS -> myTrie.put(aS, PRESENT));
        return myTrie;
    }


}