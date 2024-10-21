package OETPN.PlaceTypes;

public class ControllerInput<T,V> {
    public T reference;
    public V feedback;

    public ControllerInput(T reference, V feedback){
        this.reference = reference;
        this.feedback = feedback;
    }

    public String toString(){
        return this.reference + "|" + this.feedback;
    }
}
