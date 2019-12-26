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
                System.out.println("WriteDataToDB.toDBCoin| Insert Done");
            } else {
                System.out.println("WriteDataToDB.toDBCoin| Insert fail!!!!");
            }
        } else {
            System.out.println("WriteDataToDB.toDBCoin| Update Done");
        }
    }

    public static void toDBCoins(Currencys currencys) throws SQLException {
        for(HashMap.Entry<String,Currency> coin : currencys.currencys.entrySet()){
            toDBCoin(coin.getValue());
        }
    }

    public static void toDBPair(Symbol symbol) throws SQLException, IOException {
        String baseCoin = symbol.baseCurrency;
        String quoteCoin = symbol.quoteCurrency;
        String feeCoin = symbol.feeCurrency;
        String selectBaseCoin=new StringBuilder().append("SELECT id FROM exchange.coins where abbreviation=\"").append(baseCoin).append("\"").toString();
        String selectQuoteCoin=new StringBuilder().append("SELECT id FROM exchange.coins where abbreviation=\"").append(quoteCoin).append("\"").toString();
        String selectFeeCoin=new StringBuilder().append("SELECT id FROM exchange.coins where abbreviation=\"").append(feeCoin).append("\"").toString();
        ArrayList<HashMap> rsBaseCoin = DBconnactionVPS.getResultSet(selectBaseCoin);
        ArrayList<HashMap> rsQuoteCoin = DBconnactionVPS.getResultSet(selectQuoteCoin);
        ArrayList<HashMap> rsFeeCoin = DBconnactionVPS.getResultSet(selectFeeCoin);
        if (rsBaseCoin.size()==0){
            toDBCoin(new Currency(baseCoin));
            rsBaseCoin = DBconnactionVPS.getResultSet(selectBaseCoin);
        }
        int idBaseCoin = GetVal.getInt(rsBaseCoin.get(0),"id");
        if (rsQuoteCoin.size()==0) {
            toDBCoin(new Currency(quoteCoin));
            rsQuoteCoin = DBconnactionVPS.getResultSet(selectQuoteCoin);
        }
        int idQuoteCoin = GetVal.getInt(rsQuoteCoin.get(0),"id");
        if (rsFeeCoin.size()==0) {
            toDBCoin(new Currency(feeCoin));
            rsFeeCoin = DBconnactionVPS.getResultSet(selectFeeCoin);
        }
        int idFeeCoin = GetVal.getInt(rsFeeCoin.get(0),"id");
        if (idBaseCoin>0 & idQuoteCoin>0 & idFeeCoin>0){
            String update = new StringBuilder().append("UPDATE exchange.pairs SET baseCoin=").append(idBaseCoin).append(", quoteCoin =").append(idQuoteCoin).append(", feeCoin =")
                    .append(idFeeCoin).append(" WHERE exForm=\"").append(symbol.id).append("\"").toString();
            String insert = new StringBuilder().append("INSERT INTO exchange.pairs (baseCoin, quoteCoin, feeCoin, exForm) VALUES (").append(idBaseCoin).append(", ")
                    .append(idQuoteCoin).append(", ").append(idFeeCoin).append(", \"").append(symbol.id).append("\")").toString();
            if (DBconnactionVPS.executeQueryInt(update)<1){
                if (DBconnactionVPS.executeQueryInt(insert)==1){
                    System.out.println("WriteDataToDB.toDBPair| Insert Done");
                } else {
                    System.out.println("WriteDataToDB.toDBPair| Insert fail!!!");
                }
            } else {
                    System.out.println("WriteDataToDB.toDBPair| Update Done");
            }
        }
    }

    public static void toDBPairs(Symbols symbols) throws IOException, SQLException {
        for(HashMap.Entry<String,Symbol> entry : symbols.symbols.entrySet()){
            toDBPair(entry.getValue());
        }
    }

    public static void toDBHitbtc(TickerSymbol tickerSymbol) throws SQLException, IOException {
        String strPair = tickerSymbol.symbol;
        String selectIdPair = new StringBuilder().append("SELECT id FROM exchange.pairs where exForm=\"").append(strPair).append("\"").toString();
        ArrayList<HashMap> rsIdPair = DBconnactionVPS.getResultSet(selectIdPair);
        if (rsIdPair.size()<1){
            toDBPair(new Symbol(strPair));
            rsIdPair = DBconnactionVPS.getResultSet(selectIdPair);
        }
        int idPair = GetVal.getInt(rsIdPair.get(0),"id");
        String update = new StringBuilder().append("UPDATE exchange.hitbtc SET ask1=").append(tickerSymbol.ask).append(", bid1=").append(tickerSymbol.bid)
                .append(", date=now() where id=").append(idPair).toString();
        String insert = new StringBuilder().append("INSERT INTO exchange.hitbtc (pair, bid1, ask1, date) VALUES (").append(idPair).append(", ").append(tickerSymbol.bid)
                .append(", ").append(tickerSymbol.ask).append(", now())").toString();
        if (DBconnactionVPS.executeQueryInt(update)<1){
            if (DBconnactionVPS.executeQueryInt(insert)==1){
                System.out.println("WriteDataToDB.toDBHitbtc| Insert Done");
            } else {
                System.out.println("WriteDataToDB.toDBHitbtc| Insert fail!!!");
            }
        } else {
            System.out.println("WriteDataToDB.toDBHitbtc| Update Done");
        }
    }

    public static void toDBHitbtcAll(Ticker ticker) throws IOException, SQLException {
        for (HashMap.Entry<String, TickerSymbol> entry : ticker.tickers.entrySet()){
            toDBHitbtc(entry.getValue());
        }
    }

}
