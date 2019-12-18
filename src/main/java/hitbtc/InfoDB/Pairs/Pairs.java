package hitbtc.InfoDB.Pairs;

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

    public ArrayList<Integer> getArrQuoteCoinsForBaseCoin(int baseCoin){
        ArrayList<Integer> arrRes = new ArrayList<>();
        for(Map.Entry<Integer, Pair> mapP : this.pairs.entrySet()){
            if (mapP.getValue().baseCoin==baseCoin){
                arrRes.add(mapP.getValue().quoteCoin);
            }
        }
        return arrRes;
    }

    public int getPair(int coin1, int coin2){
        int res = 0;
        for (Map.Entry<Integer,Pair> entry : this.pairs.entrySet()){
            if (entry.getValue().baseCoin==coin1 & entry.getValue().quoteCoin==coin2){
                res = entry.getKey();
                break;
            }
            if (entry.getValue().baseCoin==coin2 & entry.getValue().quoteCoin==coin1){
                res = entry.getKey();
                break;
            }
        }
        return res;
    }

    public String getExForm(int id){
        try {
            return this.pairs.get(id).exForm;
        }catch (Exception e){
            return "-";
        }
    }

    public int getBaseCoin(int pair){
        return this.pairs.get(pair).baseCoin;
    }

    public int getQuoteCoin(int pair){
        return this.pairs.get(pair).quoteCoin;
    }

    public int geFeeCoin(int pair){
        return this.pairs.get(pair).feeCoin;
    }

}

