package ApiDeserialization;

import hitbtc.ApiClasses.Symbol.Symbol;
import hitbtc.ApiClasses.Symbol.Symbols;

import java.io.IOException;

public class TestSymbols {

    public static void main(String[] args) throws IOException {
        System.out.println("Begin");
        Symbol symb = new Symbol("OMGETH");
        System.out.println("symb:");
        System.out.println(symb.toString());
        Symbols symbs = new Symbols();
        System.out.println("symbs:");
        System.out.println(symbs);
    }
}
