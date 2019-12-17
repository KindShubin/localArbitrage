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
            this.hitbtc.put(id, new HitbtcPair(id, pair, bid1, ask1, bid2, ask2, bid3, ask3, bid4, ask4, bid5, ask5, date, description));
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

}

class HitbtcPair{

    public int id;
    public int pair;
    public double bid1;
    public double ask1;
    public double bid2;
    public double ask2;
    public double bid3;
    public double ask3;
    public double bid4;
    public double ask4;
    public double bid5;
    public double ask5;
    public Timestamp date;
    public String description;

    public HitbtcPair(int id, int pair, double bid1, double ask1, double bid2, double ask2, double bid3, double ask3, double bid4, double ask4, double bid5, double ask5, Timestamp date, String description){
        this.id=id;
        this.pair=pair;
        this.bid1=bid1;
        this.ask1=ask1;
        this.bid2=bid2;
        this.ask2=ask2;
        this.bid3=bid3;
        this.ask3=ask3;
        this.bid4=bid4;
        this.ask4=ask4;
        this.bid5=bid5;
        this.ask5=ask5;
        this.date=date;
        this.description=description;
    }

    public String toString(){
        String res = new StringBuilder().append("|id:").append(id).append("|pair:").append(pair).append("|bid1:").append(bid1).append("|ask1:").append(ask1)
                .append("|bid2:").append(bid2).append("|ask2:").append(ask2).append("|bid3:").append(bid3).append("|ask3:").append(ask3).append("|bid4:").append(bid4)
                .append("|ask4:").append(ask4).append("|bid5:").append(bid5).append("|ask5:").append(ask5).append("|date:").append(date).append("|description:").append(description).toString();
        return res;
    }

}

