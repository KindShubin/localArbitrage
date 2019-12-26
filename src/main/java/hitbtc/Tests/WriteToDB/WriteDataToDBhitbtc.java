package hitbtc.Tests.WriteToDB;

import hitbtc.ApiClasses.Ticker.Ticker;
import hitbtc.ApiClasses.Ticker.TickerSymbol;
import hitbtc.InfoDB.WriteDataToDB;

import java.io.IOException;
import java.sql.SQLException;

public class WriteDataToDBhitbtc {

    public static void main(String[] args) throws IOException, SQLException {
        System.out.println("Begin");
        System.out.println("XRPUSD");
        TickerSymbol tick = new TickerSymbol("XRPUSD");
        System.out.println("tick:");
        System.out.println(tick.toString());
        System.out.println("write:");
        WriteDataToDB.toDBHitbtc(tick);
        System.out.println("XRPUSDT");
        TickerSymbol tick1 = new TickerSymbol("XRPUSDT");
        System.out.println("tick1:");
        System.out.println(tick1.toString());
        System.out.println("write:");
        WriteDataToDB.toDBHitbtc(tick1);
        System.out.println("EMCUSDT");
        TickerSymbol tick2 = new TickerSymbol("EMCUSDT");
        System.out.println("tick2:");
        System.out.println(tick2.toString());
        System.out.println("write:");
        WriteDataToDB.toDBHitbtc(tick2);
        //Ticker ticks = new Ticker();
        //System.out.println("ticks:");
        //System.out.println(ticks);
        System.exit(0);
    }

}
