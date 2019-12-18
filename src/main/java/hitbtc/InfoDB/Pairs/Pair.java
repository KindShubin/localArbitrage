package hitbtc.InfoDB.Pairs;

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

    public String toString(){
        String result = new StringBuilder().append("id:").append(id).append("\tbaseCoin:").append(baseCoin).append("\tquoteCoin").append(quoteCoin)
                .append("\tfeeCoin:").append(feeCoin).append("\texForm:").append(exForm).toString();
        return result;
    }

}
