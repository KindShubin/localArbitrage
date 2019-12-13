package hitbtc;

import java.sql.Timestamp;
import java.util.List;

class Ticker {
    public List<TickerSymbol> listTickerSymbols;
}

class TickerSymbol {
    public double ask;
    public double bid;
    public double last;
    public double open;
    public double low;
    public double high;
    public double volume;
    public double volumeQuote;
    public Timestamp timestamp;
    public String symbol;

    public void print(){
        System.out.printf("symbol:%s\task:%s\tbid:%s\tlast:%s\topen:%s\tlow:%s\thigh:%s\tvolume:%s\tvolumeQuote:%s\ttimestamp:%s\n",symbol,ask,bid,last,open,low,high,volume,volumeQuote,timestamp);
    }
}