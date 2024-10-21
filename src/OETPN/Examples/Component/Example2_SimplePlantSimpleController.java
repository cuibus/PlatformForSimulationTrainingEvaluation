package OETPN.Examples.Component;

import Component.Component;
import OETPN.EventType;
import OETPN.PlaceTypes.NumberToken;

public class Example2_SimplePlantSimpleController {
    // create a simple Component in which the input token goes from input to controller, then to plant, then out, unchanged
    public static void main(String[] args) {
        // the simplest component used here only passes the token from input to output unchanged
        Component plant = Component.getSimplestComponentWithDelay("plant", token -> {
            System.out.println("Token out from plant: " + token.toString());
        });
        Component controller = Component.getSimplestComponent("controller", token -> {
            System.out.println("Token out from controller: " + token.toString());
        });
        Component component = new Component(plant, controller);
        component.setName("MainComponent");

        System.out.println("INITIAL");
        System.out.println(component.toString());

        for (int i = 0; i < 7; i++) {
            component.step(EventType.tic);
            System.out.println("\nStep " + i);
            System.out.println(component.toString());

            if (i == 2) {
                try {
                    Thread.sleep(100); // just an additional sleep to be "in between" ticks
                } catch (InterruptedException e) { }
                component.addInputToken(new NumberToken(5));
                component.step(EventType.input);
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) { }

        }
    }
}
