package org.ict4h.model.query;

public class DeleteEntityQuery implements Query {
    public static final String DELETE_TEMPLATE = "DELETE FROM %s WHERE ID = %s;";
    private final String tableName;
    private int id;

    public DeleteEntityQuery(String tableName, int id) {
        this.tableName = tableName;
        this.id = id;
    }

    @Override
    public String asSql() {
        return String.format(DELETE_TEMPLATE,tableName,id);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof DeleteEntityQuery)) return false;
        return asSql().equals(((DeleteEntityQuery) other).asSql());
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