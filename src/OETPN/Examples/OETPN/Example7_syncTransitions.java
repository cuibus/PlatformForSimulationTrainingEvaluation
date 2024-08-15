package OETPN.Examples.OETPN;

import OETPN.EventType;
import OETPN.OETPN;
import OETPN.OutputTransition;
import OETPN.PlaceTypes.NumberToken;
import OETPN.PlaceTypes.Token;
import OETPN.Transition;

import java.util.List;

public class Example7_syncTransitions {
    public static void main(String[] args) {
        // (P0)->|->(P1)->|->(P2)->|->(P3)->|->(P4)
        String[] placeNames = { "P0", "P1", "P2", "P3", "P4"};
        Token[] initialMarking = {
                new NumberToken(0.1),
                null,
                null,
                null,
                null
        };
        boolean[][] pre = {
                {true, false, false, false},
                {false, true, false, false},
                {false, false, true, false},
                {false, false, false, true},
                {false, false, false, false}
        };
        boolean[][] post = {
                {false, true, false, false, false},
                {false, false, true, false, false},
                {false, false, false, true, false},
                {false, false, false, false, true},
        };
        Transition[] transitions = {
                new Transition("T0", 0, tokens -> { System.out.println("T0 executed");return tokens; }, false),
                new Transition("T1", 0, tokens -> { System.out.println("T1 executed");return tokens; }, false),
                new Transition("T2", 0, tokens -> { System.out.println("T2 executed");return tokens; }, true),
                new Transition("T3", 1, tokens -> { System.out.println("T3 executed");return tokens; }, false),
        };

        OETPN oetpn = new OETPN(placeNames, initialMarking, pre, post, transitions);

        System.out.println("INITIAL");
        System.out.println(oetpn.toString());

        for (int i = 0; i < 10; i++) {
            oetpn.step(EventType.tic);
            System.out.println("\nStep " + i);
            System.out.println(oetpn.toString());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) { }
        }
    }
}
