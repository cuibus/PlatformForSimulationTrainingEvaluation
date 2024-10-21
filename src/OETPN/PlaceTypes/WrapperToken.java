package OETPN.PlaceTypes;

public class WrapperToken<T> implements Token {
    T value;

    public WrapperToken(T value){
        this.value = value;
    }

    public String toString(){
        return value.toString();
    }
}
