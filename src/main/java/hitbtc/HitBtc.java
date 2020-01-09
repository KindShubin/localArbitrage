package hitbtc;

import DB.GetVal;
import com.google.gson.Gson;

import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

        for(int v=0; v<1; v++, Thread.sleep(5000) ) {

            StringBuilder sb1= new StringBuilder();
            int vv=0;
            String times="";
            String time1= new StringBuilder().append("time:").append(new SimpleDateFormat("hh:mm:ss.SSSS").format(new Date())).append("\tbefore write to DB\n").toString();
            times=times+time1;
            System.out.println(time1);
            Ticker ticker = new Ticker();
//            try {
//                WriteDataToDB.toDBHitbtcAll(ticker);
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
            String time2= new StringBuilder().append("time:").append(new SimpleDateFormat("hh:mm:ss.SSSS").format(new Date())).append("\tafter write to DB\n").toString();
            times=times+time2;
            System.out.println(time2);
            Coins coinsdb = new Coins();
            Pairs pairsdb = new Pairs();
            Hitbtc hitbtcdb = new Hitbtc();
            String time3= new StringBuilder().append("time:").append(new SimpleDateFormat("hh:mm:ss.SSSS").format(new Date())).append("\tafter make objects Coins Pairs Hitbtc\n").toString();
            times=times+time3;
            System.out.println(time3);
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
                                System.out.printf("Base coin:%s-%s\tQuoteCoin1:%s-%s\tQuoteCoin2:%s-%s\t1)%s:%s:%s\t2)%s:%s:%s\t3)%s:%s:%s",
                                        baseAltCoin, coinsdb.getAbbr(baseAltCoin), quoteCoin1thTransaction, coinsdb.getAbbr(quoteCoin1thTransaction),
                                        quoteCoin2thTransaction, coinsdb.getAbbr(quoteCoin2thTransaction),
                                        idPair1thTransaction, pairsdb.getExForm(idPair1thTransaction), ticker.tickers.get(pairsdb.getExForm(idPair1thTransaction)).symbol,
                                        idPair2thTransaction, pairsdb.getExForm(idPair2thTransaction), ticker.tickers.get(pairsdb.getExForm(idPair2thTransaction)).symbol,
                                        idPair3thTransaction, pairsdb.getExForm(idPair3thTransaction), ticker.tickers.get(pairsdb.getExForm(idPair3thTransaction)).symbol);
                                //////////////////
                                boolean check = true;
                                double volumebaseCoin=0.0;
                                double volumeQuoteCoin2thTransacrion=0.0;
                                double volumeQuoteCoin3and1thTransaction=0.0;
                                try{
                                    //volumebaseCoin = makeTransaction(quoteCoin1thTransaction, baseAltCoin, VALUE.get(quoteCoin1thTransaction), hitbtcdb, pairsdb, false);
                                    volumebaseCoin = makeTransactionAPI(quoteCoin1thTransaction, baseAltCoin, VALUE.get(quoteCoin1thTransaction), ticker, pairsdb, false);
                                }catch (Exception e){
                                    check=false;
                                    System.out.println("Error. volumebaseCoin fail:");
                                    e.toString();
                                }
                                try{
                                    //volumeQuoteCoin2thTransacrion = makeTransaction(baseAltCoin, quoteCoin2thTransaction, volumebaseCoin, hitbtcdb, pairsdb, false);
                                    volumeQuoteCoin2thTransacrion = makeTransactionAPI(baseAltCoin, quoteCoin2thTransaction, volumebaseCoin, ticker, pairsdb, false);
                                }catch (Exception e){
                                    check=false;
                                    System.out.println("Error. volumeQuoteCoin2thTransacrion fail:");
                                    e.toString();
                                }
                                try{
                                    //volumeQuoteCoin3and1thTransaction = makeTransaction(quoteCoin2thTransaction, quoteCoin1thTransaction, volumeQuoteCoin2thTransacrion, hitbtcdb, pairsdb, false);
                                    volumeQuoteCoin3and1thTransaction = makeTransactionAPI(quoteCoin2thTransaction, quoteCoin1thTransaction, volumeQuoteCoin2thTransacrion, ticker, pairsdb, false);
                                }catch (Exception e){
                                    check=false;
                                    System.out.println("Error. volumeQuoteCoin3and1thTransaction fail:");
                                    e.toString();
                                }
                                if (check) {
                                    if (volumeQuoteCoin3and1thTransaction > VALUE.get(quoteCoin1thTransaction)) {
                                        System.out.println("\n++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
                                        String time4= new StringBuilder().append("time:").append(new SimpleDateFormat("hh:mm:ss.SSSS").format(new Date())).append("\tперед предварительным просчетом\n").toString();
                                        times=times+time4;
                                        System.out.println(time4);
                                        // повторные вычисления но с меткой print true. Для принта доп. информации
                                        //volumebaseCoin = makeTransaction(quoteCoin1thTransaction, baseAltCoin, VALUE.get(quoteCoin1thTransaction), hitbtcdb, pairsdb, true);
                                        //volumeQuoteCoin2thTransacrion = makeTransaction(baseAltCoin, quoteCoin2thTransaction, volumebaseCoin, hitbtcdb, pairsdb, true);
                                        //volumeQuoteCoin3and1thTransaction = makeTransaction(quoteCoin2thTransaction, quoteCoin1thTransaction, volumeQuoteCoin2thTransacrion, hitbtcdb, pairsdb, true);
                                        volumebaseCoin = makeTransactionAPI(quoteCoin1thTransaction, baseAltCoin, VALUE.get(quoteCoin1thTransaction), ticker, pairsdb, true);
                                        volumeQuoteCoin2thTransacrion = makeTransactionAPI(baseAltCoin, quoteCoin2thTransaction, volumebaseCoin, ticker, pairsdb, true);
                                        volumeQuoteCoin3and1thTransaction = makeTransactionAPI(quoteCoin2thTransaction, quoteCoin1thTransaction, volumeQuoteCoin2thTransacrion, ticker, pairsdb, true);
                                        String time5= new StringBuilder().append("time:").append(new SimpleDateFormat("hh:mm:ss.SSSS").format(new Date())).append("\tперед финальным просчетом\n").toString();
                                        times=times+time5;
                                        System.out.println(time5);
                                        String estProfit=finalProfit(coinsdb.coins.get(baseAltCoin),coinsdb.coins.get(quoteCoin1thTransaction),coinsdb.coins.get(quoteCoin2thTransaction));
//                                        Orderbook orderbook1thTr = new Orderbook(pairsdb.getExForm(idPair1thTransaction),3);
//                                        Orderbook orderbook2thTr = new Orderbook(pairsdb.getExForm(idPair2thTransaction),3);
//                                        Orderbook orderbook3thTr = new Orderbook(pairsdb.getExForm(idPair3thTransaction),3);
//                                        System.out.println("Symbol:"+pairsdb.getExForm(idPair1thTransaction)+"\n"+orderbook1thTr.toString());
//                                        System.out.println("Symbol:"+pairsdb.getExForm(idPair2thTransaction)+"\n"+orderbook2thTr.toString());
//                                        System.out.println("Symbol:"+pairsdb.getExForm(idPair3thTransaction)+"\n"+orderbook3thTr.toString());
                                        vv++;
                                        sb1.append(pairsdb.getExForm(idPair1thTransaction)).append("-").append(pairsdb.getExForm(idPair2thTransaction)).append("-").append(pairsdb.getExForm(idPair3thTransaction))
                                                .append(":").append(estProfit).append("\t");
                                        String time6= new StringBuilder().append("time:").append(new SimpleDateFormat("hh:mm:ss.SSSS").format(new Date())).append("\tпосле финального просчета\n").toString();
                                        times=times+time6;
                                        System.out.println(time6);
                                    }
                                    else {
                                        System.out.println("\tres:"+volumeQuoteCoin3and1thTransaction);
                                    }
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
            String time7= new StringBuilder().append("time:").append(new SimpleDateFormat("hh:mm:ss.SSSS").format(new Date())).append("\tконец\n").toString();
            times=times+time7;
            System.out.println(times);

        }
        System.out.println(sb.toString());
        System.exit(0);

    }

    public static TickerSymbol[] jsonTickerToArrayTickers(String json) {
        Gson g = new Gson();
        TickerSymbol[] tickers = g.fromJson(json, TickerSymbol[].class);
        return tickers;
    }

    // удалил метод writeJsonToDB(TickerSymbol[] tickers). Есть спец класс WriteDataToDB.java там все есть

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

    //возвращает объем купленных коинов. На вход пожается инфа из БД
    public static Double makeTransaction(int coinSell, int coinBuy, double volumeSellCoin, Hitbtc snapshotHitbtc, Pairs pairs, boolean print){
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
            res= price==0?0.0:1*volumeSellCoin/price*(1-FEE);
// убрал также из входящих параметров  double volumeBuyCoin. На этом этапе это не важно
//            if (volumeBuyCoin<res){
//                res=volumeBuyCoin;
//            }
        }
        else if (pairs.getBaseCoin(pair)==coinSell){
            idHitbtcPair=snapshotHitbtc.getIdHitbtcPair(pair);
            if(idHitbtcPair<1){
                return res;
            }
            price=snapshotHitbtc.hitbtc.get(idHitbtcPair).bid1;
            strPrice="bid1";
            res=volumeSellCoin*price/1*(1-FEE);
//            if(volumeBuyCoin<res){
//                res=volumeBuyCoin;
//            }
        }
        else{
            System.out.printf("ERROR |makeTransaction| coinSell:%s, coinBuy:%s, volumeSellCoin:%s, pair:%s, pairs.getBaseCoin(pair):%s"
                    ,coinSell,coinBuy,volumeSellCoin,pair,pairs.getBaseCoin(pair));
        }
        if (print){
            System.out.printf("*coinSell:%s, coinBuy:%s, volumeSellCoin:%s, idHitbtcPair:%s, pair:%s, strPrice:%s, price:%s, res=%s*\n",coinSell,coinBuy,volumeSellCoin,idHitbtcPair,pair,strPrice,price,res);
        }
        return res;
    }

    //возвращает объем купленных коинов. На вход пожается инфа из БД
    public static Double makeTransactionAPI(int coinSell, int coinBuy, double volumeSellCoin, Ticker ticker, Pairs pairs, boolean print){
        int pair = pairs.getPair(coinBuy, coinSell);
        if (pair == 0) {return 0.0;}
        String strPair=pairs.pairs.get(pair).exForm;
        //System.out.printf("Pair:%s, strPair%s\n", pair, strPair);
        double price = 0.0;
        String strPrice="";//потом убрать
        double res = 0.0;
        if (pairs.getBaseCoin(pair)==coinBuy){
            try{
                price=ticker.tickers.get(strPair).ask;
            } catch (Exception e){
                System.out.printf("|makeTransactionAPI| Error find price ask for pair%s\n",strPair);
                e.toString();
                return  res;
            }
            strPrice="ask1";
            res= price==0?0.0:1*volumeSellCoin/price*(1-FEE);
// убрал также из входящих параметров  double volumeBuyCoin. На этом этапе это не важно
//            if (volumeBuyCoin<res){
//                res=volumeBuyCoin;
//            }
        }
        else if (pairs.getBaseCoin(pair)==coinSell){
            try{
                price=ticker.tickers.get(strPair).bid;
            }catch (Exception e){
                System.out.printf("|makeTransactionAPI| Error find price bid for pair%s\n",strPair);
                e.toString();
                return  res;
            }
            strPrice="bid1";
            res= price==0.0?0:volumeSellCoin*price*(1-FEE);
//            if(volumeBuyCoin<res){
//                res=volumeBuyCoin;
//            }
        }
        else{
            System.out.printf("ERROR |makeTransaction| coinSell:%s, coinBuy:%s, volumeSellCoin:%s, pair:%s, pairs.getBaseCoin(pair):%s"
                    ,coinSell,coinBuy,volumeSellCoin,pair,pairs.getBaseCoin(pair));
        }
        if (print){
            System.out.printf("*coinSell:%s, coinBuy:%s, volumeSellCoin:%s, strPairTicker:%s, pair:%s, strPrice:%s, price:%s, res=%s*\n",coinSell,coinBuy,volumeSellCoin,strPair,pair,strPrice,price,res);
        }
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
    ////////////////////временно сделал чтобы отдавало String с возможной прибылью
    public static String finalProfit(Coin baseCoin, Coin quoteCoin1, Coin quoteCoin2) throws SQLException, IOException {
        Pair pairTrans1th = new Pair(getPairDB(baseCoin.id,quoteCoin1.id));
        Pair pairTrans2th = new Pair(getPairDB(baseCoin.id,quoteCoin2.id));
        Pair pairTrans3th = new Pair(getPairDB(quoteCoin1.id,quoteCoin2.id));

        System.out.println("pairTrans1th: "+pairTrans1th.toString());
        System.out.println("pairTrans2th: "+pairTrans2th.toString());
        System.out.println("pairTrans3th: "+pairTrans3th.toString());

        Orderbook obTrans1th = new Orderbook(pairTrans1th.exForm,4);
        Orderbook obTrans2th = new Orderbook(pairTrans2th.exForm,4);
        Orderbook obTrans3th = new Orderbook(pairTrans3th.exForm,4);

        System.out.println("\nobTrans1th: "+obTrans1th.toString());
        System.out.println("obTrans2th: "+obTrans2th.toString());
        System.out.println("obTrans3th: "+obTrans3th.toString());

        //продаю QuoteCoin1 покупаю BaseCoin
        double volBaseCoinTrans1th = obTrans1th.ask.get(0).size;
        double volQuote1CoinTrans1th = obTrans1th.ask.get(0).size*obTrans1th.ask.get(0).price*(1+FEE);
        //продаю BaseCoin, покупаю QuoteCoin2
        double volBaseCoinTrans2th = volBaseCoinTrans1th;
        double volQuote2CoinTrans2th = getAmountBuyCoinFromPair(pairTrans2th,baseCoin,obTrans2th, volBaseCoinTrans1th);
        //продаю QuoteCoin2, покупаю QuoteCoin1
        double volQuote1CoinTrans3th = getAmountBuyCoinFromPair(pairTrans3th,quoteCoin2,obTrans3th,volQuote2CoinTrans2th);
        double volQuote2CoinTrans3th = volQuote2CoinTrans2th;
        String debug = new StringBuilder().append("volBaseCoinTrans1th:").append(volBaseCoinTrans1th).append("\tvolQuote1CoinTrans1th:").append(volQuote1CoinTrans1th)
                .append("\nvolBaseCoinTrans2th:").append(volBaseCoinTrans2th).append("\tvolQuote2CoinTrans2th:").append(volQuote2CoinTrans2th)
                .append("\nvolQuote1CoinTrans3th:").append(volQuote1CoinTrans3th).append("\tvolQuote2CoinTrans3th:").append(volQuote2CoinTrans3th)
                .append("\n estimate profit:").append(volQuote1CoinTrans3th-volQuote1CoinTrans1th).append(quoteCoin1.abbreviation).toString();
        System.out.println(debug);
        return new StringBuilder().append(volQuote1CoinTrans3th-volQuote1CoinTrans1th).append(" ").append(quoteCoin1.abbreviation).toString();
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
                price = ob.bid.get(i).price;
                i++;
            }
            resAmount=price*amountSellCoin*(1-FEE);
        } else {
            strPrice = "ask";
            while (amountSellCoinFromOrderbook<amountSellCoin && i<ob.bid.size()){
                amountSellCoinFromOrderbook+=ob.ask.get(i).size*ob.ask.get(i).price;
                price = ob.ask.get(i).price;
                i++;
            }
            resAmount = price==0?0.0:amountSellCoin/price*(1-FEE);//если price 0, то деление на 0.0 выдает результат Infinity
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
