package model.query;

public class SelectQuery implements Query {
    private final String tableName;

    public SelectQuery(String tableName) {
        this.tableName = tableName;
    }

    @Override
    public String asSql() {
        return String.format("SELECT * FROM %s LIMIT 1;",tableName);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof SelectQuery)) return false;
        return asSql().equals(((SelectQuery) other).asSql());
    }

    @Override
    public int hashCode() {
        return asSql().hashCode();
    }

    @Override
    public String toString() {
        return asSql();
    }
}