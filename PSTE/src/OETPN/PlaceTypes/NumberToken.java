package OETPN.PlaceTypes;

public class NumberToken implements Token {
    public double nr;

    public NumberToken(double nr){
        this.nr = nr;
    }

    public String toString(){
        return nr+"";
    }
}
