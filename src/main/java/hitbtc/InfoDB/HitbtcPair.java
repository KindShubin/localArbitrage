package hitbtc.InfoDB;

import java.sql.Timestamp;
import java.util.Map;

public class HitbtcPair{

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
