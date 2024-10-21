package OETPN;

import OETPN.PlaceTypes.Token;
import java.util.List;

public class OutputTransition extends Transition{

    // ouput transitions have delay=0 and are async,
    // they can consume only one token and must output nothing
    public OutputTransition(String name, TokenConsumer tokenConsumer){
        super(name, 0, (tokens) -> { tokenConsumer.apply(tokens.get(0)); return null;}, true);
    }

    public void addActionForOutputTransition(TokenConsumer tokenConsumer){
        TokenProcessor oldAction = this.tokenProcessor;
        TokenProcessor combinedAction = (tokens) -> {
            oldAction.apply(tokens);
            tokenConsumer.apply(tokens.get(0));
            return null;
        };
        // add the new action on top of the old one
        this.tokenProcessor = combinedAction;
    }

    // this feature allows saving an action of an output transition in order to be executed later
    private TokenProcessor actionSavedForLater = null;
    private List<Token> tokensSavedForLater = null;
    public void blockActionForLater(){
        this.actionSavedForLater = this.tokenProcessor;
        this.tokenProcessor = (tokens) -> {
            this.tokensSavedForLater = tokens; return null;
        };
    }

    public void doActionNowIfNeeded(){
        // sanity check
        if (actionSavedForLater == null) throw new RuntimeException("No action saved for later for " + this.toString());
        else {
            this.tokenProcessor = actionSavedForLater;
            if (tokensSavedForLater != null) { // else it means that the transition was not executed between blockActionForLater and now
                this.tokenProcessor.apply(tokensSavedForLater);
            }
            this.actionSavedForLater = null;
            this.tokensSavedForLater = null;
        }
    }
}
