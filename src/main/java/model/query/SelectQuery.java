package model.query;

public class SelectQuery implements Query {
    private final String tableName;
    private String columnName;
    private int id;

    public SelectQuery(String tableName) {
        this.tableName = tableName;
        this.id = -1;
    }

    public SelectQuery(String tableName,String columnName ,int id) {
        this.tableName = tableName;
        this.columnName = columnName;
        this.id = id;
    }

    @Override
    public String asSql() {
        if(id == -1)
            return String.format("SELECT * FROM %s LIMIT 1;",tableName);
        return String.format("SELECT * FROM %s WHERE %s = %s;",tableName,columnName,id);
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