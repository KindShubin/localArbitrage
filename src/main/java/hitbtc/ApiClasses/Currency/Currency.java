package hitbtc.ApiClasses.Currency;
//Symbol - пара на бирже: BCNBTC, ETHBTC ...
//Currency - конкретная монета: BCN, BTC
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Currency {

    public String id;
    public String fullName;
    public boolean crypto;
    public boolean payinEnabled;
    public boolean payinPaymentId;
    public int payinConfirmations;
    public boolean payoutEnabled;
    public boolean payoutIsPaymentId;
    public boolean transferEnabled;
    public boolean delisted;
    public Double payoutFee;

    public Currency(String id, String fullName, boolean crypto, boolean payinEnabled, boolean payinPaymentId, int payinConfirmations,
                    boolean payoutEnabled, boolean payoutIsPaymentId, boolean transferEnabled, boolean delisted, Double payoutFee){
        this.id=id;
        this.fullName=fullName;
        this.crypto=crypto;
        this.payinEnabled=payinEnabled;
        this.payinPaymentId=payinPaymentId;
        this.payinConfirmations=payinConfirmations;
        this.payoutEnabled=payoutEnabled;
        this.payoutIsPaymentId=payoutIsPaymentId;
        this.transferEnabled=transferEnabled;
        this.delisted=delisted;
        this.payoutFee=payoutFee;
    }

    public Currency(String strCurrency) throws IOException {
        String url = new StringBuilder().append("https://api.hitbtc.com/api/2/public/currency/").append(strCurrency).toString();
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
        Gson g = new Gson();
        Currency tempCurrency =  g.fromJson(response.toString(), Currency.class);
        this.id=tempCurrency.id;
        this.fullName=tempCurrency.fullName;
        this.crypto=tempCurrency.crypto;
        this.payinEnabled=tempCurrency.payinEnabled;
        this.payinPaymentId=tempCurrency.payinPaymentId;
        this.payinConfirmations=tempCurrency.payinConfirmations;
        this.payoutEnabled=tempCurrency.payoutEnabled;
        this.payoutIsPaymentId=tempCurrency.payoutIsPaymentId;
        this.transferEnabled=tempCurrency.transferEnabled;
        this.delisted=tempCurrency.delisted;
        this.payoutFee=tempCurrency.payoutFee;
    }

    public String toString(){
        return new StringBuilder().append("id:").append(id).append("|fullName:").append(fullName).append("|crypto:").append(crypto).append("|payinEnabled:")
                .append(payinEnabled).append("|payinPaymentId:").append(payinPaymentId).append("|payinConfirmations:").append(payinConfirmations)
                .append("|payoutEnabled:").append(payoutEnabled).append("|payoutIsPaymentId:").append(payoutIsPaymentId).append("|transferEnabled:")
                .append(transferEnabled).append("|delisted:").append(delisted).append("|payoutFee:").append(payoutFee).toString();
    }


}
