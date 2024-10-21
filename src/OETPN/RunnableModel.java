package OETPN;

import OETPN.PlaceTypes.Token;

public abstract class RunnableModel extends Thread implements Token {
    public final int maxSimulationHorizon = 200;
    protected boolean isRunning;

    public abstract void step(EventType event);

    public void run(){
        isRunning = true;
        int nrSteps = 0;
        while (isRunning && nrSteps < maxSimulationHorizon){
            step(EventType.tic);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) { }
        }
    }

    public void halt(){
        this.isRunning = false;
    }
}
