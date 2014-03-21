package model;

public class Query {

    private final String statement;

    public Query(String statement) {
        this.statement = statement;
    }

    public String asSql(){
        return statement;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof Query)) return false;
        return statement.equals(((Query) other).statement);
    }

    @Override
    public int hashCode() {
        return statement.hashCode();
    }
}
