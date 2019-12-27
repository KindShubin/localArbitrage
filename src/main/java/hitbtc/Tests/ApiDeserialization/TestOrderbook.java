package hitbtc.Tests.ApiDeserialization;

import hitbtc.ApiClasses.Orderbook.Orderbook;

import java.io.IOException;

public class TestOrderbook {

    public static void main(String[] args) throws IOException {
        System.out.println("Begin");
        Orderbook ethbtc = new Orderbook("ETHBTC", 5);
        System.out.println("ethbtc:\n"+ethbtc.toString());
        Orderbook arneth = new Orderbook("ARNETH", 6);
        System.out.println("ARNETH:\n"+arneth.toString());
        Orderbook xrpusd = new Orderbook("XRPUSD", 7);
        System.out.println("XRPUSD:\n"+xrpusd.toString1());
        Orderbook xrpusdt = new Orderbook("XRPUSDT", 7);
        System.out.println("XRPUSDT:\n"+xrpusdt.toString1());
    }
}
