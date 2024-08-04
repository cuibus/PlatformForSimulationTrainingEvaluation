package OETPN.Examples;

import OETPN.*;
import OETPN.PlaceTypes.*;

public class Example1 {
    public static void main(String[] args) {
        // oetpn: (P1)->|->(P2)->|->(P3)
        // with delays
        // initial marking: P1
        // no processing, just copying tokens

        Class[] placeTypes = {
                FuzzyToken.class,
                FuzzyToken.class,
                FuzzyToken.class
        };
        Token[] initialMarking = {
                FuzzyToken.randomToken(),
                null,
                null
        };
        boolean[][] pre = {
                {true, false},
                {false, true},
                {false, false}
        };
        boolean[][] post = {
                {false, true, false},
                {false, false, true}
        };
        Transition[] transitions = {
                new Transition(1, tokens -> { return tokens; }),
                new Transition(2, tokens -> { return tokens; })
        };

        OETPN oetpn = new OETPN(placeTypes, initialMarking, pre, post, transitions);
        System.out.println("INITIAL");
        System.out.println(oetpn.toString());

        for (int i=0;i<5;i++){
            oetpn.step(EventType.tic);
            System.out.println("Step " + i);
            System.out.println(oetpn.toString());
        }
    }
}
