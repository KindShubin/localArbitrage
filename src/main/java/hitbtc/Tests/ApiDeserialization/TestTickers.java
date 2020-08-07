package hitbtc.Tests.ApiDeserialization;

import hitbtc.ApiClasses.Ticker.Ticker;
import hitbtc.ApiClasses.Ticker.TickerSymbol;

import java.io.IOException;

public class TestTickers {

    public static void main(String[] args) throws IOException {
        System.out.println("Begin");
        TickerSymbol tick = new TickerSymbol("NXTETH");
        System.out.println("tick:");
        System.out.println(tick.toString());
        Ticker ticks = new Ticker();
        System.out.println("ticks:");
        System.out.println(ticks);

    }

}
