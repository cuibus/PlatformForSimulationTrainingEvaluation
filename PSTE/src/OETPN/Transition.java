package OETPN;

import java.util.HashMap;

public class Transition {
    public int delay = 0;
    HashMap<String, TokenProcessor> grdMapPairs = new HashMap<>();
    public String name;
//    public Transition(int delay, String fuzzyTable){
//        this.delay = delay;
//        // TODO create a token processor which processes tokens according to fuzzyTable and call the other constructor
//    }

    public Transition(int delay, TokenProcessor tokenProcessor){
        this.delay = delay;
        this.grdMapPairs.put("default", tokenProcessor);
    }

    public Transition(int delay, TokenProcessor tokenProcessor, OETPN Child, int index){
        this.delay = delay;
        this.grdMapPairs.put("default", tokenProcessor);
        if Child.running == false;
        getToken (OETPN.marking [index]);
    }

    public Token getToken ( Token t){
        return t;
    }

    public String toString(){
        return "T" + name + ":" + this.delay;
    }
}
