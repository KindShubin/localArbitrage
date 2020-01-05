package hitbtc.ApiClasses.Orderbook;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;

public class Orderbook {

    public ArrayList<PriceSize> ask;
    public ArrayList<PriceSize> bid;
    public Timestamp timestamp=null;

    public Orderbook(){
        this.ask=new ArrayList<PriceSize>();
        this.bid=new ArrayList<PriceSize>();
        this.timestamp=null;
    }

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
        for (int i=0; i<limit; i++){
            if(this.ask.size()<(i+1) || this.ask==null){
                this.ask.add(new PriceSize(0.0, 0.0));
            }
            if(this.bid.size()<(i+1) || this.bid==null){
                this.bid.add(new PriceSize(0.0, 0.0));
            }
        }

    }

    private Orderbook jsonToClass(String json){
        Gson g = new Gson();
        Orderbook ob;
        try{
            ob = g.fromJson(json, Orderbook.class);
        }catch (Exception e){
            System.out.println("|Orderbook.jsonToClass| Error:");
            e.toString();
            ob = new Orderbook();
            try{
                System.out.println("ask.size"+ob.ask.size());
            }catch (Exception e1){
                System.out.println("ask.size error");
                e1.toString();
            }
            try{
                System.out.println("bid.size"+ob.bid.size());
            }catch (Exception e2){
                System.out.println("bid.size error");
                e2.toString();
            }
        }
        return ob;
    }

    public String toString(){
        String ask = "";
        String bid = "";
        if (this.ask != null){
            for(int i=0; i<this.ask.size(); i++){
                ask += "("+(i+1)+")price:"+this.ask.get(i).price+",size:"+this.ask.get(i).size+" ";
                bid += "("+(i+1)+")price:"+this.bid.get(i).price+",size:"+this.bid.get(i).size+" ";
            }
        }
        return new StringBuilder().append("ask: ").append(ask).append("\n").append("bid: ").append(bid).append("\n")
                .append("timestamp: ").append(this.timestamp).append("\n").toString();
    }

    public String toString1(){
        String ask;
        String bid;
        ask=(this.ask==null)?"":this.ask.toString();
        bid=this.bid==null?"":this.bid.toString();
        return new StringBuilder().append("ask:").append(ask).append("\t").append("bid:").append(bid).append("\t")
                .append("timestamp").append(this.timestamp).append("\n").toString();
    }



}

