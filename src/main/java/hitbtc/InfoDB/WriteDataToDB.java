package hitbtc.InfoDB;

import DB.DBconnactionVPS;
import DB.GetVal;
import hitbtc.ApiClasses.Currency.Currency;
import hitbtc.ApiClasses.Currency.Currencys;
import hitbtc.ApiClasses.Symbol.Symbol;
import hitbtc.ApiClasses.Symbol.Symbols;
import hitbtc.ApiClasses.Ticker.Ticker;
import hitbtc.ApiClasses.Ticker.TickerSymbol;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class WriteDataToDB {

    public static void toDBCoin(Currency currency) throws SQLException {
        String update = new StringBuilder().append("UPDATE exchange.coins SET name = \"").append(currency.fullName).append("\" WHERE abbreviation = \"").append(currency.id).append("\"").toString();
        String insert = new StringBuilder().append("INSERT INTO exchange.coins (abbreviation, name) VALUES (\"").append(currency.id).append("\", \"").append(currency.fullName).append("\")").toString();
        if (DBconnactionVPS.executeQueryInt(update)<1){
            if (DBconnactionVPS.executeQueryInt(insert)==1){
                System.out.printf("|WriteDataToDB.toDBCoin| %s Insert Done\n", currency.id);
            } else {
                System.out.printf("|WriteDataToDB.toDBCoin| %s Insert fail!!!!\n", currency.id);
            }
        } else {
            System.out.printf("|WriteDataToDB.toDBCoin| %s Update Done\n", currency.id);
        }
    }

    public static void toDBCoins(Currencys currencys) throws SQLException {
        for(HashMap.Entry<String,Currency> coin : currencys.currencys.entrySet()){
            toDBCoin(coin.getValue());
        }
    }

    // в api "symbol" - это пара коинов, например LTCETH с полями baseCurrency, quoteCurrency и т.д. В базе это Pair
    public static void toDBPair(Symbol symbol) throws SQLException, IOException {
        String baseCoin = symbol.baseCurrency;
        String quoteCoin = symbol.quoteCurrency;
        String feeCoin = symbol.feeCurrency;
        String selectBaseCoin=new StringBuilder().append("SELECT id FROM exchange.coins where abbreviation=\"").append(baseCoin).append("\"").toString();
        String selectQuoteCoin=new StringBuilder().append("SELECT id FROM exchange.coins where abbreviation=\"").append(quoteCoin).append("\"").toString();
        String selectFeeCoin=new StringBuilder().append("SELECT id FROM exchange.coins where abbreviation=\"").append(feeCoin).append("\"").toString();
        int idBaseCoin=0;
        int idQuoteCoin=0;
        int idFeeCoin=0;
        try{
            ArrayList<HashMap> rsBaseCoin = DBconnactionVPS.getResultSet(selectBaseCoin);
            if (rsBaseCoin.size()==0){
                toDBCoin(new Currency(baseCoin));
                rsBaseCoin = DBconnactionVPS.getResultSet(selectBaseCoin);
            }
            idBaseCoin = GetVal.getInt(rsBaseCoin.get(0),"id");
        }
        catch (Exception e){
            e.toString();
        }
        try{
            ArrayList<HashMap> rsQuoteCoin = DBconnactionVPS.getResultSet(selectQuoteCoin);
            if (rsQuoteCoin.size()==0) {
                toDBCoin(new Currency(quoteCoin));
                rsQuoteCoin = DBconnactionVPS.getResultSet(selectQuoteCoin);
            }
            idQuoteCoin = GetVal.getInt(rsQuoteCoin.get(0),"id");
        }
        catch (Exception e){
            e.toString();
        }
        try{
            ArrayList<HashMap> rsFeeCoin = DBconnactionVPS.getResultSet(selectFeeCoin);
            if (rsFeeCoin.size()==0) {
                toDBCoin(new Currency(feeCoin));
                rsFeeCoin = DBconnactionVPS.getResultSet(selectFeeCoin);
            }
            idFeeCoin = GetVal.getInt(rsFeeCoin.get(0),"id");
        }catch (Exception e){
            e.toString();
        }
        if (idBaseCoin>0 & idQuoteCoin>0 & idFeeCoin>0 & symbol.id!=null){
            String update = new StringBuilder().append("UPDATE exchange.pairs SET baseCoin=").append(idBaseCoin).append(", quoteCoin =").append(idQuoteCoin).append(", feeCoin =")
                    .append(idFeeCoin).append(" WHERE exForm=\"").append(symbol.id).append("\"").toString();
            String insert = new StringBuilder().append("INSERT INTO exchange.pairs (baseCoin, quoteCoin, feeCoin, exForm) VALUES (").append(idBaseCoin).append(", ")
                    .append(idQuoteCoin).append(", ").append(idFeeCoin).append(", \"").append(symbol.id).append("\")").toString();
//            int g = DBconnactionVPS.executeQueryInt(update);
//            System.out.println("g="+g);
            if (DBconnactionVPS.executeQueryInt(update)<1){
                if (DBconnactionVPS.executeQueryInt(insert)==1){
                    System.out.printf("|WriteDataToDB.toDBPair| %s Insert Done\n", symbol.id);
                } else {
                    System.out.printf("|WriteDataToDB.toDBPair| %s Insert fail!!!\n", symbol.id);
                }
            } else {
                    System.out.printf("|WriteDataToDB.toDBPair| %s Update Done\n", symbol.id);
            }
        } else { System.out.printf("|WriteDataToDB.toDBPair| %s update and insert didn't start\n", symbol.id);}
    }

    public static void toDBPairs(Symbols symbols) throws IOException, SQLException {
        for(HashMap.Entry<String,Symbol> entry : symbols.symbols.entrySet()){
            toDBPair(entry.getValue());
        }
    }

    public static void toDBHitbtc(TickerSymbol tickerSymbol) throws SQLException, IOException {
        String strPair = tickerSymbol.symbol;//"ETHUSD"...
        String selectIdPair = new StringBuilder().append("SELECT id FROM exchange.pairs where exForm=\"").append(strPair).append("\"").toString();
        ArrayList<HashMap> rsIdPair = DBconnactionVPS.getResultSet(selectIdPair);
        if (rsIdPair.size()<1){
            toDBPair(new Symbol(strPair));
            rsIdPair = DBconnactionVPS.getResultSet(selectIdPair);
        }
        if (rsIdPair.size()>0){
            int idPair = GetVal.getInt(rsIdPair.get(0),"id");
            double bid1=tickerSymbol.bid>0?tickerSymbol.bid:0.0;// на случай если стакан заявок пуст
            double ask1=tickerSymbol.ask>0?tickerSymbol.ask:0.0;// на случай если стакан заявок пуст
            String update = new StringBuilder().append("UPDATE exchange.hitbtc SET ask1=").append(ask1).append(", bid1=").append(bid1)
                    .append(", date=now() where pair=").append(idPair).toString();
            String insert = new StringBuilder().append("INSERT INTO exchange.hitbtc (pair, bid1, ask1, date) VALUES (").append(idPair).append(", ").append(bid1)
                    .append(", ").append(ask1).append(", now())").toString();
            if (DBconnactionVPS.executeQueryInt(update)<1){
                if (DBconnactionVPS.executeQueryInt(insert)==1){
                    //System.out.println("|WriteDataToDB.toDBHitbtc| insert:"+insert);
                    System.out.printf("|WriteDataToDB.toDBHitbtc| %s Insert Done\n", strPair);
                } else {
                    System.out.printf("|WriteDataToDB.toDBHitbtc| %s Updste fail. Insert fail!!!\n", strPair);
                }
            } else {
                //System.out.println("|WriteDataToDB.toDBHitbtc| update:"+update);
                System.out.printf("|WriteDataToDB.toDBHitbtc| %s Update Done\n", strPair);
            }
        } else { System.out.printf("|WriteDataToDB.toDBHitbtc| %s update and insert didn't start\n", strPair);}
    }

    public static void toDBHitbtcAll(Ticker ticker) throws IOException, SQLException {
        System.out.println("|WriteDataToDB.toDBHitbtcAll| Processing...");
        for (HashMap.Entry<String, TickerSymbol> entry : ticker.tickers.entrySet()){
            toDBHitbtc(entry.getValue());
        }
        System.out.println("|WriteDataToDB.toDBHitbtcAll| Write finish");
    }

}
