package OETPN;

import OETPN.PlaceTypes.Token;

import java.util.List;

public interface TokenProcessor {
    List<Token> apply(List<Token> tokens);
}

