package com.ericande;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FileImporterTest {
    private static final int DICTIONARY_SIZE = 64662;
    private FileImporter theVictim;

    @Before
    public void setUp() throws Exception {
        theVictim = new FileImporter();
    }

    @Test
    public void canLoadWordTrie() {
        assertEquals(DICTIONARY_SIZE, theVictim.loadWordTrie().size());
    }

    @Test
    public void canLoadWordSet() {
        assertEquals(DICTIONARY_SIZE, theVictim.loadWordSet().size());
    }

    @Test
    public void canLoadWordList() {
        assertEquals(DICTIONARY_SIZE, theVictim.loadWordList().size());
    }
}