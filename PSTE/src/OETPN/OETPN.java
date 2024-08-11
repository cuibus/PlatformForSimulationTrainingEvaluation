package OETPN;

import OETPN.PlaceTypes.Token;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class OETPN extends OETPNBlackBox {
    // OETPN parameters
    Transition[] transitions;
    String[] placeNames;
    Token[] marking;
    boolean[][] pre, post;

    // simulation parameters
    private List<ExecutingTransition> inExecution;

    // execution helper parameters
    private Random random = new Random();
    public final int numberOfExecutionsBeforeRaisingInfiniteLoop = 20;

    public OETPN(String[] placeNames, Token[] initialMarking, boolean[][] pre, boolean[][] post, Transition[] transitions) {
        this.placeNames = placeNames;
        this.marking = initialMarking;
        this.pre = pre;
        this.post = post;
        this.transitions = transitions;

        this.validateOETPN();

        this.inExecution = new ArrayList<ExecutingTransition>();
    }

    public void addInputToken(String inputPlaceName, Token token) {
        // some validations
        int placeIndex = Arrays.asList(placeNames).indexOf(inputPlaceName);
        if (placeIndex < 0) throw new RuntimeException("Input place name " + inputPlaceName + " was not found.");
        for (int t = 0; t < post.length; t++) {
            if (post[t][placeIndex])
                throw new RuntimeException("Cannot add token in place " + placeIndex + " (because it is not an input place)");
        }

        this.marking[placeIndex] = token;
        if (this.isRunning) {
            this.step(EventType.input);
        }
    }

    public StepResult step(EventType event) {
        StepResult result = new StepResult();
        if (event == EventType.tic) {
            for (int t = 0; t < inExecution.size(); t++) {
                inExecution.get(t).delayRemaining--;
                if (inExecution.get(t).delayRemaining <= 0) {
                    finalizeTransition(indexOfTransition(inExecution.get(t).t), inExecution.get(t).input);
                    result.somethingWasExecuted = true;
                }
            }
            inExecution.removeIf(ie -> ie.delayRemaining <= 0);
        }

        boolean somethingWasExecuted;
        int numberOfExecutions = 0; // just to test infinite loop
        do {
            List<Integer> executables = getExecutableTransitionsIndexes();
            if (somethingWasExecuted = executables.size() > 0) {
                int toExecute = executables.get(random.nextInt(executables.size()));
                startTransition(toExecute);
                result.somethingWasExecuted = true;
                numberOfExecutions++;
            }
            if (numberOfExecutions >= numberOfExecutionsBeforeRaisingInfiniteLoop) {
                throw new RuntimeException("Infinite loop detected"); // TODO: print here something worthy
            }
        }
        while (somethingWasExecuted);
        return result;
    }


    private void validateOETPN() {
        Predicate<String[]> hasNonNullDuplicates = arr ->
                Arrays.stream(arr).filter(Objects::nonNull).distinct().count() !=
                        Arrays.stream(arr).filter(Objects::nonNull).count();

        if (hasNonNullDuplicates.test(this.placeNames)) {
            throw new RuntimeException("Duplicate place name found");
        }

        if (hasNonNullDuplicates.test(Arrays.stream(this.transitions).map(t -> t.name).toArray(String[]::new))) {
            throw new RuntimeException("Duplicate transition name found");
        }

        // check if output transitions have output arcs
        for (int i = 0; i < transitions.length; i++) {
            if (transitions[i] instanceof OutputTransition) {
                for (int j = 0; j < post[i].length; j++) {
                    if (post[i][j])
                        throw new RuntimeException("An output transition cannot have an output arc: transition " + transitions[i].getNameOrIndex(i));
                }
            }
        }

        // check if transitions without input have delays
        for (int i = 0; i < transitions.length; i++) {
            if (transitions[i].delay == 0) {
                boolean hasInputPlace = false;
                for (int j = 0; j < pre.length; j++) {
                    hasInputPlace |= pre[j][i];
                }
                if (!hasInputPlace) throw new RuntimeException("Transition " + transitions[i].getNameOrIndex(i) + " has no input and no delay.");
            }
        }
    }

    private void startTransition(int transitionIndex) {
        // extract from pre
        List<Token> extractedTokens = new ArrayList<>();
        for (int p = 0; p < marking.length; p++) {
            if (pre[p][transitionIndex]) {
                extractedTokens.add(marking[p]);
                marking[p] = null;
            }
        }
        if (transitions[transitionIndex].delay == 0) {
            finalizeTransition(transitionIndex, extractedTokens);
        } else {
            inExecution.add(new ExecutingTransition(transitions[transitionIndex], extractedTokens));
        }
    }

    private void finalizeTransition(int transitionIndex, List<Token> input) {
        List<Token> output = transitions[transitionIndex].grdMapPairs.get("default").apply(input);
        if (!(transitions[transitionIndex] instanceof OutputTransition)) {
            int writtenTokens = 0;
            for (int p = 0; p < marking.length; p++) {
                if (post[transitionIndex][p]) {
                    marking[p] = output.get(writtenTokens);
                    writtenTokens++;
                }
            }
        }
    }

    private List<Integer> getExecutableTransitionsIndexes() {
        List<Integer> result = new ArrayList<Integer>();
        for (int t = 0; t < transitions.length; t++) {
            if (isExecutable(t)) {
                result.add(t);
            }
        }
        return result;
    }

    private boolean isExecutable(int transitionIndex) {
        for (int p = 0; p < marking.length; p++) {
            if (pre[p][transitionIndex] && marking[p] == null) {
                return false;
            }
        }
        return true;
    }

    private int indexOfTransition(Transition t) {
        for (int i = 0; i < transitions.length; i++) {
            if (transitions[i] == t) {
                return i;
            }
        }
        throw new RuntimeException("Transition " + t + " was not found");
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("{");
        sb.append("Places: " + Arrays.stream(placeNames).collect(Collectors.joining(" ")) + ", ");
        sb.append("Transitions: " + Arrays.stream(transitions).map(Object::toString).collect(Collectors.joining(" ")) + ", ");
        sb.append("Marking: [" + Arrays.stream(marking).map((m) -> {
            return m == null ? "null" : m.toString();
        }).collect(Collectors.joining(" ")) + "], ");
        sb.append("InExecution: [" + inExecution.stream().map((ie) -> {
            return toStringExecution(ie);
        }).collect(Collectors.joining(" ")) + "], ");
        return sb.append("}").toString();
    }

    public String toStringExecution(ExecutingTransition inExecution) {
        return inExecution.t.getNameOrIndex(this.indexOfTransition(inExecution.t))
                + ":" + inExecution.delayRemaining + "(" +
                inExecution.input.stream().map(Token::toString).collect(Collectors.joining(" ")) + ")";
    }
}

class ExecutingTransition {
    public Transition t;
    public int delayRemaining;
    public List<Token> input;

    public ExecutingTransition(Transition t, List<Token> input) {
        this.t = t;
        this.delayRemaining = t.delay;
        this.input = input;
    }
}
