package hitbtc.Tests.WriteToDB;

import hitbtc.ApiClasses.Currency.Currency;
import hitbtc.ApiClasses.Currency.Currencys;
import hitbtc.InfoDB.WriteDataToDB;

import java.io.IOException;
import java.sql.SQLException;

public class WriteDataToDBcoins {

    public static void main(String[] args) throws IOException, SQLException {
        System.out.println("Begin");
        System.out.println("USD");
        Currency curr = new Currency("USD");
        System.out.println("curr:");
        System.out.println(curr.toString());
        System.out.println("write:");
        WriteDataToDB.toDBCoin(curr);
        System.out.println("USDT");
        Currency curr1 = new Currency("USDT");
        System.out.println("curr1:");
        System.out.println(curr1.toString());
        System.out.println("write:");
        WriteDataToDB.toDBCoin(curr1);

        //Currencys currs = new Currencys();
        //System.out.println("currs:");
        //System.out.println(currs);
    }

}
