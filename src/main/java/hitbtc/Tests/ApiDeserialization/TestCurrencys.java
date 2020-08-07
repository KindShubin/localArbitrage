package hitbtc.Tests.ApiDeserialization;

import hitbtc.ApiClasses.Currency.Currency;
import hitbtc.ApiClasses.Currency.Currencys;

import java.io.IOException;

public class TestCurrencys {

    public static void main(String[] args) throws IOException {
        System.out.println("Begin");
        Currency curr = new Currency("TRX");
        System.out.println("curr:");
        System.out.println(curr.toString());
        Currencys currs = new Currencys();
        System.out.println("currs:");
        System.out.println(currs);
    }

}
