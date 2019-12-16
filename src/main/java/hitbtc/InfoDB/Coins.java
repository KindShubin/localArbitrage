package hitbtc.InfoDB;

import DB.DBconnactionVPS;
import DB.GetVal;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Coins {
    // integer - id из Coin
    public Map<Integer, Coin> coins;

    public Coins(){
        String select = "SELECT id, abbreviation, name, description FROM exchange.coins order by id;";
        new Coins(select);
    }

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

    public void print(){
        for(Map.Entry<Integer, Coin> entry : coins.entrySet()){
            String res = new StringBuilder().append("id_coin:").append(entry.getKey()).append("\tCoin:").append(entry.getValue().toString()).toString();
            System.out.println(res);
        }
    }
}

class Coin {
    public int id;
    public String abbreviation;
    public String name;
    public String description;

    Coin(int id, String abbreviation, String name, String description){
        this.id = id;
        this.abbreviation = abbreviation;
        this.name = name;
        this.description = description;
    }

    public String toString(){
        String result = new StringBuilder().append("id:").append(id).append("\tabbreviation:").append(abbreviation)
                .append("\tname:").append(name).append("\tdescription").append(description).toString();
        return result;
    }

}
