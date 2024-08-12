package Component;

import OETPN.*;
import OETPN.PlaceTypes.Token;

public class Component extends RunnableModel {
    Component plant;
    Component controller;
    OETPN oetpn;

    public boolean isRunning;

    // input place type
    OutputTransition outputTransition;
    // output token type

    public static Component getSimpleComponent(TokenConsumer actionForOutputTransition) {
        // returns the simplest component, with input port connected directly to output, no delays
        Component simpleC = new Component(actionForOutputTransition);
        return simpleC;
    }

    private Component(TokenConsumer actionForOutputTransition) {
        this.plant = null;
        this.controller = null;
        this.outputTransition = new OutputTransition("T0", tokens -> {
            actionForOutputTransition.apply(tokens);
        });
        this.oetpn = new OETPN(
                new String[]{"P0"},
                new Token[]{null},
                new boolean[][]{{true}},
                new boolean[][]{{false}},
                new Transition[]{outputTransition}
        );
    }

    public Component(Token initialToken, Component plant, Component controller) {
        this.plant = plant;
        this.controller = controller;
        this.outputTransition = new OutputTransition("TX", tokens -> {
            System.out.println("output to out: " + tokens.get(0).toString());
        }); // print out the token for logging
        this.oetpn = getSimpleCommunicatorOetpn(initialToken);
    }

    private OETPN getSimpleCommunicatorOetpn(Token initialToken) {
        String[] placeNames = {"P0", "P1", "P2"};
        Token[] initialMarking = {
                null,
                initialToken,
                null
        };
        boolean[][] pre = {
                {false, false, true},
                {false, true, false},
                {true, false, false},
        };
        boolean[][] post = {
                {false, false, false},
                {false, false, false},
                {false, false, false}
        };
        Transition[] transitions = {
                new OutputTransition("T0", tokens -> {
                    plant.addInputToken(tokens.get(0));
                    System.out.println("output to Controller: " + tokens.get(0).toString());
                }), // print out the token for logging
                new OutputTransition("T1", tokens -> {
                    controller.addInputToken(tokens.get(0));
                    System.out.println("output to Plant: " + tokens.get(0).toString());
                }), // print out the token for logging
                this.outputTransition,
        };
        OETPN oetpn = new OETPN(placeNames, initialMarking, pre, post, transitions);
        return oetpn;
    }

    public void addInputToken(Token inputToken) {
        oetpn.addInputToken("P0", inputToken);
        if (this.isRunning) {
            oetpn.step(EventType.input);
        }
    }

    public StepResult step(EventType eventType) {
        StepResult result = new StepResult();
        if (isRunning) {
            return oetpn.step(eventType);
        } else {
            if (eventType == EventType.input) {
                do {
                    StepResult stepInputResult = oetpn.step(eventType);
                    StepResult stepPlantResult = plant == null ? null : plant.step(eventType);
                    StepResult stepToControllerResult = plant == null ? null : oetpn.step(eventType);
                    StepResult stepControllerResult = controller == null ? null : controller.step(eventType);
                    StepResult stepOutputResult = controller == null ? null : oetpn.step(eventType);

                    result.somethingWasExecuted = stepInputResult.somethingWasExecuted ||
                            (stepPlantResult != null && stepPlantResult.somethingWasExecuted) ||
                            (stepToControllerResult != null && stepToControllerResult.somethingWasExecuted) ||
                            (stepControllerResult != null && stepControllerResult.somethingWasExecuted) ||
                            (stepOutputResult != null && stepOutputResult.somethingWasExecuted);
                }
                while (result.somethingWasExecuted);
            } else {
                if (plant != null) {
                    plant.isRunning = true;
                }
                if (controller != null) {
                    controller.isRunning = true;
                }

                result = oetpn.step(eventType);
                // TODO: in this case the returned result must contain the result of plant and controller as well!
                if (plant != null) {
                    plant.isRunning = false;
                }
                if (controller != null) {
                    controller.isRunning = true;
                }
            }
            return result;
        }
    }

    public void run() {
        this.isRunning = true;
        this.oetpn.start();
        this.plant.start();
        this.controller.start();
    }

    public void halt() {
        this.isRunning = false;
        this.oetpn.halt();
        this.plant.halt();
        this.controller.halt();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("{");
        sb.append("Oetpn: " + oetpn.toString() + ", ");
        sb.append("Plant: " + (plant == null ? "null" : plant.toString()) + ", ");
        sb.append("Controller: " + (controller == null ? "null" : controller.toString()) + ", ");
        sb.append("Output transition name: " + outputTransition.name + " ");
        return sb.append("}").toString();
    }
}
