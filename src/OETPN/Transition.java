package OETPN;

import java.util.Stack;

public class Transition {
    public int delay = 0;
    public boolean isAsync = true;

    TokenProcessor tokenProcessor;
    Stack<Runnable> additionalActions;

    public String name;

    public Transition(String name, int delay, TokenProcessor tokenProcessor, boolean isAsync){
        this.name = name;
        this.delay = delay;
        this.isAsync = isAsync;
        this.tokenProcessor = tokenProcessor;
        this.additionalActions = new Stack<>();
    }

    public Transition(String name, int delay, TokenProcessor tokenProcessor){
        this(name, delay, tokenProcessor, true);
    }

    public Transition(int delay, TokenProcessor tokenProcessor) {
        this(null, delay, tokenProcessor, true);
    }

    public Transition(int delay, TokenProcessor tokenProcessor, boolean isAsync) {
        this(null, delay, tokenProcessor, isAsync);
    }


    public String toString(){
        return (name != null ? name : "T") + ":" + this.delay;
    }

    public String getNameOrIndex(int index){
        return name != null ? name : "T_"+index;
    }

    public void addActionForTransition(Runnable action){
        this.additionalActions.push(action);
    }

    public Runnable popActionForTransition(){
        return this.additionalActions.pop();
    }
}
