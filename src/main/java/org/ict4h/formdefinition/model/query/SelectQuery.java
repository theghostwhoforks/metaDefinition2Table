package org.ict4h.formdefinition.model.query;

public class SelectQuery implements Query {
    protected final String tableName;
    protected int id;

    public SelectQuery(String tableName) {
        this.tableName = tableName;
    }

    public SelectQuery(String tableName,int id) {
        this.tableName = tableName;
        this.id = id;
    }

    @Override
    public String asSql() {
        return String.format("SELECT * FROM %s WHERE ID = %s;",tableName,id);
    }

    public String createDescribeQuery() {
        return String.format("SELECT * FROM %s LIMIT 1;",tableName);
    }

    public String getTableName(){
        return tableName;
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