package hitbtc.InfoDB;

import DB.DBconnactionVPS;
import DB.GetVal;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Hitbtc {

    public static final String SELECT = "SELECT id, pair, bid1, ask1, bid2, ask2, bid3, ask3, bid4, ask4, bid5, ask5, date, description FROM exchange.hitbtc;";
    public Map<Integer,HitbtcPair> hitbtc;

    public Hitbtc(String select){
        this.hitbtc= new HashMap<>();
        ArrayList<HashMap> resDB = null;
        try {
            resDB= DBconnactionVPS.getResultSet(select);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        for(HashMap hm : resDB){
            int id = GetVal.getInt(hm,"id");
            int pair = GetVal.getInt(hm,"pair");
            double bid1 = GetVal.getDouble(hm,"bid1");
            double ask1 = GetVal.getDouble(hm,"ask1");
            double bid2 = GetVal.getDouble(hm,"bid2");
            double ask2 = GetVal.getDouble(hm,"ask2");
            double bid3 = GetVal.getDouble(hm,"bid3");
            double ask3 = GetVal.getDouble(hm,"ask3");
            double bid4 = GetVal.getDouble(hm,"bid4");
            double ask4 = GetVal.getDouble(hm,"ask4");
            double bid5 = GetVal.getDouble(hm,"bid5");
            double ask5 = GetVal.getDouble(hm,"ask5");
            Timestamp date = GetVal.getTimeS(hm,"date");
            String description = GetVal.getStr(hm,"description");
            HitbtcPair hbp =  new HitbtcPair(id, pair, bid1, ask1, bid2, ask2, bid3, ask3, bid4, ask4, bid5, ask5, date, description);
            this.hitbtc.put(id, hbp);
        }
    }

    public Hitbtc(){
        this(SELECT);
    }

    public void print(){
        for (Map.Entry<Integer, HitbtcPair> entry : this.hitbtc.entrySet()){
            System.out.printf("id:%s\tHitbtcPair:\t%s\n", entry.getKey(), entry.getValue().toString());
        }
    }

    public int getId(int pair){
        int res = 0;
        for (Map.Entry<Integer,HitbtcPair> entry : this.hitbtc.entrySet()){
            if (entry.getValue().pair==pair){
                res=entry.getKey();
            }
        }
        return res;
    }

    public int getIdHitbtcPair(int pair){
        int res = 0;
        for (Map.Entry<Integer,HitbtcPair> entry : this.hitbtc.entrySet()){
            if (entry.getValue().pair==pair){
                System.out.println("|getIdHitbtcPair| entry.getValue().pair="+entry.getValue().pair);
                System.out.println("|getIdHitbtcPair| entry.getValue().id="+entry.getValue().id);
                res=entry.getValue().id;
            }
        }
        return res;
    }

}

