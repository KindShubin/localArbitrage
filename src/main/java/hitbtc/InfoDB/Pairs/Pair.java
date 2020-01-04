package hitbtc.InfoDB.Pairs;

import DB.DBconnactionVPS;
import DB.GetVal;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class Pair{
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

    public Pair(int id) throws SQLException {
        String select = "SELECT baseCoin, quoteCoin, feeCoin, exForm FROM exchange.pairs where id="+id;
        ArrayList<HashMap> rs = DBconnactionVPS.getResultSet(select);
        this.id=id;
        this.baseCoin= GetVal.getInt(rs.get(0),"baseCoin");
        this.quoteCoin= GetVal.getInt(rs.get(0),"quoteCoin");
        this.feeCoin= GetVal.getInt(rs.get(0),"feeCoin");
        this.exForm= GetVal.getStr(rs.get(0),"exForm");
    }

    public String toString(){
        String result = new StringBuilder().append("id:").append(id).append("\tbaseCoin:").append(baseCoin).append("\tquoteCoin").append(quoteCoin)
                .append("\tfeeCoin:").append(feeCoin).append("\texForm:").append(exForm).toString();
        return result;
    }



}
