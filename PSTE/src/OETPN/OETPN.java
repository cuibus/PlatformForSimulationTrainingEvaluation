package OETPN;

import OETPN.PlaceTypes.Token;

import java.util.*;
import java.util.stream.Collectors;

public class OETPN extends Thread {
    // OETPN parameters
    Transition[] transitions;
    Class[] placeTypes;
    Token[] marking;
    boolean[][] pre, post;

    // execution helper parameters
    private boolean running;
    private Random random = new Random();
    public final int numberOfExecutionsBeforeRaisingInfiniteLoop = 20;

    // simulation parameters
    private List<ExecutingTransition> inExecution;

    public OETPN(Class[] placeTypes, Token[] initialMarking, boolean[][] pre, boolean [][] post, Transition[] transitions){
        this.placeTypes = placeTypes;
        this.marking = initialMarking;
        this.pre = pre;
        this.post = post;
        this.transitions = transitions;
        for (int i=0;i<transitions.length;i++){
            transitions[i].name = i+"";
            // just a safety check
            if (transitions[i] instanceof OutputTransition){
                for (int j=0;j<post[i].length;j++){
                    if (post[i][j]) throw new RuntimeException("An output transition cannot have an output arc: transition " + i);
                }
            }
        }

        this.inExecution = new ArrayList<ExecutingTransition>();
    }

    public void addInputToken(int placeIndex, Token token){
        //TODO: rewrite this to add placeName rather than placeIndex (must add place names with default values for all places)
        for (int t=0;t<post.length;t++){
            if (post[t][placeIndex]) throw new RuntimeException("Cannot add token in place " + placeIndex + " (because it is not an input place)");
        }
        this.marking[placeIndex] = token;
    }

    public void step(EventType event) {
        if (event == EventType.tic){
            for (int t=0;t<inExecution.size();t++){
                inExecution.get(t).delayRemaining--;
                if (inExecution.get(t).delayRemaining <= 0){
                    finalizeTransition(indexOfTransition(inExecution.get(t).t), inExecution.get(t).output);
                }
            }
            inExecution.removeIf(ie -> ie.delayRemaining <=0);
        }

        boolean executablesExist = true;
        int numberOfExecutions = 0; // just to test infinite loop
        while (executablesExist) {
            List<Integer> executables = getExecutableTransitionsIndexes();
            executablesExist = executables.size() > 0;
            if(executablesExist) {
                int toExecute = executables.get(random.nextInt(executables.size()));
                startTransition(toExecute);
                numberOfExecutions++;
            }
            if (numberOfExecutions >= numberOfExecutionsBeforeRaisingInfiniteLoop){
                throw new RuntimeException("Infinite loop detected"); // TODO: print here something worthy
            }
        }
    }

    public void startTransition(int transitionIndex){
        // extract from pre
        List<Token> extractedTokens = new ArrayList<>();
        for (int p = 0; p < marking.length; p++) {
            if (pre[p][transitionIndex]) {
                extractedTokens.add(marking[p]);
                marking[p] = null;
            }
        }
        List<Token> output = transitions[transitionIndex].grdMapPairs.get("default").apply(extractedTokens);

        if (transitions[transitionIndex].delay == 0){
            if (!(transitions[transitionIndex] instanceof OutputTransition)) {
                finalizeTransition(transitionIndex, output);
            }
        }
        else {
            inExecution.add(new ExecutingTransition(transitions[transitionIndex], output));
        }
    }

    private void finalizeTransition(int transitionIndex, List<Token> output){
        int writtenTokens=0;
        for (int p = 0; p < marking.length; p++) {
            if (post[transitionIndex][p]) {
                marking[p] = output.get(writtenTokens);
                writtenTokens++;
            }
        }
    }

    private List<Integer> getExecutableTransitionsIndexes() {
        List<Integer> result = new ArrayList<Integer>();
        for (int t=0;t<transitions.length;t++){
            if (isExecutable(t)){
                result.add(t);
            }
        }
        return result;
    }

    private boolean isExecutable(int transitionIndex){
        for (int p = 0; p < marking.length; p++) {
            if (pre[p][transitionIndex] && marking[p] == null) {
                return false;
            }
        }
        return true;
    }

    private int indexOfTransition(Transition t){
        for (int i=0;i<transitions.length;i++){
            if (transitions[i] == t){
                return i;
            }
        }
        throw new RuntimeException("Transition " + t + " was not found");
    }

    public void run(){
        running = true;
        while (running){
            step(EventType.tic);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) { }
        }
    }

    public void halt(){
        this.running = false;
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("PlaceTypes: " + Arrays.stream(placeTypes).map(Object::toString).collect(Collectors.joining(" ")) + "\n");
        sb.append("Transitions: " + Arrays.stream(transitions).map(Object::toString).collect(Collectors.joining(" ")) + "\n");
        sb.append("Marking: " + Arrays.stream(marking).map((m) -> {return m==null ? "null":m.toString();}).collect(Collectors.joining(" ")) + "\n");
        sb.append("InExecution: " + inExecution.stream().map((ie) -> { return toStringExecution(ie); }).collect(Collectors.joining(" ")) + "\n");
        return sb.toString();
    }

    public String toStringExecution(ExecutingTransition inExecution){
        return "T" + this.indexOfTransition(inExecution.t) + ":" + inExecution.delayRemaining +
                inExecution.output.stream().map(Token::toString).collect(Collectors.joining(" "));
    }
}

class ExecutingTransition {
    public Transition t;
    public int delayRemaining;
    public List<Token> output;
    public ExecutingTransition(Transition t, List<Token> output){
        this.t = t;
        this.delayRemaining = t.delay;
        this.output = output;
    }
}
