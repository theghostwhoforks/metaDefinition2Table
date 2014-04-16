package org.ict4h.formdefinition.model.query;

public class SelectAllQuery extends SelectQuery{

    private final String entityId;

    public SelectAllQuery(String tableName, String entityId) {
        super(tableName);
        this.entityId = entityId;
    }

    @Override
    public String asSql() {
        return String.format("SELECT ID FROM %s WHERE ENTITY_ID = '%s';",tableName,entityId);
    }
}
