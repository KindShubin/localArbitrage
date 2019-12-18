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
import java.util.Map;

import DB.DBconnactionVPS;
import hitbtc.InfoDB.Coins.Coins;
import hitbtc.InfoDB.Hitbtc.Hitbtc;
import hitbtc.InfoDB.Pairs.Pairs;

public class HitBtc {

    public static final double FEE = 0.0007;

    //временно для просчета
    public static final Map<Integer, Double> VALUE = new HashMap<Integer, Double>();
    static {
    VALUE.put(1,0.00015);
    VALUE.put(6,0.008);
    VALUE.put(59,0.4);
    VALUE.put(255,1.0);
    VALUE.put(257,1.0);
    VALUE.put(267,1.0);
    VALUE.put(306,0.005);
    VALUE.put(315,1.0);
    }



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
                        int idPair2thTransaction = pairs.getPair(baseAltCoin, quoteCoin2thTransaction);//id пары для второй транзакции
                        int idPair3thTransaction = pairs.getPair(quoteCoin1thTransaction, quoteCoin2thTransaction);//id пары базовых коинов для третей транзакции
                        System.out.printf("Base coin:%s-%s\tQuoteCoin1:%s-%s\tQuoteCoin2:%s-%s\t1)%s:%s\t2)%s:%s\t3)%s:%s\n",
                                baseAltCoin, coins.getAbbr(baseAltCoin), quoteCoin1thTransaction, coins.getAbbr(quoteCoin1thTransaction), quoteCoin2thTransaction,
                                coins.getAbbr(quoteCoin2thTransaction), idPair1thTransaction, pairs.getExForm(idPair1thTransaction), idPair2thTransaction,
                                pairs.getExForm(idPair2thTransaction), idPair3thTransaction, pairs.getExForm(idPair3thTransaction));
                        //////////////////
                        boolean check = true;
                        double volumebaseCoin=makeTransaction(quoteCoin1thTransaction, baseAltCoin, VALUE.get(quoteCoin1thTransaction), 9999.0, hitbtcdb,pairs);
                        if (volumebaseCoin==99999.9999) {check = false;}
                        double volumeQuoteCoin2thTransacrion = makeTransaction(baseAltCoin, quoteCoin2thTransaction, volumebaseCoin, 8888.0, hitbtcdb,pairs);
                        if (volumeQuoteCoin2thTransacrion == 99999.99999){check=false;}
                        double volumeQuoteCoin3and1thTransaction = makeTransaction(quoteCoin2thTransaction, quoteCoin1thTransaction, volumeQuoteCoin2thTransacrion, 7777.0,hitbtcdb,pairs);
                        if (volumeQuoteCoin3and1thTransaction == 99999.99999){check=false;}
                        if (check){
                            if (volumeQuoteCoin3and1thTransaction>VALUE.get(quoteCoin1thTransaction)){
                                System.out.println("+++++++++++++++++++++++++++++++++++++++");
                            }
                            else System.out.println("--------------------------------------");
                        } else {
                            System.out.println("??????????????????????????????????");
                        }

                        ///////////////////

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

    //возвращает объем купленных коинов
    public static Double makeTransaction(int coinSell, int coinBuy, double volumeSellCoin, double volumeBuyCoin, Hitbtc snapshotHitbtc, Pairs pairs){
        int pair = pairs.getPair(coinBuy, coinSell);
        if (pair == 0) {return 0.0;}
        double price = 0.0;
        String strPrice="";//потом убрать
        int idHitbtcPair=0;
        double res = 0.0;
        if (pairs.getBaseCoin(pair)==coinBuy){
            idHitbtcPair=snapshotHitbtc.getIdHitbtcPair(pair);
            System.out.println("idHitbtcPair:"+idHitbtcPair);
            if(idHitbtcPair<1){
                return res;
            }
            price=snapshotHitbtc.hitbtc.get(idHitbtcPair).ask1;
            strPrice="ask1";
            res=1*volumeSellCoin/price*(1-FEE);
            if (volumeBuyCoin<res){
                res=volumeBuyCoin;
            }
        }
        else if (pairs.getBaseCoin(pair)==coinSell){
            idHitbtcPair=snapshotHitbtc.getIdHitbtcPair(pair);
            System.out.println("idHitbtcPair:"+idHitbtcPair);
            if(idHitbtcPair<1){
                return res;
            }
            price=snapshotHitbtc.hitbtc.get(idHitbtcPair).bid1;
            strPrice="bid1";
            res=volumeSellCoin*price/1*(1-FEE);
            if(volumeBuyCoin<res){
                res=volumeBuyCoin;
            }
        }
        else{
            System.out.printf("ERROR |makeTransaction| coinSell:%s, coinBuy:%s, volumeSellCoin:%s, volumeBuyCoin:%s, pair:%s, pairs.getBaseCoin(pair):%s"
                    ,coinSell,coinBuy,volumeSellCoin,volumeBuyCoin,pair,pairs.getBaseCoin(pair));
        }
        System.out.printf("*coinSell:%s, coinBuy:%s, volumeSellCoin:%s,volumeBuyCoin:%s, idHitbtcPair:%s, pair:%s, strPrice:%s, price:%s, res=%s*\n",coinSell,coinBuy,volumeSellCoin,volumeBuyCoin,idHitbtcPair,pair,strPrice,price,res);
        return res;
    }


}
