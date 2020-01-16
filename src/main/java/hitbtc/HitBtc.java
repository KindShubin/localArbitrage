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
                        int idPair1thTransaction = pairsdb.getIntPair(baseAltCoin, quoteCoin1thTransaction);//id пары для первой транзакции
                        for (int k = 0; k < quotsCoin.size(); k++) {//for (int k=j+1; k<quotsCoin.size(); k++){
                            if (k != j) {
                                int quoteCoin2thTransaction = quotsCoin.get(k);//базовый коин для второй транзакции
                                int idPair2thTransaction = pairsdb.getIntPair(baseAltCoin, quoteCoin2thTransaction);//id пары для второй транзакции
                                int idPair3thTransaction = pairsdb.getIntPair(quoteCoin1thTransaction, quoteCoin2thTransaction);//id пары базовых коинов для третей транзакции
                                ///////// блок со сравнение инофрмации из БД и Tickers
                                String strPair1FromTickerApi="";
                                String strPair2FromTickerApi="";
                                String strPair3FromTickerApi="";
                                try{
                                    strPair1FromTickerApi = ticker.tickers.get(pairsdb.getExForm(idPair1thTransaction)).symbol;
                                }catch (Exception e){
                                    //если в Tickers нет нуджного совпадения по паре вида BTCETH, пробую искать совпадение ETHBTC
                                    try {
                                        strPair1FromTickerApi = ticker.tickers.get(coinsdb.coins.get(pairsdb.pairs.get(idPair1thTransaction).feeCoin).abbreviation.toString() + coinsdb.coins.get(pairsdb.pairs.get(idPair1thTransaction).baseCoin).abbreviation.toString()).symbol;
                                    } catch (Exception e1){e1.toString();}
                                }
                                try{
                                    strPair2FromTickerApi = ticker.tickers.get(pairsdb.getExForm(idPair2thTransaction)).symbol;
                                }catch (Exception e){
                                    //если в Tickers нет нуджного совпадения по паре вида BTCETH, пробую искать совпадение ETHBTC
                                    try {
                                        strPair2FromTickerApi = ticker.tickers.get(coinsdb.coins.get(pairsdb.pairs.get(idPair2thTransaction).feeCoin).abbreviation.toString() + coinsdb.coins.get(pairsdb.pairs.get(idPair2thTransaction).baseCoin).abbreviation.toString()).symbol;
                                    } catch (Exception e1){e1.toString();}
                                }
                                try{
                                    strPair3FromTickerApi = ticker.tickers.get(pairsdb.getExForm(idPair3thTransaction)).symbol;
                                }catch (Exception e){
                                    //если в Tickers нет нуджного совпадения по паре вида BTCETH, пробую искать совпадение ETHBTC
                                    try{
                                    strPair3FromTickerApi = ticker.tickers.get(coinsdb.coins.get(pairsdb.pairs.get(idPair3thTransaction).feeCoin).abbreviation.toString()+coinsdb.coins.get(pairsdb.pairs.get(idPair3thTransaction).baseCoin).abbreviation.toString()).symbol;
                                    } catch (Exception e1){e1.toString();}
                                }
                                ///////////////////////////////
                                System.out.printf("Base coin:%s-%s\tQuoteCoin1:%s-%s\tQuoteCoin2:%s-%s\t1)%s:%s:%s\t2)%s:%s:%s\t3)%s:%s:%s",
                                        baseAltCoin, coinsdb.getAbbr(baseAltCoin), quoteCoin1thTransaction, coinsdb.getAbbr(quoteCoin1thTransaction),
                                        quoteCoin2thTransaction, coinsdb.getAbbr(quoteCoin2thTransaction),
                                        idPair1thTransaction, pairsdb.getExForm(idPair1thTransaction), strPair1FromTickerApi.equals(pairsdb.getExForm(idPair1thTransaction))?strPair1FromTickerApi:strPair1FromTickerApi+"!!!!!!!!!!!!!!!!!!!!!",
                                        idPair2thTransaction, pairsdb.getExForm(idPair2thTransaction), strPair2FromTickerApi.equals(pairsdb.getExForm(idPair2thTransaction))?strPair2FromTickerApi:strPair2FromTickerApi+"!!!!!!!!!!!!!!!!!!!!!",
                                        idPair3thTransaction, pairsdb.getExForm(idPair3thTransaction), strPair3FromTickerApi.equals(pairsdb.getExForm(idPair3thTransaction))?strPair3FromTickerApi:strPair3FromTickerApi+"!!!!!!!!!!!!!!!!!!!!!");
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
                                        Coin coinBase=coinsdb.coins.get(baseAltCoin);
                                        Coin coinQuote1=coinsdb.coins.get(quoteCoin1thTransaction);
                                        Coin coinQuote2=coinsdb.coins.get(quoteCoin2thTransaction);
                                        // первое значение в массиве - кол-во обмена базовых коинов, второе значение - прдположительный профит в coinQuoute1
                                        ArrayList<Double> estProfit=finalProfit(coinBase,coinQuote1,coinQuote2);
//                                        Orderbook orderbook1thTr = new Orderbook(pairsdb.getExForm(idPair1thTransaction),3);
//                                        Orderbook orderbook2thTr = new Orderbook(pairsdb.getExForm(idPair2thTransaction),3);
//                                        Orderbook orderbook3thTr = new Orderbook(pairsdb.getExForm(idPair3thTransaction),3);
//                                        System.out.println("Symbol:"+pairsdb.getExForm(idPair1thTransaction)+"\n"+orderbook1thTr.toString());
//                                        System.out.println("Symbol:"+pairsdb.getExForm(idPair2thTransaction)+"\n"+orderbook2thTr.toString());
//                                        System.out.println("Symbol:"+pairsdb.getExForm(idPair3thTransaction)+"\n"+orderbook3thTr.toString());
                                        vv++;
                                        sb1.append(pairsdb.getExForm(idPair1thTransaction)).append("-").append(pairsdb.getExForm(idPair2thTransaction)).append("-").append(pairsdb.getExForm(idPair3thTransaction))
                                                .append(":").append(estProfit.get(1)).append("\t");
                                        String time6= new StringBuilder().append("time:").append(new SimpleDateFormat("hh:mm:ss.SSSS").format(new Date())).append("\tпосле финального просчета, перед итерациями\n").toString();
                                        times=times+time6;
                                        System.out.println(time6);
                                        Orderbook orderbook1thTr = new Orderbook(pairsdb.getExForm(idPair1thTransaction),4);
                                        Orderbook orderbook2thTr = new Orderbook(pairsdb.getExForm(idPair2thTransaction),4);
                                        Orderbook orderbook3thTr = new Orderbook(pairsdb.getExForm(idPair3thTransaction),4);
                                        Pair pair1thTrans = pairsdb.getPair(baseAltCoin, quoteCoin1thTransaction);
                                        Pair pair2thTrans = pairsdb.getPair(baseAltCoin, quoteCoin2thTransaction);
                                        Pair pair3thTrans = pairsdb.getPair(quoteCoin1thTransaction, quoteCoin2thTransaction);
                                        Double amountBaseCoin = estProfit.get(0);
                                        double nextProf = getNextBaseVolume(pair1thTrans,pair2thTrans,pair3thTrans,coinBase,coinQuote1,coinQuote2,orderbook1thTr,orderbook2thTr,orderbook3thTr,amountBaseCoin);
                                        String time7= new StringBuilder().append("time:").append(new SimpleDateFormat("hh:mm:ss.SSSS").format(new Date())).append("\tпосле итераций\n").toString();
                                        times=times+time7;
                                        System.out.println(time7);

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
            String time8= new StringBuilder().append("time:").append(new SimpleDateFormat("hh:mm:ss.SSSS").format(new Date())).append("\tконец\n").toString();
            times=times+time8;
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
        int pair = pairs.getIntPair(coinBuy, coinSell);
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
        int pair = pairs.getIntPair(coinBuy, coinSell);
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
    public static ArrayList<Double> finalProfit(Coin baseCoin, Coin quoteCoin1, Coin quoteCoin2) throws SQLException, IOException {
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

        boolean profitOrNot= true;// значение используется в цикле при поиске максимально большого кол-ва сделок для транзакции.
        // если добавление последующих заявок из стакана прводят к тому что итог получается пинус, тогда значение false;

        boolean bottleneckBaseCoinTrans1th=false;
        boolean bottleneckQuote1CoinTrans1th=false;
        boolean bottleneckBaseCoinTrans2th=false;
        boolean bottleneckQuote2CoinTrans2th=false;
        boolean bottleneckQuote1CoinTrans3th=false;
        boolean bottleneckQuote2CoinTrans3th=false;
        double result = 0.0;

        //продаю QuoteCoin1 покупаю BaseCoin
        double volBaseCoinTrans1th = getFirstVolume(obTrans1th, obTrans2th, obTrans3th); //obTrans1th.ask.get(0).size;
        double volQuote1CoinTrans1th = volBaseCoinTrans1th*obTrans1th.ask.get(0).price*(1+FEE);
        //продаю BaseCoin, покупаю QuoteCoin2
        double volBaseCoinTrans2th = volBaseCoinTrans1th;
        double volQuote2CoinTrans2th = getAmountBuyCoinFromPair(pairTrans2th,baseCoin,obTrans2th, volBaseCoinTrans2th);
        //продаю QuoteCoin2, покупаю QuoteCoin1
        double volQuote1CoinTrans3th = getAmountBuyCoinFromPair(pairTrans3th,quoteCoin2,obTrans3th,volQuote2CoinTrans2th);
        double volQuote2CoinTrans3th = volQuote2CoinTrans2th;
        result=volQuote1CoinTrans3th-volQuote1CoinTrans1th;

        if (result > 0.0) {
            while (profitOrNot) {

            }
        }

        String debug = new StringBuilder().append("volBaseCoinTrans1th:").append(volBaseCoinTrans1th).append("\tvolQuote1CoinTrans1th:").append(volQuote1CoinTrans1th)
                .append("\nvolBaseCoinTrans2th:").append(volBaseCoinTrans2th).append("\tvolQuote2CoinTrans2th:").append(volQuote2CoinTrans2th)
                .append("\nvolQuote1CoinTrans3th:").append(volQuote1CoinTrans3th).append("\tvolQuote2CoinTrans3th:").append(volQuote2CoinTrans3th)
                .append("\n estimate profit:").append(volQuote1CoinTrans3th-volQuote1CoinTrans1th).append(quoteCoin1.abbreviation).toString();
        System.out.println(debug);
        ArrayList<Double> resArr = new ArrayList<Double>(2);
        resArr.add(volBaseCoinTrans2th);
        resArr.add(result);
        return resArr;
        //return new StringBuilder().append(volQuote1CoinTrans3th-volQuote1CoinTrans1th).append(" ").append(quoteCoin1.abbreviation).toString();
    }

    //отдает объем предполагаемой сделки когда известна пара, монета которая продается и объем продаваемой монеты
    //изначально делалось для тех случаев когда есть пара c BTC и USD и непонятно как представлена BTCUSD или USDBTC.
    //считает результат по большей цене из стакана чем цена первого ордера в стакане. Зависит от объема
    public static double getAmountBuyCoinFromPair(Pair pair, Coin coinSell, Orderbook ob, double amountSellCoin){
        double resAmount=0.0;
        double amountSellCoinFromOrderbook=0.0;//промежуточное значение объемов, набираемое из стакана торгов
        double price=0.0;
        String strPrice;//ask или bid
        int i=0;//инкремент для сумирования объемов из OrderBook
        if (firstCoinOnPair(pair, coinSell)){
            strPrice = "bid";
            while (amountSellCoinFromOrderbook<amountSellCoin | i<ob.bid.size()){
                // сравнивает что получится после добавления объема рассматриваемой котировки к обему обему
                // если обему превышает заданное amountSellCoin, то ставит финальным объемом сделки значение amountSellCoin
                // и price с последней котировки
                // если же после суммирования size объем все еще меньше, то просто добавляется объем из котировки к финальному объему и начинаем рассматривать следующую котировку
                amountSellCoinFromOrderbook = amountSellCoinFromOrderbook+=ob.bid.get(i).size<amountSellCoin?amountSellCoinFromOrderbook+=ob.bid.get(i).size:amountSellCoin;
                price = ob.bid.get(i).price;
                i++;
            }
            resAmount = price==0?0.0:price*amountSellCoin*(1-FEE);//если price 0, то деление на 0.0 выдает результат Infinity
        } else {
            strPrice = "ask";
            while (amountSellCoinFromOrderbook<amountSellCoin | i<ob.ask.size()){
                amountSellCoinFromOrderbook = amountSellCoinFromOrderbook+=ob.ask.get(i).size<amountSellCoin?amountSellCoinFromOrderbook+=ob.ask.get(i).size:amountSellCoin;
                price = ob.ask.get(i).price;
                i++;
            }
            resAmount = price==0?0.0:amountSellCoin/price*(1-FEE);//если price 0, то деление на 0.0 выдает результат Infinity
        }
        System.out.printf("|getAmountBuyCoinFromPair| pair:%s, coinSell:%s, amountSellCoin:%s.\t amountSellCoinFromOrderbook:%s, strPrice:%s, price:%s, resAmount:%s\n", pair.exForm, coinSell.abbreviation, amountSellCoin, amountSellCoinFromOrderbook, strPrice, price, resAmount);
        return resAmount;
    }

    public static double getAmountSellCoinFromPair(Pair pair, Coin coinBuy, Orderbook ob, double amountBuyCoin){
        double resAmount;
        double amountBuyCoinOrderbook=0.0;
        double price=0.0;
        String strPrice;
        int i=0;
        if (firstCoinOnPair(pair, coinBuy)){
            strPrice="ask";
            while (amountBuyCoinOrderbook<amountBuyCoin | i<ob.ask.size()){
                amountBuyCoinOrderbook = amountBuyCoinOrderbook+=ob.ask.get(i).size<amountBuyCoin?amountBuyCoinOrderbook+=ob.ask.get(i).size:amountBuyCoin;
                price=ob.ask.get(i).price;
                i++;
            }
            resAmount = price==0.0?0.0:price*amountBuyCoin*(1+FEE);
        } else {
            strPrice="bid";
            while (amountBuyCoinOrderbook<amountBuyCoin | i<ob.bid.size()){
                amountBuyCoinOrderbook = amountBuyCoinOrderbook+=ob.bid.get(i).size<amountBuyCoin?amountBuyCoinOrderbook+=ob.bid.get(i).size:amountBuyCoin;
                price=ob.bid.get(i).price;
                i++;
            }
            resAmount = price==0.0?0.0:amountBuyCoin/price*(1+FEE);
        }
        System.out.printf("|getAmountSellCoinFromPair| pair:%s, coinBuy:%s, amountBuyCoin:%s.\t amountBuyCoinFromOrderbook:%s, strPrice:%s, price:%s, resAmount:%s\n", pair.exForm, coinBuy.abbreviation, amountBuyCoin, amountBuyCoinOrderbook, strPrice, price, resAmount);
        return resAmount;
    }

    //findcoin=Coin1 - монета объемы котоой указаны в Orderbook явно.
    //findCoin=Coin2 - монета к которой торгуется coin1. Надо переводить объем в coin1  и сравнивать
    private static double getPriceFromOrderbook(Orderbook ob, Coin coin1, Coin coin2, Coin findCoin, double amount, String askOrBid){
        double price = 0.0;
        int size = askOrBid=="ask"?ob.ask.size():ob.bid.size();
        if (findCoin==coin1){
            double tempAmountFromOB=0.0;
            for (int i=0; i<size; i++){
                if (askOrBid=="ask"){
                    tempAmountFromOB = tempAmountFromOB+ob.ask.get(i).size<amount?tempAmountFromOB+ob.ask.get(i).size:amount;
                } else {
                    tempAmountFromOB = tempAmountFromOB+ob.bid.get(i).size<amount?tempAmountFromOB+ob.bid.get(i).size:amount;
                }
                if (tempAmountFromOB>=amount){
                    price = askOrBid=="ask"?ob.ask.get(i).price:ob.bid.get(i).price;
                    break;
                }
            }
        } else{
            double tempAmountFromOB=0.0;
            for(int i=0; i<size; i++){
                // объем монеты к которой торгуется(quote) на конкретно котировке
                double partAmount = askOrBid=="ask"?ob.ask.get(i).price*ob.ask.get(i).size:ob.bid.get(i).price*ob.bid.get(i).size;
                tempAmountFromOB = tempAmountFromOB+partAmount<amount?tempAmountFromOB+partAmount:amount;
                if (tempAmountFromOB>=amount){
                    price = askOrBid=="ask"?ob.ask.get(i).price:ob.bid.get(i).price;
                }
            }
        }
        return price;
    }

    private static double getSummAmountFromOrderbook(Orderbook ob, Coin coin1, Coin coin2, Coin findCoin, double price, String askOrBid){
        double resAmount=0.0;
        int size = askOrBid=="ask"?ob.ask.size():ob.bid.size();
        if (findCoin==coin1){
            for (int i=0; i<size; i++){
                if (askOrBid=="ask"){
                    if (ob.ask.get(i).price<=price){
                        resAmount+=ob.ask.get(i).size;
                    } else {break;}
                } else{
                    if (ob.bid.get(i).price<=price){
                        resAmount+=ob.bid.get(i).size;
                    } else {break;}
                }
            }
        } else {
            for (int i=0; i>size; i++){
                if (askOrBid=="ask"){
                    double partAmount = ob.ask.get(i).price*ob.ask.get(i).size;
                    if (ob.ask.get(i).price<=price){
                        resAmount+=partAmount;
                    } else {break;}
                } else {
                    double partAmount = ob.bid.get(i).price*ob.bid.get(i).size;
                    if (ob.bid.get(i).price<=price){
                        resAmount+=partAmount;
                    } else {break;}
                }
            }
        }
        return resAmount;
    }

    // определяет первые объемы и цены для просчета профита
    private static double getFirstVolume(Orderbook obTrans1th, Orderbook obTrans2th, Orderbook obTrans3th){
        double res = obTrans1th.ask.get(0).size<obTrans2th.bid.get(0).size?obTrans1th.ask.get(0).size:obTrans2th.bid.get(0).size;
        double volQuoteCoin1thTrans = res*obTrans1th.ask.get(0).price;
        double volBaseCoin3thTrans = obTrans3th.ask.get(0).size;// то же самое что и QuoteCoin1thTrans
        if (volQuoteCoin1thTrans>volBaseCoin3thTrans){
            res=volBaseCoin3thTrans/obTrans1th.ask.get(0).price;
        }
        return res;
    }

    // на вход получает котировки по все 2м парам, а также объем базовой монеты с первой сделки, которая показала "+".
    // находит узкое горлышко в котировках и добавляет на максимальное значение, пока не достигнет следующего bottlneck котировок.
    private static double getNextBaseVolume(Pair pairTrans1th, Pair pairTrans2th, Pair pairTrans3th, Coin coinBase, Coin coinQuote1, Coin coinQuote2,
                                            Orderbook obTrans1th, Orderbook obTrans2th, Orderbook obTrans3th, double amountBaseCoin){
        double volBaseCoinTrans1=amountBaseCoin;
        double volQuoteCoin1Trnas1=getAmountSellCoinFromPair(pairTrans1th, coinBase, obTrans1th, volBaseCoinTrans1);
        double volBaseCoinTrans2=volBaseCoinTrans1;
        double volQuoteCoin2Trans2=getAmountBuyCoinFromPair(pairTrans2th, coinBase, obTrans2th, volBaseCoinTrans2);
        double volQuoteCoin2Trans3=volQuoteCoin2Trans2;
        double volQuoteCoin1Trans3=getAmountBuyCoinFromPair(pairTrans3th, coinQuote2, obTrans3th, volQuoteCoin2Trans3);

        // разница в объеме монет между значениеми просчитаными ранее, при которых есть профит и остаточным объемом в стакане котировок при той же самой цене.
        double deltaBaseCoinTrans1 = volBaseCoinTrans1 - getSummAmountFromOrderbook(obTrans1th,coinBase,coinQuote1,coinBase,getPriceFromOrderbook(obTrans1th,coinBase,coinQuote1,coinBase,volBaseCoinTrans1,"ask"),"ask");
        // может имеет смысл перевести по курсу deltaBaseCoinTrans1 = getSummAmountFromOrderbook и отнять уже объем volQuoteCoin1Trnas1 от полученного значения????
        double getPriceFromOBCoinQ1Tr1=getPriceFromOrderbook(obTrans1th, coinBase,coinQuote1,coinQuote1,volQuoteCoin1Trnas1,"ask");
        double getSummAmountFromOBCoinQ1Tr1= getSummAmountFromOrderbook(obTrans1th,coinBase,coinQuote1,coinQuote1,getPriceFromOBCoinQ1Tr1,"ask");
        System.out.println("getPriceFromOBCoinQ1Tr1: "+getPriceFromOBCoinQ1Tr1+" getSummAmountFromOBCoinQ1Tr1: "+getSummAmountFromOBCoinQ1Tr1);
        double deltaQuoteCoin1Trans1 = volQuoteCoin1Trnas1 - getSummAmountFromOrderbook(obTrans1th,coinBase,coinQuote1,coinQuote1,getPriceFromOrderbook(obTrans1th, coinBase,coinQuote1,coinQuote1,volQuoteCoin1Trnas1,"ask"),"ask");
        double deltaBaseCoinTrans2 = volBaseCoinTrans2 - getSummAmountFromOrderbook(obTrans2th,coinBase, coinQuote2, coinBase, getPriceFromOrderbook(obTrans2th, coinBase, coinQuote2, coinBase, volBaseCoinTrans2, "bid"),"bid");
        double deltaQuoteCoin2Trans2 = volQuoteCoin2Trans2 - getSummAmountFromOrderbook(obTrans2th,coinBase, coinQuote2, coinQuote2, getPriceFromOrderbook(obTrans2th, coinBase, coinQuote2, coinQuote2, volQuoteCoin2Trans2, "bid"), "bid");
        double deltaQuoteCoin2Trans3 = volQuoteCoin2Trans3 - getSummAmountFromOrderbook(obTrans3th, pairTrans3th.baseCoin==coinQuote1.id?coinQuote1:coinQuote2, pairTrans3th.quoteCoin==coinQuote2.id?coinQuote2:coinQuote1, coinQuote2,
                getPriceFromOrderbook(obTrans3th, pairTrans3th.baseCoin==coinQuote1.id?coinQuote1:coinQuote2, pairTrans3th.quoteCoin==coinQuote2.id?coinQuote2:coinQuote1, coinQuote2, volQuoteCoin2Trans3,
                        pairTrans3th.baseCoin==coinQuote1.id?"bid":"ask"), pairTrans3th.baseCoin==coinQuote1.id?"bid":"ask");
        //дичь. Как это проверить, если пара не стандартная, типа USDTBTC я хз.
        double deltaQuoteCoin1Trans3 = volQuoteCoin1Trans3 - getSummAmountFromOrderbook(obTrans3th, pairTrans3th.baseCoin==coinQuote1.id?coinQuote1:coinQuote2, pairTrans3th.quoteCoin==coinQuote2.id?coinQuote2:coinQuote1, coinQuote1,
                getPriceFromOrderbook(obTrans3th,  pairTrans3th.baseCoin==coinQuote1.id?coinQuote1:coinQuote2, pairTrans3th.quoteCoin==coinQuote2.id?coinQuote2:coinQuote1, coinQuote1, volQuoteCoin1Trans3,
                        pairTrans3th.baseCoin==coinQuote1.id?"bid":"ask"), pairTrans3th.baseCoin==coinQuote1.id?"bid":"ask");
        System.out.println("|getNextBaseVolume|");
        System.out.println("volBaseCoinTrans1: "+volBaseCoinTrans1+"\tdeltaBaseCoinTrans1: "+deltaBaseCoinTrans1);
        System.out.println("volQuoteCoin1Trnas1: "+volQuoteCoin1Trnas1+"\tdeltaQuoteCoin1Trans1: "+deltaQuoteCoin1Trans1);
        System.out.println("volBaseCoinTrans2: "+volBaseCoinTrans2+"\tdeltaBaseCoinTrans2: "+deltaBaseCoinTrans2);
        System.out.println("volQuoteCoin2Trans2: "+volQuoteCoin2Trans2+"\tdeltaQuoteCoin2Trans2: "+deltaQuoteCoin2Trans2);
        System.out.println("volQuoteCoin2Trans3: "+volQuoteCoin2Trans3+"\tdeltaQuoteCoin2Trans3: "+deltaQuoteCoin2Trans3);
        System.out.println("volQuoteCoin1Trans3: "+volQuoteCoin1Trans3+"\tdeltaQuoteCoin1Trans3: "+deltaQuoteCoin1Trans3);
        return 0.0;
    }

    // возвращает true если coin в паре стоит первым, т.е. является базовым, который торгуется к чему-то крупному типа BTC, ETH....
    public static boolean firstCoinOnPair(Pair pair, Coin coin){
        if (pair.baseCoin==coin.id){
            return true;
        }
        else return false;
    }





}
