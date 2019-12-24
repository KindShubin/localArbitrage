package hitbtc.ApiClasses.Symbol;
//Symbol - пара на бирже: BCNBTC, ETHBTC ...
//Currency - конкретная монета: BCN, BTC
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Symbols {

    public static final String APIURL = "https://api.hitbtc.com/api/2/public/symbol";
    public HashMap<String,Symbol> symbols;

    public Symbols(ArrayList<Symbol> arrSymols){
        for(Symbol smbl : arrSymols){
            this.symbols.put(smbl.id,smbl);
        }
    }

    public Symbols() throws IOException {
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
        Symbol[] arrSymbol = jsonParseToArraySymbol(response.toString());
        for (Symbol smbl : arrSymbol){
            this.symbols.put(smbl.id,smbl);
        }
    }

    public static Symbol[] jsonParseToArraySymbol(String json) {
        Gson g = new Gson();
        Symbol[] arrSymbol = g.fromJson(json, Symbol[].class);
        return arrSymbol;
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        for(Map.Entry<String,Symbol> entry: this.symbols.entrySet()){
            sb.append(entry.getKey()).append(":\t").append(entry.getValue().toString()).append("\n");
        }
        return sb.toString();
    }


}
