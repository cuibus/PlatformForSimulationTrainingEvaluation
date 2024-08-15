package OETPN;

import java.util.HashMap;

public class Transition {
    public int delay = 0;
    public boolean isAsync = true;

    HashMap<String, TokenProcessor> grdMapPairs = new HashMap<>();
    public String name;
//    public Transition(int delay, String fuzzyTable){
//        this.delay = delay;
//        // TODO create a token processor which processes tokens according to fuzzyTable and call the other constructor
//    }

    public Transition(String name, int delay, TokenProcessor tokenProcessor, boolean isAsync){
        this.name = name;
        this.delay = delay;
        this.isAsync = isAsync;
        this.grdMapPairs.put("default", tokenProcessor);
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
}
