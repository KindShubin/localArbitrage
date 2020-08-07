package hitbtc.InfoDB.Coins;

public class Coin {
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
                .append("\tname:").append(name).append("\tdescription:").append(description).toString();
        return result;
    }

}
