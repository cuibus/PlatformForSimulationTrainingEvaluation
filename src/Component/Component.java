package Component;

import OETPN.*;
import OETPN.PlaceTypes.ControllerInput;
import OETPN.PlaceTypes.Token;
import OETPN.PlaceTypes.WrapperToken;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Component extends RunnableModel {
    Component plant;
    Component controller;
    OETPN oetpn; // TODO: make this a RunnableModel, not necessarily an oetpn. This way, students can really write code that programs distributed components, without getting into OETPN with absolute necessity. (might be useful to test RT aspects of DCS)
    OutputTransition outputTransition;

    public String name;
    public boolean isRunning;

    private Component(String name, OETPN oetpn, Component controller, Component plant){
        this.name = name;
        this.controller = controller;
        this.plant = plant;
        this.oetpn = oetpn;
        this.outputTransition = (OutputTransition)oetpn.getTransitionByName("Tout");
    }

    public static Component getSimplestComponentWithDelay(String name, TokenConsumer actionForOutputTransition){
        // returns the simplest component, with input port connected directly to output, a single delay of [1]
        Transition outputTransition = new OutputTransition("Tout", tokens -> {
            actionForOutputTransition.apply(tokens);
        });
        OETPN oetpn = new OETPN(
                new String[]{"Pin", "P1"},
                new Token[]{null, null},
                new boolean[][] {{true, false}, {false, true}},
                new boolean[][] {{false, true}, {false, false}},
                new Transition[]{ new Transition("T1", 1, (tokens) -> { return tokens;}), outputTransition}
        );
        return new Component(name, oetpn, null, null);
    }

    public static Component getSimplestComponent(String name, TokenConsumer actionForOutputTransition) {
        // returns the simplest component, with input port connected directly to output, no delays
        Transition outputTransition = new OutputTransition("Tout", tokens -> {
            actionForOutputTransition.apply(tokens);
        });
        OETPN oetpn = new OETPN(
                new String[]{"Pin"},
                new Token[]{null},
                new boolean[][]{{true}},
                new boolean[][]{{false}},
                new Transition[]{outputTransition}
        );
        return new Component(name, oetpn, null, null);
    }

    public Component(Component plant, Component controller) {
        // returns the standard component, in which:
        // the plant receives a WrapperToken with the input from input port and the output of the controller
        // the input token is not consumed in this process
        // the controller receives the output from the plant
        // the controller output also is sent via the output port

        this.plant = plant;
        this.plant.outputTransition.addActionForOutputTransition((token) -> {
            System.out.println("plant->oetpn: " + token.toString());
            this.oetpn.addInputToken("P2", token);
        });
        this.controller = controller;
        this.controller.outputTransition.addActionForOutputTransition((token) -> {
            System.out.println("controller->oetpn: " + token.toString());
            this.oetpn.addInputToken("P1", token);
        });
        this.outputTransition = new OutputTransition("Tout", token -> {
            System.out.println("oetpn->out: " + token.toString());
        }); // print out the token for logging
        this.oetpn = getSimpleCommunicatorOetpn();
    }

    private OETPN getSimpleCommunicatorOetpn() {
        String[] placeNames = {"Pin", "P1", "P2", "P3", "P4", "P5"};
        Token initialToken = new Token() {
            public String toString() { return "InitToken"; } // token used only for starting the loop
        };
        Token[] initialMarking = {null, null, null, null, initialToken, null};
        boolean[][] pre = {
                {false, false, false, true, false}, // Pin
                {false, true, false, false, false}, // P1
                {false, false, false, false, true}, // P2
                {true, false, false, false, false}, // P3
                {false, false, false, true, false}, // P4
                {false, false, true, false, false}, // P5
                //T0     T1     Tout   T3     T4
        };
        boolean[][] post = {
                {false, false, false, false, false, false}, // T0
                {false, false, false, false, false, false}, // T1
                {false, false, false, false, false, false}, // Tout
                {true, false, false, true, false, false}, // T3
                {false, false, false, false, true, true}, // T4
                // Pin     P1    P2     P3     P4     P5
        };
        Transition[] transitions = {
                new OutputTransition("T0", token -> {
                    System.out.println("oetpn->controller: " + token.toString());
                    controller.addInputToken(token);
                }), // print out the token for logging
                new OutputTransition("T1", token -> {
                    System.out.println("oetpn->plant: " + token.toString());
                    plant.addInputToken(token);
                }), // print out the token for logging
                this.outputTransition,
                new Transition("T3", 0, tokens -> {
                    return List.of(
                            tokens.get(0), // inject the input token back (reference doesn't change)
                            new WrapperToken<>(new ControllerInput(tokens.get(0), tokens.get(1)))
                    );
                }),
                new Transition("T4", 0, tokens -> {
                    return List.of(
                            tokens.get(0),
                            tokens.get(0)
                    );
                })
        };
        OETPN oetpn = new OETPN(placeNames, initialMarking, pre, post, transitions);
        return oetpn;
    }

    public void addInputToken(Token inputToken) {
        oetpn.addInputToken("Pin", inputToken);
        if (this.isRunning) {
            oetpn.step(EventType.input);
        }
    }

    public void step(EventType eventType) {
        if (isRunning) {
            oetpn.step(eventType);
        } else {
            if (eventType == EventType.input) {
                startComponentsForStep();
                oetpn.step(eventType);
                stopComponentsAfterStep();
            } else {
                if (plant!=null) plant.outputTransition.blockActionForLater();
                if (controller!= null) controller.outputTransition.blockActionForLater();

                if (plant!=null) plant.step(eventType);
                if (controller!= null)controller.step(eventType);
                oetpn.step(eventType);

                startComponentsForStep();
                if (plant!=null) plant.outputTransition.doActionNowIfNeeded();
                if (controller!= null)controller.outputTransition.doActionNowIfNeeded();
                stopComponentsAfterStep();
            }
        }
    }

    private void startComponentsForStep() {
        this.isRunning = true;
        if (plant != null) {
            plant.isRunning = true;
        }
        if (controller != null) {
            controller.isRunning = true;
        }
        AtomicInteger loopIterations = new AtomicInteger();
        this.outputTransition.addActionForTransition(() -> {
            loopIterations.getAndIncrement(); // count the number of iterations that the component does
            if (loopIterations.get() > OETPN.numberOfExecutionsBeforeRaisingInfiniteLoop)
                throw new RuntimeException("Infinite loop detected at component " + this.toString());
        });
    }

    private void stopComponentsAfterStep() {
        this.isRunning = false;
        if (plant != null) {
            plant.isRunning = false;
        }
        if (controller != null) {
            controller.isRunning = true;
        }
        this.outputTransition.popActionForTransition();
    }

    public void run() {
        this.isRunning = true;
        this.plant.start();
        this.controller.start();
        this.oetpn.start();
    }

    public void halt() {
        this.isRunning = false;
        this.oetpn.halt();
        this.controller.halt();
        this.plant.halt();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("{"+name+": ");
        sb.append("Oetpn: " + oetpn.toString() + ", ");
        sb.append("Plant: " + (plant == null ? "null" : plant.toString()) + ", ");
        sb.append("Controller: " + (controller == null ? "null" : controller.toString()) + ", ");
        sb.append("Output transition name: " + outputTransition.name + " ");
        return sb.append("}").toString();
    }
}
