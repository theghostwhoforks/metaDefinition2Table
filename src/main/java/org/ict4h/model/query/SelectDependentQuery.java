package org.ict4h.model.query;

public class SelectDependentQuery extends SelectQuery {
    public SelectDependentQuery(String tableName, int id) {
        super(tableName,id);
    }

    @Override
    public String asSql() {
        return String.format("SELECT * FROM %s WHERE parent_id = %s;",tableName,id);
    }

}