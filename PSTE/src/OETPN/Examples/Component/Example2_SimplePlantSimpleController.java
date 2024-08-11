package OETPN.Examples.Component;

import Component.Component;
import OETPN.EventType;
import OETPN.PlaceTypes.NumberToken;

public class Example2_SimplePlantSimpleController {
    public static void main(String[] args) {
        Component plant = Component.getSimpleComponent(tokens -> {
            System.out.println("Token out from plant: " + tokens.get(0).toString());
        });
        Component controller = Component.getSimpleComponent(tokens -> {
            System.out.println("Token out from controller: " + tokens.get(0).toString());
        });
        Component component = new Component(plant, controller);

        System.out.println("INITIAL");
        System.out.println(component.toString());

        for (int i = 0; i < 5; i++) {
            component.step(EventType.tic);
            System.out.println("\nStep " + i);
            System.out.println(component.toString());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) { }

            if (i == 2) {
                component.addInputToken(new NumberToken(5));
                component.step(EventType.input);
            }
        }
    }
}
