package OETPN.Examples.OETPN;

import OETPN.*;
import OETPN.PlaceTypes.*;

public class Example1_SimplePN {
    public static void main(String[] args) {
        // oetpn: (P1)->|->(P2)->|->(P3)
        // with delays
        // initial marking: P1
        // no processing, just copying tokens

        String[] placeNames = { "P1", "P2", "P3" };
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
                new Transition("T0", 1, tokens -> { return tokens; }),
                new Transition("T1", 2, tokens -> { return tokens; })
        };

        OETPN oetpn = new OETPN(placeNames, initialMarking, pre, post, transitions);
        System.out.println("INITIAL");
        System.out.println(oetpn.toString());

        for (int i=0;i<5;i++){
            oetpn.step(EventType.tic);
            System.out.println("Step " + i);
            System.out.println(oetpn.toString());
        }
    }
}
