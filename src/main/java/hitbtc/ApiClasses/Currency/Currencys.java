package hitbtc.ApiClasses.Currency;
//Symbol - пара на бирже: BCNBTC, ETHBTC ...
//Currency - конкретная монета: BCN, BTC
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class Currencys {

    public HashMap<String,Currency> currencys;
    public static final String APIURL = "https://api.hitbtc.com/api/2/public/currency";

    public Currencys() throws IOException {
        URL obj = new URL(APIURL);
        HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
        connection.setRequestMethod("GET");
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        Currency[] arrCurrency = getArrCurrencysFromJson(response.toString());
        for (Currency curr : arrCurrency){
            this.currencys.put(curr.id,curr);
        }
    }

    public Currency[] getArrCurrencysFromJson(String json){
        Gson g = new Gson();
        Currency[] arrCurrency =  g.fromJson(json, Currency[].class);
        return arrCurrency;
    }

    public String toString(){
        StringBuilder sb= new StringBuilder();
        for (Map.Entry<String,Currency> entry : this.currencys.entrySet()){
            sb.append(entry.getKey()).append(":\t").append(entry.getValue().toString()).append("\n");
        }
        return sb.toString();
    }

}
