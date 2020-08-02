package com.ericande.ghost;

import org.jetbrains.annotations.NotNull;

import java.util.SortedMap;

public class MinimaxGhostGameStrategy implements GhostGameStrategy {
    private static final GhostGameStrategy theFallbackStrategy = new BasicGhostGameStrategy();
    private SortedMap<String, Object> thePrefixMap;

    @Override
    public char acceptMove(String aLetters, @NotNull SortedMap<String, Object> aPrefixMap) {
        thePrefixMap = aPrefixMap;
        if (aPrefixMap.isEmpty()) {
            return GhostGame.CHALLENGE;
        } else {
            for (char myC = 'a'; myC <= 'z'; myC++) {
                if (!canWin(aLetters + myC)) {
                    return myC;  //Just return first winning move, don't bother finding all possible ones
                }
            }
            return theFallbackStrategy.acceptMove(aLetters, aPrefixMap);
        }
    }

    private boolean canWin(String aS) {
        if (thePrefixMap.containsKey(aS) ||
                thePrefixMap.tailMap(aS).isEmpty() ||
                !thePrefixMap.tailMap(aS).firstKey().startsWith(aS)) {
            return true; //Already won
        } else {
            for (char myC = 'a'; myC <= 'z'; myC++) {
                String myTry = aS + myC;
                if (!thePrefixMap.subMap(myTry, aS + (char) (myC+1)).isEmpty()) {
                    if (!canWin(myTry)) {
                        return true;
                    }
                }
            }
            return false;
        }
    }
}
