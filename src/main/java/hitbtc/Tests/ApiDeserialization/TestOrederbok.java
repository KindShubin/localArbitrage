package hitbtc.Tests.ApiDeserialization;

import hitbtc.ApiClasses.Orderbook.Orderbook;

import java.io.IOException;

public class TestOrederbok{

    public static void main(String[] args) throws IOException {
        System.out.println("Begin");
        Orderbook ethbtc = new Orderbook("ETHBTC", 5);
        System.out.println("ethbtc: "+ethbtc.toString());
        Orderbook arneth = new Orderbook("ARNETH", 6);
        System.out.println("ARNETH: "+arneth.toString());
    }
}
