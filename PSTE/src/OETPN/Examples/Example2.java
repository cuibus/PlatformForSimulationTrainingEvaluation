package OETPN.Examples;

import OETPN.EventType;
import OETPN.OETPN;
import OETPN.PlaceTypes.FuzzyToken;
import OETPN.PlaceTypes.Token;
import OETPN.Transition;

import java.util.Arrays;
import java.util.List;

public class Example2 {
    public static void main(String[] args) {
        // oetpn: (P1)->|->(P2,P3)->|->(P5)
        //              |->(P4)->|(P6)
        // with delays
        // initial marking: P1
        // no processing, just copying tokens

        Class[] placeTypes = {
                FuzzyToken.class,
                FuzzyToken.class,
                FuzzyToken.class,
                FuzzyToken.class,
                FuzzyToken.class,
                FuzzyToken.class
        };
        Token[] initialMarking = {
                FuzzyToken.randomToken(),
                null,
                null,
                null,
                null,
                null
        };
        boolean[][] pre = {
                {true, false, false},
                {false, true, false},
                {false, true, false},
                {false, false, true},
                {false, false, false},
                {false, false, false},
        };
        boolean[][] post = {
                {false,  true,  true, true, false, false},
                {false, false, false, false,  true, false},
                {false, false, false, false, false,  true},
        };
        Transition[] transitions = {
                new Transition(1, tokens -> { return List.of(tokens.get(0), tokens.get(0), tokens.get(0)); }), // gets 1 token, returns 3
                new Transition(1, tokens -> { return List.of(tokens.get(0)); }), // gets 2 tokens, returns 1
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
