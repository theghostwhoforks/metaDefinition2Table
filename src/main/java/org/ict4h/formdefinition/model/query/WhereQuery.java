package org.ict4h.formdefinition.model.query;

public class WhereQuery implements Query {
    private final String tableName;
    private final String fieldName;
    private final String fieldValue;

    //TODO - use Pair when we need to deal with multiple where clause.
    public WhereQuery(String tableName, String fieldName, String fieldValue) {
        this.tableName = tableName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

    @Override
    public String asSql() {
        return String.format("SELECT * FROM %s WHERE %s = '%s';",tableName,fieldName,fieldValue);
    }
}
