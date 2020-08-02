package com.ericande.ghost;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.SortedMap;

import static com.ericande.ghost.GhostGame.CHALLENGE;

public class BasicGhostGameStrategy implements GhostGameStrategy {
    private static final Random RANDOM = new Random();
    /*
    *Behavior: Picks a letter which continues but does not finish a valid word if one is available
    * If none are available, challenges.
     */
    @Override
    public char acceptMove(String aLetters, @NotNull SortedMap<String, Object> aPrefixMap) {
        List<Character> myCanAdd = new ArrayList<>();
        if (aPrefixMap.isEmpty()) {
            return CHALLENGE;
        } else {
            for (char myC = 'a'; myC <= 'z'; myC++) {
                if (!aPrefixMap.containsKey(aLetters + myC)) {
                    SortedMap<String, Object> myLetterSubMap = aPrefixMap.subMap(aLetters + myC, aLetters + (char) (myC + 1));
                    if (!myLetterSubMap.isEmpty()) {
                        for (String myS : myLetterSubMap.keySet()) {
                            if (myS.length() > aLetters.length() + 1) {
                                myCanAdd.add(myC);
                                break;
                            }
                        }
                    }
                }
            }

            if (myCanAdd.isEmpty()) {
                return CHALLENGE;
            } else {
                return myCanAdd.get(RANDOM.nextInt(myCanAdd.size()));
            }
        }
    }
}
