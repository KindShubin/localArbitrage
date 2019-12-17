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
import hitbtc.InfoDB.Hitbtc;
import hitbtc.InfoDB.Pairs;

public class HitBtc {

    public static final double FEE = 0.0007;

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
        Coins coins = new Coins();
        Pairs pairs = new Pairs();
        Hitbtc hitbtcdb = new Hitbtc();
        ArrayList<Integer> baseAltCoins=getBaseAltCoins();
        for(int baseAltCoin : baseAltCoins){
            //ArrayList<Integer> quotsCoinDB = getQuoteCoinsForBaseCoinDB(i);
            ArrayList<Integer> quotsCoin = pairs.getArrQuoteCoinsForBaseCoin(baseAltCoin);
            System.out.printf("|Base coin:%s\tQuoteCoins:%s\n" , baseAltCoin, quotsCoin.toString());
            if(quotsCoin.size()>1){
                // массив исключающий пары в далнейшем
                ArrayList<Integer> arrDisableQuoteCoin = new ArrayList<>();
                for (int j=0; j<quotsCoin.size(); j++){
                    arrDisableQuoteCoin.add(quotsCoin.get(j));
                    int quoteCoin1thTransaction = quotsCoin.get(j);//базовый коин для первой транзакции
                    int idPair1thTransaction = pairs.getPair(baseAltCoin, quoteCoin1thTransaction);//id пары для первой транзакции
                    for (int k=j+1; k<quotsCoin.size(); k++){
                        int quoteCoin2thTransaction = quotsCoin.get(k);//базовый коин для второй транзакции
                        int idPair2thTransaction = pairs.getPair(baseAltCoin, quoteCoin1thTransaction);//id пары для второй транзакции
                        int idPair3thTransaction = pairs.getPair(quotsCoin.get(j), quotsCoin.get(k));//id пары базовых коинов для третей транзакции
                        System.out.printf("Base coin:%s-%s\tQuoteCoin1:%s-%s\tQuoteCoin2:%s-%s\t1)%s:%s\t2)%s:%s\t3)%s:%s\n",
                                baseAltCoin, coins.getAbbr(baseAltCoin), quoteCoin1thTransaction, coins.getAbbr(quoteCoin1thTransaction), quoteCoin2thTransaction,
                                coins.getAbbr(quoteCoin2thTransaction), idPair1thTransaction, pairs.getExForm(idPair1thTransaction), idPair2thTransaction,
                                pairs.getExForm(idPair2thTransaction), idPair3thTransaction, pairs.getExForm(idPair3thTransaction));

                    }
                }
                System.out.println(" ");
            }
        }

        //Coins coins = new Coins();
        //coins.print();
        //Pairs pairs = new Pairs();
        //pairs.print();
        //Hitbtc hitbtcdb = new Hitbtc();
        //hitbtcdb.print();




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
        System.out.println("writeJsonToDB ..............");
        for (TickerSymbol ts : tickers){
            System.out.printf("\n%s ",ts.symbol);
            String insert = new StringBuilder().append("INSERT INTO exchange.hitbtc (pair, bid1, ask1, date) VALUES ((select id from exchange.pairs where exForm='")
                    .append(ts.symbol).append("') , ").append(ts.bid).append(", ").append(ts.ask).append(", now());").toString();
            String update = new StringBuilder().append("UPDATE exchange.hitbtc SET bid1=").append(ts.bid).append(", ask1 = ").append(ts.ask)
                    .append(", date=now() WHERE (pair = (select id from exchange.pairs where exForm='").append(ts.symbol).append("'));").toString();
            //System.out.println(insert);
            //System.out.println(update);
            int i = 0;
            try{
                i = DBconnactionVPS.executeQueryInt(update);
                System.out.print("output UPDATE:"+i+" ");
            } catch (SQLException e) {
                System.out.println("update ERROR ");
                //e.printStackTrace();
                if (i!=0){
                    System.out.println("ERROR wtf");
                    System.out.println(e.toString());
                }
            }
            if (i==0){
                //System.out.println("ERROR. Update to exchenge.hitbtc failed. Try insert");
                try {
                    DBconnactionVPS.executeQuery(insert);
                    System.out.print("Insert DONE");
                } catch (SQLException e) {
                    System.out.println("Insert ERROR:");
                    e.printStackTrace();
                }
            }
        }
        System.out.println("writeJsonToDB done");
    }

    //Выборка базовых монет которые торгуются на бирже к USD, BTC, ETH и т.д.
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

    //получить все инстурменты к которым торгуется данныя монета через Базу
    //есть аналог метода в классе Pairs. getArrQuoteCoinsForBaseCoin
    public static ArrayList<Integer> getQuoteCoinsForBaseCoinDB(int id){
        //String select = "select ep.quoteCoin FROM exchange.hitbtc as eh join exchange.pairs as ep on eh.pair=ep.id where ep.baseCoin="+id;
        String select = "select quoteCoin from exchange.pairs where baseCoin="+id;
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

    //получить номер пары по двум номерам коинов. Ищет USDETH или ETHUSD...
    // есть аналог в классе Pairs
    public static int getPairDB(int coin1, int coin2){
        String select = new StringBuilder("SELECT id FROM exchange.pairs where (baseCoin=").append(coin1).append(" or baseCoin=").append(coin2)
        .append(") and (quoteCoin=").append(coin1).append(" or quoteCoin=").append(coin2).append(");").toString();
        int result;
        try {
            ArrayList<HashMap> resDB = DBconnactionVPS.getResultSet(select);
            result=GetVal.getInt(resDB.get(0),"id");
        } catch (SQLException e) {
            result=0;
            //System.out.println("ERROR sql getPairDB()");
            //System.out.println(e.toString());
            //e.printStackTrace();
        } catch (Exception e) {
            result=0;
            //System.out.println("ERROR getPairDB()");
            //System.out.println(e.toString());
            //e.printStackTrace();
        }
        return result;
    }

}
