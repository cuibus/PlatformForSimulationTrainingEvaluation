package OETPN.Examples.OETPN;

import OETPN.EventType;
import OETPN.OETPN;
import OETPN.OutputTransition;
import OETPN.PlaceTypes.NumberToken;
import OETPN.PlaceTypes.Token;
import OETPN.Transition;

import java.util.List;

//implement the equation:
// x(k+1) =a.x(k) +b.u(k); -> OETPN
//x(0)=0.1;
// a=-0.3; b=0.2;u(k)=0.5

public class Example6_EquationInOETPN {
    final static double a = -0.3;
    final static double b = 0.2;
    public static void main(String[] args) {
        OutputTransition tout = new OutputTransition("Tout", (tokens) -> {
            System.out.println("output from transition: " + tokens.get(0).toString());
        });

        String[] placeNames = { "Pu", "Px", "Px+1", "P3"};
        Token[] initialMarking = {
                null,
                new NumberToken(0.1),
                null,
                null
        };
        boolean[][] pre = {
                {true, false, false}, // Pu
                {true, false, false}, // Px
                {false, true, false}, // Px+1
                {false, false, true}  // P3
                // t1    t[1]   tout
        };
        boolean[][] post = {
                {false, false, true, false}, // t1
                {false, true, false, true}, // t[1]
                {false, false, false, false} // tout
                // Pu     Px   Px+1    P3
        };
        Transition[] transitions = {
                new Transition("T1", 0, tokens -> {
                    double uk = ((NumberToken)tokens.get(0)).nr;
                    double xk = ((NumberToken)tokens.get(1)).nr;
                    double xkp1 = a * xk + b * uk;
                    return List.of(new NumberToken(xkp1)); }), //this is the actual equation
                new Transition("T[1]", 1, tokens -> {return List.of(tokens.get(0), tokens.get(0));}),
                tout
        };

        OETPN oetpn = new OETPN(placeNames, initialMarking, pre, post, transitions);

        System.out.println("INITIAL");
        System.out.println(oetpn.toString());

        for (int i = 0; i < 10; i++) {
            oetpn.addInputToken("Pu", new NumberToken(0.5));
            oetpn.step(EventType.tic);
            System.out.println("\nStep " + i);
            System.out.println(oetpn.toString());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) { }
        }
    }
}
