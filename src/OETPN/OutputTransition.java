package OETPN;

public class OutputTransition extends Transition{

    public OutputTransition(String name, TokenConsumer tokenConsumer){
        super(name, 0, (tokens) -> {tokenConsumer.apply(tokens);return null;});
    }

}
