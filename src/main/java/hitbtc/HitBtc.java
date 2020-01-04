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
import hitbtc.ApiClasses.Orderbook.Orderbook;
import hitbtc.ApiClasses.Ticker.Ticker;
import hitbtc.ApiClasses.Ticker.TickerSymbol;
import hitbtc.InfoDB.Coins.Coin;
import hitbtc.InfoDB.Coins.Coins;
import hitbtc.InfoDB.Hitbtc.Hitbtc;
import hitbtc.InfoDB.Pairs.Pair;
import hitbtc.InfoDB.Pairs.Pairs;
import hitbtc.InfoDB.WriteDataToDB;

public class HitBtc {

    public static final double FEE = 0.0007;

    //временно для просчета
    public static final Map<Integer, Double> VALUE = new HashMap<Integer, Double>();
    static {
        VALUE.put(1,1.0);
        VALUE.put(6,1.0);
        VALUE.put(59,1.0);
        VALUE.put(255,1.0);
        VALUE.put(257,1.0);
        VALUE.put(267,1.0);
        VALUE.put(306,1.0);
        VALUE.put(315,1.0);
        VALUE.put(392,1.0);

    //VALUE.put(1,0.00015);
    //VALUE.put(6,0.008);
    //VALUE.put(59,0.4);
    //VALUE.put(255,1.0);
    //VALUE.put(257,1.0);
    //VALUE.put(267,1.0);
    //VALUE.put(306,0.005);
    //VALUE.put(315,1.0);
    }



    public static void main(String[] args) throws IOException, InterruptedException, SQLException {

        String url = "https://api.hitbtc.com/api/2/public/ticker";

        StringBuilder sb = new StringBuilder();

        for(int v=0; v<20; v++, Thread.sleep(4000) ) {

            StringBuilder sb1= new StringBuilder();
            int vv=0;

            Ticker ticker = new Ticker();
            try {
                WriteDataToDB.toDBHitbtcAll(ticker);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            Coins coinsdb = new Coins();
            Pairs pairsdb = new Pairs();
            Hitbtc hitbtcdb = new Hitbtc();
            ArrayList<Integer> baseAltCoins = getBaseAltCoins();
            for (int baseAltCoin : baseAltCoins) {
                ArrayList<Integer> quotsCoin = pairsdb.getArrQuoteCoinsForBaseCoin(baseAltCoin);
                System.out.printf("|Base coin:%s\tQuoteCoins:%s\n", baseAltCoin, quotsCoin.toString());
                if (quotsCoin.size() > 1) {
                    // массив исключающий пары в далнейшем
                    ArrayList<Integer> arrDisableQuoteCoin = new ArrayList<>();
                    for (int j = 0; j < quotsCoin.size(); j++) {
                        arrDisableQuoteCoin.add(quotsCoin.get(j));
                        int quoteCoin1thTransaction = quotsCoin.get(j);//базовый коин для первой транзакции
                        int idPair1thTransaction = pairsdb.getPair(baseAltCoin, quoteCoin1thTransaction);//id пары для первой транзакции
                        for (int k = 0; k < quotsCoin.size(); k++) {//for (int k=j+1; k<quotsCoin.size(); k++){
                            if (k != j) {
                                int quoteCoin2thTransaction = quotsCoin.get(k);//базовый коин для второй транзакции
                                int idPair2thTransaction = pairsdb.getPair(baseAltCoin, quoteCoin2thTransaction);//id пары для второй транзакции
                                int idPair3thTransaction = pairsdb.getPair(quoteCoin1thTransaction, quoteCoin2thTransaction);//id пары базовых коинов для третей транзакции
                                System.out.printf("Base coin:%s-%s\tQuoteCoin1:%s-%s\tQuoteCoin2:%s-%s\t1)%s:%s\t2)%s:%s\t3)%s:%s\n",
                                        baseAltCoin, coinsdb.getAbbr(baseAltCoin), quoteCoin1thTransaction, coinsdb.getAbbr(quoteCoin1thTransaction), quoteCoin2thTransaction,
                                        coinsdb.getAbbr(quoteCoin2thTransaction), idPair1thTransaction, pairsdb.getExForm(idPair1thTransaction), idPair2thTransaction,
                                        pairsdb.getExForm(idPair2thTransaction), idPair3thTransaction, pairsdb.getExForm(idPair3thTransaction));
                                //////////////////
                                boolean check = true;
                                double volumebaseCoin = makeTransaction(quoteCoin1thTransaction, baseAltCoin, VALUE.get(quoteCoin1thTransaction), 9999.0, hitbtcdb, pairsdb);
                                if (volumebaseCoin == 99999.9999) {
                                    check = false;
                                }
                                double volumeQuoteCoin2thTransacrion = makeTransaction(baseAltCoin, quoteCoin2thTransaction, volumebaseCoin, 8888.0, hitbtcdb, pairsdb);
                                if (volumeQuoteCoin2thTransacrion == 99999.99999) {
                                    check = false;
                                }
                                double volumeQuoteCoin3and1thTransaction = makeTransaction(quoteCoin2thTransaction, quoteCoin1thTransaction, volumeQuoteCoin2thTransacrion, 7777.0, hitbtcdb, pairsdb);
                                if (volumeQuoteCoin3and1thTransaction == 99999.99999) {
                                    check = false;
                                }
                                if (check) {
                                    if (volumeQuoteCoin3and1thTransaction > VALUE.get(quoteCoin1thTransaction)) {
                                        System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
                                        finalProfit(coinsdb.coins.get(baseAltCoin),coinsdb.coins.get(quoteCoin1thTransaction),coinsdb.coins.get(quoteCoin2thTransaction));
//                                        Orderbook orderbook1thTr = new Orderbook(pairsdb.getExForm(idPair1thTransaction),3);
//                                        Orderbook orderbook2thTr = new Orderbook(pairsdb.getExForm(idPair2thTransaction),3);
//                                        Orderbook orderbook3thTr = new Orderbook(pairsdb.getExForm(idPair3thTransaction),3);
//                                        System.out.println("Symbol:"+pairsdb.getExForm(idPair1thTransaction)+"\n"+orderbook1thTr.toString());
//                                        System.out.println("Symbol:"+pairsdb.getExForm(idPair2thTransaction)+"\n"+orderbook2thTr.toString());
//                                        System.out.println("Symbol:"+pairsdb.getExForm(idPair3thTransaction)+"\n"+orderbook3thTr.toString());
                                        vv++;
                                        sb1.append(pairsdb.getExForm(idPair1thTransaction)).append("-").append(pairsdb.getExForm(idPair2thTransaction)).append("-").append(pairsdb.getExForm(idPair3thTransaction)).append("\t");

                                    }
                                    //else System.out.println("--------------------------------------");
                                } else {
                                    System.out.println("??????????????????????????????????");
                                }
                            }
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
            sb.append("v").append(v).append(":").append(vv).append(":\t").append(sb1.toString()).append("\n");

        }
        System.out.println(sb.toString());
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
            //if(ts.symbol=="USDT"){ts.symbol="USD";}
            System.out.printf("\n%s ",ts.symbol);
            String selectIdPair = new StringBuilder().append("select id from exchange.pairs where exForm=\"").append(ts.symbol).append("\"").toString();
            System.out.println(selectIdPair);
            String insert = new StringBuilder().append("INSERT INTO exchange.hitbtc (pair, bid1, ask1, date) VALUES ((").append(selectIdPair).append("),").append(ts.bid).append(",")
                    .append(ts.ask).append(", now())").toString();
            String update = new StringBuilder().append("UPDATE exchange.hitbtc SET bid1=").append(ts.bid).append(", ask1=").append(ts.ask)
                    .append(", date=now() WHERE (pair = (").append(selectIdPair).append("))").toString();

            //System.out.println(insert);
            //System.out.println(update);
            int i = 0;
            try{
                i = DBconnactionVPS.executeQueryInt(update);
                System.out.print("output UPDATE:"+i+" ");
            } catch (SQLException e) {
                System.out.println("update ERROR ");
                if (i!=0){
                    System.out.println("ERROR wtf");
                    System.out.println(e.toString());
                }
            }
            if (i==0){
                //System.out.println("ERROR. Update to exchenge.hitbtc failed. Try insert");
                ArrayList<HashMap> resID = null;
                try {
                    resID = DBconnactionVPS.getResultSet(selectIdPair);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                if (GetVal.getInt(resID.get(0),"id")>1){
                    try {
                        System.out.println(insert);
                        //DBconnactionVPS.executeQuery(insert);
                        DBconnactionVPS.executeQueryInsert(insert);
                        System.out.print("Insert DONE");
                    } catch (SQLException e) {
                        System.out.println("Insert ERROR:");
                        e.printStackTrace();
                    }
                } else {
                    //String insertToPairs = ""
                }
                try {
                    System.out.println(insert);
                    //DBconnactionVPS.executeQuery(insert);
                    DBconnactionVPS.executeQueryInsert(insert);
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
        //392
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

    public static void insertNewPairToDB(String symbol) throws SQLException {
        String selectCheck = new StringBuilder().append("SELECT id FROM exchange.pairs where exForm = ").append(symbol).toString();
        ArrayList<HashMap> rsSelectCheck = DBconnactionVPS.getResultSet(selectCheck);
        if (GetVal.getInt(rsSelectCheck.get(0),"id")>1){
            System.out.println("Error|insertNewPairToDB| Symbol alredy in DB");
        } else {

        }
    }

    // получает на вход base coin и 2 quote coin которые показали предварительную доходность.
    // заьирает по api orderbook каждой пары и считает уже по существующим объемам все 3 транзакции.
    //   ????? переделать так чтобы на вход приходили объекты снапшота БД с Pairs, Coins  и т.д. и данные брать из обекта ??????????
    public static void finalProfit(Coin baseCoin, Coin quoteCoin1, Coin quoteCoin2) throws SQLException, IOException {
        Pair pairTrans1th = new Pair(getPairDB(baseCoin.id,quoteCoin1.id));
        Pair pairTrans2th = new Pair(getPairDB(baseCoin.id,quoteCoin2.id));
        Pair pairTrans3th = new Pair(getPairDB(quoteCoin1.id,quoteCoin2.id));

        System.out.println("pairTrans1th: "+pairTrans1th.toString());
        System.out.println("pairTrans2th: "+pairTrans2th.toString());
        System.out.println("pairTrans3th: "+pairTrans3th.toString());

        Orderbook obTrans1th = new Orderbook(pairTrans1th.exForm,3);
        Orderbook obTrans2th = new Orderbook(pairTrans2th.exForm,3);
        Orderbook obTrans3th = new Orderbook(pairTrans3th.exForm,3);

        System.out.println("obTrans1th: "+obTrans1th.toString());
        System.out.println("obTrans2th: "+obTrans2th.toString());
        System.out.println("obTrans3th: "+obTrans3th.toString());

        //продаю QuoteCoin1 покупаю BaseCoin
        double volBaseCoinTrans1th = obTrans1th.ask.get(0).size*(1-FEE);
        double volQuote1CoinTrans1th = obTrans1th.ask.get(0).size*obTrans1th.ask.get(0).price;
        //продаю BaseCoin, покупаю QuoteCoin2
        double volBaseCoinTrans2th = volBaseCoinTrans1th;
        double volQuote2CoinTrans2th = getAmountBuyCoinFromPair(pairTrans2th,baseCoin,obTrans2th, volBaseCoinTrans1th);
        //продаю QuoteCoin2, покупаю QuoteCoin1
        double volQuote1CoinTrans3th = getAmountBuyCoinFromPair(pairTrans3th,quoteCoin2,obTrans3th,volQuote2CoinTrans2th);
        double volQuote2CoinTrans3th = volQuote2CoinTrans2th;
        String debug = new StringBuilder().append("volBaseCoinTrans1th:").append(volBaseCoinTrans1th).append("\tvolQuote1CoinTrans1th:").append(volQuote1CoinTrans1th)
                .append("\nvolBaseCoinTrans2th:").append(volBaseCoinTrans2th).append("\tvolQuote2CoinTrans2th:").append(volQuote2CoinTrans2th)
                .append("\nvolQuote1CoinTrans3th:").append(volQuote1CoinTrans3th).append("\tvolQuote2CoinTrans3th:").append(volQuote2CoinTrans3th).toString();
        System.out.println(debug);

    }

    //отдает объем предполагаемой сделки когда известна пара, монета которая продается и объем продаваемой монеты
    //изначально делалось для тех случаев когда есть пара c BTC и USD и непонятно как представлена BTCUSD или USDBTC.
    //считает результат по большей цене из стакана чем цена первого ордера в стакане. Зависит от объема
    public static double getAmountBuyCoinFromPair(Pair pair, Coin coinSell, Orderbook ob, double amountSellCoin){
        double resAmount=0.0;
        double amountSellCoinFromOrderbook=0.0;
        double price=0.0;
        String strPrice="";
        int i=0;//инкремент для сумирования объемов из OrderBook
        if (firstCoinOnPair(pair, coinSell)){
            strPrice = "bid";
            while (amountSellCoinFromOrderbook<amountSellCoin && i<ob.bid.size()){
                amountSellCoinFromOrderbook+=ob.bid.get(i).size;
                i++;
            }
            price = ob.bid.get(i).price;
            resAmount=price*amountSellCoin*(1-FEE);
        } else {
            strPrice = "ask";
            while (amountSellCoinFromOrderbook<amountSellCoin && i<ob.bid.size()){
                amountSellCoinFromOrderbook+=ob.ask.get(i).size*ob.ask.get(i).price;
                i++;
            }
            price = ob.ask.get(i).price;
            resAmount = amountSellCoin/price*(1-FEE);
        }
        System.out.printf("|getAmountBuyCoinFromPair| pair:%s, coinSell:%s, amountSellCoin:%s.\t amountSellCoinFromOrderbook:%s, strPrice:%s, price:%s, resAmount:%s\n", pair.exForm, coinSell.abbreviation, amountSellCoin, amountSellCoinFromOrderbook, strPrice, price, resAmount);
        return resAmount;
    }

    public static boolean firstCoinOnPair(Pair pair, Coin coin){
        if (pair.baseCoin==coin.id){
            return true;
        }
        else return false;
    }





}
