package OETPN.Examples;

import OETPN.*;
import OETPN.PlaceTypes.*;

import java.util.List;

public class Example5_ActiveTokens {
    public static void main(String[] args) {
        // oetpn: (P1)->|->(P2)->|->(P3)    // parent is initialized with a token containing a number
        //                 /  \
        //                (P4)->|->(P5)->|->(P4)    - child net is executed, child adds +1 on each transition, is initialized with the value which comes from P1
        // with delays
        // initial marking: P1
        // no processing
        TokenProcessor childTp = (tokens) -> {
            System.out.println("child transition started execution. Value is: " + ((NumberToken) tokens.get(0)).nr);
            return List.of(new NumberToken(
                    ((NumberToken) tokens.get(0)).nr + 1));
        };
        OETPN childOetpn = new OETPN(
                new Class[]{Number.class, Number.class, Number.class},
                new Token[]{new NumberToken(1), null, null},
                new boolean[][]{{true, false}, {false, true}, {false, false}},
                new boolean[][]{{false, true, false}, {false, false, true}},
                new Transition[]{new Transition(1, childTp), new Transition(1, childTp)}
        );

        OETPN parentOetpn = new OETPN(
                new Class[]{OETPN.class, Number.class, Number.class},
                new Token[]{childOetpn, null, null},
                new boolean[][]{{true, false}, {false, true}, {false, false}},
                new boolean[][]{{false, true, false}, {false, false, true}},
                new Transition[]{
                        new Transition(2, tokens -> {
                            ((OETPN) tokens.get(0)).start();
                            return List.of(new NumberToken(0));
                        }),
                        new Transition(2, tokens -> {
                            return tokens;
                        })}
        );

        System.out.println("INITIAL");
        System.out.println(parentOetpn.toString());

        for (int i = 0;i < 7; i++) {
            parentOetpn.step(EventType.tic);
            System.out.println("Step " + i);
            System.out.println(parentOetpn.toString());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
        }
    }
}
