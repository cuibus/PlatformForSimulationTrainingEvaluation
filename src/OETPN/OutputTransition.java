package OETPN;

public class OutputTransition extends Transition{

    // ouput transitions have delay=0 and are async
    public OutputTransition(String name, TokenConsumer tokenConsumer){
        super(name, 0, (tokens) -> {tokenConsumer.apply(tokens);return null;}, true);
    }

}
