package hitbtc.ApiClasses.Ticker;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Ticker {
    public HashMap<String,TickerSymbol> tickers = new HashMap<String, TickerSymbol>();
    public static final String APIURL = "https://api.hitbtc.com/api/2/public/ticker";

    public Ticker(TickerSymbol[] arrTickerSymbol){
        for(TickerSymbol ts : arrTickerSymbol){
            this.tickers.put(ts.symbol,ts);
        }
    }

    public Ticker() throws IOException {
        URL obj = new URL(APIURL);
        HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
        connection.setRequestMethod("GET");
        BufferedReader in = null;
        StringBuffer respond = new StringBuffer();
        String line;
        try{
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            while((line=in.readLine()) != null){
                respond.append(line);
            }
            in.close();
        } catch (Exception e){}
        TickerSymbol[] arrTickerSymbol = getArrTickerSymbol(respond.toString());
        for(TickerSymbol ts : arrTickerSymbol){
            this.tickers.put(ts.symbol,ts);
        }
    }

    private TickerSymbol[] getArrTickerSymbol(String json){
        Gson gson = new Gson();
        return gson.fromJson(json, TickerSymbol[].class);
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        for(Map.Entry<String,TickerSymbol> entry : this.tickers.entrySet()){
            sb.append(entry.getKey()).append(":\t").append(entry.getValue().toString()).append("\n");
        }
        return sb.toString();
    }
}

