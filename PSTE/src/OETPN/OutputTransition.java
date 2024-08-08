package OETPN;

public class OutputTransition extends Transition{

    public OutputTransition(TokenConsumer tokenConsumer){
        super(0, (tokens) -> {tokenConsumer.apply(tokens);return null;});
    }

}
