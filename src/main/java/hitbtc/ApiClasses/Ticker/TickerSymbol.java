package hitbtc.ApiClasses.Ticker;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;

public class TickerSymbol {
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

    public TickerSymbol(double ask, double bid, double last, double open, double low, double high, double volume, double volumeQuote, Timestamp timestamp, String symbol){
        this.ask=ask;
        this.bid=bid;
        this.last=last;
        this.open=open;
        this.low=low;
        this.high=high;
        this.volume=volume;
        this.volumeQuote=volumeQuote;
        this.timestamp=timestamp;
        this.symbol=symbol;
    }

    public TickerSymbol(String strTickerSymbol) throws IOException {
        String url = new StringBuilder().append("https://api.hitbtc.com/api/2/public/ticker/").append(strTickerSymbol).toString();
        URL obj = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
        connection.setRequestMethod("GET");
        BufferedReader in = null;
        String inputLine;
        StringBuffer response = new StringBuffer();
        try{
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
        } catch (Exception e){}
        Gson g = new Gson();
        try{
            TickerSymbol tempTickerSymbol =  g.fromJson(response.toString(), TickerSymbol.class);
            this.ask=tempTickerSymbol.ask;
            this.bid=tempTickerSymbol.bid;
            this.last=tempTickerSymbol.last;
            this.open=tempTickerSymbol.open;
            this.low=tempTickerSymbol.low;
            this.high=tempTickerSymbol.high;
            this.volume=tempTickerSymbol.volume;
            this.volumeQuote=tempTickerSymbol.volumeQuote;
            this.timestamp=tempTickerSymbol.timestamp;
            this.symbol=tempTickerSymbol.symbol;
        } catch (Exception e){}
        this.ask=-1.0;
        this.bid=-1.0;
        this.last=-1.0;
        this.open=-1.0;
        this.low=-1.0;
        this.high=-1.0;
        this.volume=-1.0;
        this.volumeQuote=-1.0;
        this.timestamp=null;
        this.symbol=null;

    }

    public String toString(){
        return new StringBuilder().append("symbol:").append(this.symbol).append("|ask:").append(this.ask).append("|bid:").append(this.bid).append("|last:").append(this.last).append("|open:").append(this.open)
                .append("|low:").append(this.low).append("|high:").append(this.high).append("|volume:").append(this.volume).append("|volumeQuote:").append(this.volumeQuote)
                .append("|timestamp:").append(this.timestamp).toString();
    }
}
