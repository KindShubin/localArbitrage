package hitbtc.InfoDB;

import DB.DBconnactionVPS;
import DB.GetVal;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Pairs {

    public static final String SELECT = "SELECT id, baseCoin, quoteCoin, feeCoin, exForm FROM exchange.pairs;";
    public Map<Integer, Pair> pairs;

    public Pairs(String select){
        this.pairs=new HashMap<>();
        ArrayList<HashMap> resDB = null;
        try {
            resDB = DBconnactionVPS.getResultSet(select);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        for (HashMap hm : resDB){
            int id = GetVal.getInt(hm, "id");
            int baseCoin = GetVal.getInt(hm, "baseCoin");
            int quoteCoin = GetVal.getInt(hm, "quoteCoin");
            int feeCoin = GetVal.getInt(hm, "feeCoin");
            String exForm = GetVal.getStr(hm, "exForm");
            this.pairs.put(id,new Pair(id,baseCoin,quoteCoin,feeCoin,exForm));
        }
    }

    public Pairs(){
        this(SELECT);
    }

    public void print(){
        for(Map.Entry<Integer, Pair> entry : this.pairs.entrySet()){
            String str = new StringBuilder().append("id_Pair:").append(entry.getKey()).append("\tPair:\t").append(entry.getValue().toString()).toString();
            System.out.println(str);
        }
    }

}

class Pair{
    public int id;
    public int baseCoin;
    public int quoteCoin;
    public int feeCoin;
    public String exForm;

    public Pair(int id, int baseCoin, int quoteCoin, int feeCoin, String exForm){
        this.id=id;
        this.baseCoin=baseCoin;
        this.quoteCoin=quoteCoin;
        this.feeCoin=feeCoin;
        this.exForm=exForm;
    }

    public String toString(){
        String result = new StringBuilder().append("id:").append(id).append("\tbaseCoin:").append(baseCoin).append("\tquoteCoin").append(quoteCoin)
                .append("\tfeeCoin:").append(feeCoin).append("\texForm:").append(exForm).toString();
        return result;
    }
}