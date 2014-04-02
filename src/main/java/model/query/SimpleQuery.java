package model.query;

public class SimpleQuery implements Query {

    private final String statement;

    public SimpleQuery(String statement) {
        this.statement = statement;
    }

    public String asSql() {
        return statement;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof SimpleQuery)) return false;
        return statement.equals(((SimpleQuery) other).statement);
    }

    @Override
    public int hashCode() {
        return statement.hashCode();
    }

    @Override
    public String toString() {return statement;}
}
