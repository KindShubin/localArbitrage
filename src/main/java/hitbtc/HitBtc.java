package hitbtc;

import DB.GetVal;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import DB.DBconnactionVPS;
import hitbtc.InfoDB.Coins;

public class HitBtc {

    public static void main(String[] args) throws IOException {

        String url = "https://api.hitbtc.com/api/2/public/ticker";

        URL obj = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) obj.openConnection();

        connection.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        System.out.println(response.toString());
        TickerSymbol[] tickers = jsonTickerToArrayTickers(response.toString());
        System.out.println("tickers:");
        for(TickerSymbol ts : tickers){
            ts.print();
        }
        System.out.printf("\n\n\n\n");
        //writeJsonToDB(tickers);
        ArrayList<Integer> uniqPairs=getBaseAltCoins();
        for(int i : uniqPairs){
            ArrayList<Integer> quotsCoin = getQuoteCoinsForBaseCoin(i);
            System.out.printf("Base coin %s: %s\n" , i, quotsCoin.toString());
        }
        
        Coins coins = new Coins();
        coins.print();

        System.exit(0);

    }

    public static TickerSymbol[] jsonTickerToArrayTickers(String json) {
        Gson g = new Gson();
        TickerSymbol[] tickers = g.fromJson(json, TickerSymbol[].class);
        return tickers;
    }

    //исходник не json, а массив TickerSymbol[] полученый из метода jsonTickerToArrayTickers(String json)
    //записывается базовая инфо пара, и ask1, bid1
    public static void writeJsonToDB(TickerSymbol[] tickers){
        for (TickerSymbol ts : tickers){
            String insert = new StringBuilder().append("INSERT INTO exchange.hitbtc (pair, bid1, ask1, date) VALUES ((select id from exchange.pairs where exForm='")
                    .append(ts.symbol).append("') , ").append(ts.bid).append(", ").append(ts.ask).append(", now());").toString();
            String update = new StringBuilder().append("UPDATE exchange.hitbtc SET bid1=").append(ts.bid).append(", ask1 = ").append(ts.ask)
                    .append(", date=now() WHERE (pair = (select id from exchange.pairs where exForm='").append(ts.symbol).append("'));").toString();
            System.out.println(insert);
            System.out.println(update);
            int i = 0;
            try{
                i = DBconnactionVPS.executeQueryInt(update);
                System.out.println("output UPDATE:"+i);
            } catch (SQLException e) {
                System.out.println("update ERROR:");
                e.printStackTrace();
            }
            if (i==0){
                System.out.println("ERROR. Update to exchenge.hitbtc failed. Try insert");
                try {
                    DBconnactionVPS.executeQuery(insert);
                    System.out.println(ts.symbol+" insert DONE");
                } catch (SQLException e) {
                    System.out.println("Insert ERROR:");
                    e.printStackTrace();
                }
            }
            else{
                System.out.println(ts.symbol+" update DONE");
            }
            System.out.println("writeJsonToDB done");
        }
    }

    public static ArrayList<Integer> getBaseAltCoins(){
        //1,6,59,255,257,267,306,315 - id монет к которым торгуется пары(BTC, ETH, USD...)
        String select = "SELECT baseCoin FROM exchange.pairs where baseCoin not in (1,6,59,255,257,267,306,315) group by baseCoin;";
        ArrayList<Integer> result = new ArrayList<>();
        ArrayList<HashMap> dbResult = null;
        try {
            dbResult = DBconnactionVPS.getResultSet(select);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("dbResult:"+dbResult);
        for (HashMap hm : dbResult){
            try{
                result.add(GetVal.getInt(hm,"baseCoin"));
            } catch (Exception e){
                System.out.println("ERROR add int ti array:");
                System.out.println(e.toString());
            }

        }
        return result;
    }

    //получить все инстурменты к которым торгуется данныя монета
    public static ArrayList<Integer> getQuoteCoinsForBaseCoin(int id){
        String select = "select ep.quoteCoin FROM exchange.hitbtc as eh join exchange.pairs as ep on eh.pair=ep.id where ep.baseCoin="+id;
        ArrayList<Integer> result = new ArrayList<>();
        ArrayList<HashMap> dbRes = null;
        try {
            dbRes = DBconnactionVPS.getResultSet(select);
        } catch (SQLException e) {
            System.out.println("|getQuoteCoinsForBaseCoin| Error select:");
            e.printStackTrace();
        }
        for (HashMap hm : dbRes){
            result.add(GetVal.getInt(hm,"quoteCoin"));
        }
        return result;
    }



}
