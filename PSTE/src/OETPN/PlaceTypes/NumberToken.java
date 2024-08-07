package OETPN.PlaceTypes;

public class NumberToken implements Token {
    // should implement a token which has a number, just like UETPN
    // possible operations on this token: math operations, logic, etc
    // (the operation should be described similarly like a mapping function, check Example1, Example2, etc)

    //the float value after the mapping, should be converted to fuzzytoken as well

    float f;
    FuzzyToken v;
    float w;

    public float addFuzzy (float f, float w){
        return this.f+this.w + f*w;
        //fuzzy token?
    }

    public float subFuzzy (float f, float w){
        return this.f-this.w + f*w;
    }
    public float mulFuzzy (float f, float w){
        return this.f*this.w + f*w;
    }
    public float divFuzzy (float f, float w){
        return this.f/this.w + f*w;
    }


}
