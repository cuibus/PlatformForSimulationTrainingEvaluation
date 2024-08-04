package OETPN;

import OETPN.PlaceTypes.Token;

import java.util.List;
import java.util.function.Function;

public interface TokenProcessor extends Function<List<Token>, List<Token>> {

}