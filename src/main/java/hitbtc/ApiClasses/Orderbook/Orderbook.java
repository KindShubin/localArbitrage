package hitbtc.ApiClasses.Orderbook;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;

public class Orderbook {

    public PriceSize[] ask=null;
    public PriceSize[] bid=null;
    public Timestamp timestamp=null;

    public Orderbook(String symbol, int limit) throws IOException {
        String url = new StringBuilder().append("https://api.hitbtc.com/api/2/public/orderbook/").append(symbol).append("?limit=").append(limit).toString();
        URL obj = new URL(url);
        String inputLine;
        StringBuffer response = new StringBuffer();
        try{
        HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
        connection.setRequestMethod("GET");
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        } catch (Exception e){
            System.out.println("Orderbook get api fail:");
            System.out.println(e.toString());
        }
        try{
        Orderbook tempOrderBook = jsonToClass(response.toString());
        this.ask = tempOrderBook.ask;
        this.bid = tempOrderBook.bid;
        this.timestamp = tempOrderBook.timestamp;
        } catch (Exception e){
            System.out.println("Orderbook deserialization fail:");
            e.toString();
        }
    }

    private Orderbook jsonToClass(String json){
        Gson g = new Gson();
        Orderbook ob = g.fromJson(json, Orderbook.class);
        return ob;
    }

    public String toString(){
        String ask = "";
        String bid = "";
        if (this.ask != null){
            for(int i=0; i<this.ask.length; i++){
                ask += "("+(i+1)+")price:"+this.ask[i].price+",size:"+this.ask[i].size+" ";
                bid += "("+(i+1)+")price:"+this.bid[i].price+",size:"+this.bid[i].size+" ";
            }
        }
        return new StringBuilder().append("ask: ").append(ask).append("\n").append("bid: ").append(bid).append("\n")
                .append("timestamp").append(this.timestamp).append("\n").toString();
    }

    public String toString1(){
        return new StringBuilder().append("ask:").append(this.ask.toString()).append("\t").append("bid:").append(this.bid.toString()).append("\t")
                .append("timestamp").append(this.timestamp).append("\n").toString();
    }



}

