package com.ericande.ghost;

import org.jetbrains.annotations.NotNull;

import java.util.SortedMap;

@FunctionalInterface
public interface GhostGameStrategy {
    //Returns character to add, or '!' for challenge
    char acceptMove(String aLetters, @NotNull SortedMap<String, Object> aPrefixMap);
}
