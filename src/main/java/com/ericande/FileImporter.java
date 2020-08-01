package com.ericande;

import org.apache.commons.collections4.Trie;
import org.apache.commons.collections4.trie.PatriciaTrie;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class FileImporter {
    public static final String COMMON_NOUNS_TXT = "src\\main\\resources\\CommonNouns.txt";
    public static final String ALL_WORDS_TXT = "src\\main\\resources\\AllWords.txt";
    private static final Predicate<String> DEFAULT_FILTER = aS -> true;
    private String theFileName;
    private Predicate<String> theFilter;

    public FileImporter() {
        theFileName = ALL_WORDS_TXT;
        theFilter = DEFAULT_FILTER;
    }

    public FileImporter setFileName(String aFileName) {
        theFileName = aFileName;
        return this;
    }

    public FileImporter setFilter(Predicate<String> aFilter) {
        theFilter = aFilter;
        return this;
    }

    public Trie<String, Object> loadWordTrie() {
        Trie<String, Object> myTrie = new PatriciaTrie<>();
        Object PRESENT = new Object();
        loadWords(aS -> myTrie.put(aS, PRESENT));
        return myTrie;
    }

    public Set<String> loadWordSet() {
        Set<String> mySet = new HashSet<>();
        loadWords(mySet::add);
        return mySet;
    }

    public List<String> loadWordList() {
        List<String> myList = new ArrayList<>();
        loadWords(myList::add);
        return myList;
    }

    private void loadWords(Consumer<String> aStringConsumer) {
        try (BufferedReader myBufferedReader = new BufferedReader(new FileReader(theFileName))) {
            for (String myLine = myBufferedReader.readLine();
                 myLine != null;
                 myLine = myBufferedReader.readLine()) {
                if (theFilter.test(myLine)) {
                    char myLastChar = myLine.charAt(myLine.length() - 1);
                    if (Character.isLetter(myLastChar)) {
                        aStringConsumer.accept(myLine);
                    } else {
                        aStringConsumer.accept(myLine.substring(0, myLine.length() - 1));
                    }
                }
            }
        } catch (IOException aE) {
            System.out.println("ERROR: Problem reading file at: " + theFileName);
            System.out.println(new File(theFileName).getAbsolutePath());
            aE.printStackTrace();
        }
    }
}
