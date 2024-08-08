package OETPN;

import OETPN.PlaceTypes.Token;

import java.util.List;

public interface TokenConsumer {
    void apply(List<Token> tokens);
}

