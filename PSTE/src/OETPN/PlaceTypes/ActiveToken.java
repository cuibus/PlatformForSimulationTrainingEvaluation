package OETPN.PlaceTypes;


public abstract class ActiveToken extends Thread implements Token {

    OETPN child;
    public abstract void run();
    OETPN.run(); //one of the transitions can make the child stop or if a new oetpn token is placed.

}
