package com.ericande;

class CachingGamePrinter {
    private String theEcho = "";

    void print(String aS) {
        System.out.println(aS);
        theEcho = aS;     //Only update echo on straightforward prints
    }

    void printAndEchoPrev(String aS) {
        System.out.println(aS);
        System.out.println(theEcho);
    }
}