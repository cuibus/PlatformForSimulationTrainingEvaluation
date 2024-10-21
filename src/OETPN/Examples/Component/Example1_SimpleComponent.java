package OETPN.Examples.Component;

import Component.Component;
import OETPN.EventType;
import OETPN.PlaceTypes.NumberToken;

public class Example1_SimpleComponent {
    // creates the simplest component, which only takes the input and passes it to the output

    public static void main(String[] args) {

        Component plant = Component.getSimplestComponent("plant", token -> {
            System.out.println("Token out from plant: " + token.toString());
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
