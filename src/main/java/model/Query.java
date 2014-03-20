package model;

public class Query {

    private final String statement;

    public Query(String statement) {
        this.statement = statement;
    }

    public String asSql(){
        return statement;
    }
}
