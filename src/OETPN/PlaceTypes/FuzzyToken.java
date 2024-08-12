package OETPN.PlaceTypes;

import core.FuzzyPetriLogic.FuzzyValue;

public class FuzzyToken extends core.FuzzyPetriLogic.FuzzyToken implements Token {

    public FuzzyToken(double NL, double NM, double ZR, double PM, double PL){
        super(NL, NM, ZR, PM, PL);
    }

    public FuzzyToken(core.FuzzyPetriLogic.FuzzyToken token){
        super(token.getFuzzyValue(FuzzyValue.NL),
                token.getFuzzyValue(FuzzyValue.NM),
                token.getFuzzyValue(FuzzyValue.ZR),
                token.getFuzzyValue(FuzzyValue.PM),
                token.getFuzzyValue(FuzzyValue.PL));
    }

    public static FuzzyToken NL = new FuzzyToken(1, 0, 0, 0, 0);
    public static FuzzyToken NM = new FuzzyToken(0, 1, 0, 0, 0);
    public static FuzzyToken ZR = new FuzzyToken(0, 0, 1, 0, 0);
    public static FuzzyToken PM = new FuzzyToken(0, 0, 0, 1, 0);
    public static FuzzyToken PL = new FuzzyToken(0, 0, 0, 0, 1);

    public static FuzzyToken randomToken(){
        return new FuzzyToken(Math.random(),Math.random(),Math.random(),Math.random(),Math.random());
    }
    public String toString(){
        return this.shortString();
    }
}
