package hitbtc.ApiClasses.Symbol;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

//Symbol - пара на бирже: BCNBTC, ETHBTC ...
//Currency - конкретная монета: BCN, BTC
public class Symbol {

    public String id;
    public String baseCurrency;
    public String quoteCurrency;
    public Double quantityIncrement;
    public Double tickSize;
    public Double takeLiquidityRate;
    public Double provideLiquidityRate;
    public String feeCurrency;

    public Symbol(String id, String baseCurrency, String quoteCurrency, Double quantityIncrement, Double tickSize, Double takeLiquidityRate, Double provideLiquidityRate, String feeCurrency){
        this.id=id;
        this.baseCurrency=baseCurrency;
        this.quoteCurrency=quoteCurrency;
        this.quantityIncrement=quantityIncrement;
        this.tickSize=tickSize;
        this.takeLiquidityRate=takeLiquidityRate;
        this.provideLiquidityRate=provideLiquidityRate;
        this.feeCurrency=feeCurrency;
    }

    public Symbol(String strSymbol) throws IOException {
        String url = new StringBuilder().append("https://api.hitbtc.com/api/2/public/symbol/").append(strSymbol).toString();
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
        } catch (Exception e){

        }
        Gson g = new Gson();
        try{
            Symbol tempSymbol =  g.fromJson(response.toString(), Symbol.class);
            this.id=tempSymbol.id;
            this.baseCurrency=tempSymbol.baseCurrency;
            this.quoteCurrency=tempSymbol.quoteCurrency;
            this.quantityIncrement=tempSymbol.quantityIncrement;
            this.tickSize=tempSymbol.tickSize;
            this.takeLiquidityRate=tempSymbol.takeLiquidityRate;
            this.provideLiquidityRate=tempSymbol.provideLiquidityRate;
            this.feeCurrency=tempSymbol.feeCurrency;
        } catch (Exception e){
        this.id=null;
        this.baseCurrency=null;
        this.quoteCurrency=null;
        this.quantityIncrement=null;
        this.tickSize=null;
        this.takeLiquidityRate=null;
        this.provideLiquidityRate=null;
        this.feeCurrency=null;
        }
    }

    public String toString(){
        return new StringBuilder().append("id:").append(this.id).append("\tbaseCurrency:").append(this.baseCurrency).append("\tquoteCurrency:").append(this.quoteCurrency)
                .append("\tquantityIncrement:").append(this.quantityIncrement).append("\ttickSize:").append(this.tickSize).append("\ttakeLiquidityRate:").append(this.takeLiquidityRate)
                .append("\tprovideLiquidityRate:").append(this.provideLiquidityRate).append("\tfeeCurrency:").append(this.feeCurrency).toString();
    }

}
