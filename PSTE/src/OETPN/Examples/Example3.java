package OETPN.Examples;

import OETPN.EventType;
import OETPN.OETPN;
import OETPN.PlaceTypes.FuzzyToken;
import OETPN.PlaceTypes.Token;
import OETPN.Transition;
import Utils.FuzzyUtils;
import core.FuzzyPetriLogic.Tables.OneXOneTable;
import core.FuzzyPetriLogic.Tables.TwoXOneTable;
import core.TableParser;

import java.util.List;

public class Example3 {
    public static void main(String[] args) {
        // oetpn: (P1,P2)->|->(P3)->|->(P4)
        // with delays
        // initial marking: P1
        // some processing, T0 is an adder, T1 is an inverter

        Class[] placeTypes = {
                FuzzyToken.class,
                FuzzyToken.class,
                FuzzyToken.class,
                FuzzyToken.class
        };
        Token[] initialMarking = {
                FuzzyToken.NL,
                FuzzyToken.PM,
                null,
                null
        };
        boolean[][] pre = {
                {true, false},
                {true, false},
                {false, true},
                {false, false}
        };
        boolean[][] post = {
                {false, false, true, false},
                {false, false, false, true}
        };
        Transition[] transitions = {
                new Transition(1, tokens -> {
                    TwoXOneTable table = new TableParser().parseTwoXOneTable(FuzzyUtils.adder2x1);
                    core.FuzzyPetriLogic.FuzzyToken[] result = table.execute(new FuzzyToken[]{
                            (FuzzyToken) tokens.get(0),
                            (FuzzyToken) tokens.get(1)
                    });
                    return List.of(new FuzzyToken(result[0]));
                }),
                new Transition(1, tokens -> {
                    OneXOneTable table = new TableParser().parseOneXOneTable(FuzzyUtils.inverter1x1);
                    core.FuzzyPetriLogic.FuzzyToken[] result = table.execute(new FuzzyToken[]{
                            (FuzzyToken) tokens.get(0),
                    });
                    return List.of(new FuzzyToken(result[0]));
                })
        };

        OETPN oetpn = new OETPN(placeTypes, initialMarking, pre, post, transitions);
        System.out.println("INITIAL");
        System.out.println(oetpn.toString());

        for (int i = 0; i < 5; i++) {
            oetpn.step(EventType.tic);
            System.out.println("Step " + i);
            System.out.println(oetpn.toString());
        }
    }
    }
