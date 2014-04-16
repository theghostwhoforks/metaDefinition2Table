package org.ict4h.formdefinition.model.query;

import org.ict4h.formdefinition.constant.Constants;

public class ListAllMetadataQuery extends SelectQuery{

    private final String entityId;

    public ListAllMetadataQuery(String tableName, String entityId) {
        super(tableName);
        this.entityId = entityId;
    }

    @Override
    public String asSql() {
        return String.format("SELECT %s,%s,%s FROM %s WHERE ENTITY_ID = '%s';", Constants.ID,Constants.MODIFIED_BY,Constants.MODIFIED_AT,
                                                                                tableName,entityId);
    }
}
