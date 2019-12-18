package hitbtc.InfoDB.Coins;

import DB.DBconnactionVPS;
import DB.GetVal;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Coins {
    // integer - id из Coin
    public static final String SELECT = "SELECT id, abbreviation, name, description FROM exchange.coins order by id;";
    public Map<Integer, Coin> coins;

    public Coins(String select){
        Map<Integer, Coin> coins = new HashMap<>();
        ArrayList<HashMap> resultDB = null;
        try {
            resultDB = DBconnactionVPS.getResultSet(select);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        for (HashMap hm : resultDB){
            int id = GetVal.getInt(hm,"id");
            String abbreviation = GetVal.getStr(hm,"abbreviation");
            String name = GetVal.getStr(hm, "name");
            String description = GetVal.getStr(hm, "description");
            coins.put(id,new Coin(id,abbreviation,name,description));
        }
        this.coins=coins;
    }

    public Coins(){
        this(SELECT);
    }

    public void print(){
        for(Map.Entry<Integer, Coin> entry : this.coins.entrySet()){
            String res = new StringBuilder().append("id_coin:").append(entry.getKey()).append("\tCoin:\t").append(entry.getValue().toString()).toString();
            System.out.println(res);
        }
    }

    public String getAbbr(int id){
        return this.coins.get(id).abbreviation;
    }
}

