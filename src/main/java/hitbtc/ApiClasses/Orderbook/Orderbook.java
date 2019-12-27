package hitbtc.ApiClasses.Orderbook;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;

public class Orderbook {

    public PriceSize[] ask;
    public PriceSize[] bid;
    public Timestamp timestamp;

    public Orderbook(String symbol, int limit) throws IOException {
        String url = new StringBuilder().append("https://api.hitbtc.com/api/2/public/orderbook/").append(symbol).append("?limit=").append(limit).toString();
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
        Orderbook tempOrderBook = jsonToClass(response.toString());
        this.ask = tempOrderBook.ask;
        this.bid = tempOrderBook.bid;
        this.timestamp = tempOrderBook.timestamp;
    }

    private Orderbook jsonToClass(String json){
        Gson g = new Gson();
        Orderbook ob = g.fromJson(json, Orderbook.class);
        return ob;
    }

    public String toString(){
        String ask = "";
        String bid = "";
        for(int i=0; i<this.ask.length; i++){
            ask += "price:"+this.ask[i].price+" size:"+this.ask[i].size+" ";
            bid += "bid:"+this.bid[i].price+" size:"+this.bid[i].size+" ";
        }
        
        return new StringBuilder().append("ask:").append(ask).append("\t").append("bid:").append(bid).append("\t")
                .append("timestamp").append(this.timestamp).append("\n").toString();
    }



}

