package OETPN.Examples.OETPN;

import OETPN.EventType;
import OETPN.OETPN;
import OETPN.PlaceTypes.FuzzyToken;
import OETPN.PlaceTypes.Token;
import OETPN.Transition;

import java.util.List;

public class Example2_ParallelTransitions {
    public static void main(String[] args) {
        // oetpn: (P1)->|->(P2,P3)->|->(P5)
        //              |->(P4)->|(P6)
        // with delays
        // initial marking: P1
        // no processing, just copying tokens

        String[] placeNames = { "P1", "P2", "P3", "P4", "P5", "P6" };
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
                new Transition("T1", 1, tokens -> { return List.of(tokens.get(0), tokens.get(0), tokens.get(0)); }), // gets 1 token, returns 3
                new Transition("T2", 1, tokens -> { return List.of(tokens.get(0)); }), // gets 2 tokens, returns 1
                new Transition("T3" , 2, tokens -> { return tokens; })
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
