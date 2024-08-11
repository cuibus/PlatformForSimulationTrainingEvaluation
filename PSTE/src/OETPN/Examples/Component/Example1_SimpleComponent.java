package OETPN.Examples.Component;

import Component.Component;
import OETPN.EventType;
import OETPN.OETPN;
import OETPN.OutputTransition;
import OETPN.PlaceTypes.FuzzyToken;
import OETPN.PlaceTypes.NumberToken;
import OETPN.PlaceTypes.Token;
import OETPN.Transition;

import java.util.List;

public class Example1_SimpleComponent {
    public static void main(String[] args) {
        Component plant = Component.getSimpleComponent(tokens -> {
            System.out.println("Token out from plant: " + tokens.get(0).toString());
        });

        System.out.println("INITIAL");
        System.out.println(plant.toString());

        for (int i = 0; i < 7; i++) {
            plant.step(EventType.tic);
            System.out.println("\nStep " + i);
            System.out.println(plant.toString());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) { }

            if (i == 2) {
                plant.addInputToken(new NumberToken(5));
                plant.step(EventType.input);
            }
        }
    }
}
