package OETPN.Examples;

import OETPN.EventType;
import OETPN.OETPN;
import OETPN.PlaceTypes.FuzzyToken;
import OETPN.PlaceTypes.Token;
import OETPN.Transition;
import OETPN.OutputTransition;

import java.util.List;

public class Example4 {
    public static void main(String[] args) {
        // oetpn: (P1,P2)->|->(P3)->|->(P4)->|, P2 is an input place, t4 is an output transition
        // with delays
        // initial marking: P1
        // no processing

        Class[] placeTypes = {
                FuzzyToken.class,
                FuzzyToken.class,
                FuzzyToken.class,
                FuzzyToken.class
        };
        Token[] initialMarking = {
                FuzzyToken.randomToken(),
                null,
                null,
                null
        };
        boolean[][] pre = {
                {true, false, false},
                {true, false, false},
                {false, true, false},
                {false, false, true}
        };
        boolean[][] post = {
                {false, false, true, false},
                {false, false, false, true},
                {false, false, false, false}
        };
        Transition[] transitions = {
                new Transition(0, tokens -> {return List.of(tokens.get(1));}), //copy the input token
                new Transition(3, tokens -> {return tokens;}),
                new OutputTransition(tokens -> {System.out.println("Output transition was executed: token ejected: " + tokens.get(0).toString());}),
        };

        OETPN oetpn = new OETPN(placeTypes, initialMarking, pre, post, transitions);
        new java.util.Timer().schedule(new java.util.TimerTask() {
                    @Override
                    public void run() {
                        oetpn.addInputToken(1, FuzzyToken.ZR);
                        oetpn.step(EventType.input);
                        System.out.println("Input added");
                        System.out.println(oetpn.toString());
                    }
                },
                1500
        );
        System.out.println("INITIAL");
        System.out.println(oetpn.toString());

        for (int i = 0; i < 7; i++) {
            oetpn.step(EventType.tic);
            System.out.println("Step " + i);
            System.out.println(oetpn.toString());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) { }
        }
    }
}
