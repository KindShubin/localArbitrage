package hitbtc.Tests.WriteToDB;

import hitbtc.ApiClasses.Symbol.Symbol;
import hitbtc.ApiClasses.Symbol.Symbols;
import hitbtc.InfoDB.WriteDataToDB;

import java.io.IOException;
import java.sql.SQLException;

public class WriteDataToDBpairs {

    public static void main(String[] args) throws IOException, SQLException {
        System.out.println("Begin");
        System.out.println("XRPUSD");
        Symbol symb = new Symbol("XRPUSD");
        System.out.println("symb:");
        System.out.println(symb.toString());
        System.out.println("write:");
        WriteDataToDB.toDBPair(symb);
        System.out.println("XRPUSDT");
        Symbol symb1 = new Symbol("XRPUSDT");
        System.out.println("symb1:");
        System.out.println(symb1.toString());
        System.out.println("write:");
        WriteDataToDB.toDBPair(symb1);
        System.out.println("DRTUSDT");
        Symbol symb2 = new Symbol("DRTUSDT");
        System.out.println("symb2:");
        System.out.println(symb2.toString());
        System.out.println("write:");
        WriteDataToDB.toDBPair(symb2);
        //Symbols symbs = new Symbols();
        //System.out.println("symbs:");
        //System.out.println(symbs);
        System.exit(0);
    }
}
