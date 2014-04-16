package org.ict4h.formdefinition.model.query;

import org.ict4h.formdefinition.constant.Constants;
import org.ict4h.formdefinition.model.Field;

import java.util.List;
import java.util.function.Function;

public class CreateDependentQuery extends CreateIndependentQuery {

    private final String referencedTableName;
    Function<String,String> foreignKeyConstraint = references -> String.format("parent_id Integer references %s (ID) ON DELETE CASCADE",references);
    private static String defaultsForReferencedTableCreation = String.format("%s SERIAL PRIMARY KEY,%s timestamp default current_timestamp,%s VARCHAR(255)",
            Constants.ID,Constants.MODIFIED_AT,Constants.MODIFIED_BY);

    public CreateDependentQuery(List<Field> fields, String tableName, String referencedTableName) {
        super(fields,tableName);
        this.referencedTableName = referencedTableName;
    }

    @Override
    public String asSql() {
        Function<String, String> stringToSql = query -> String.format("CREATE TABLE %s (%s,%s,%s);",
                String.format("%s_%s", tableName, referencedTableName),
                defaultsForReferencedTableCreation,
                query,
                foreignKeyConstraint.apply(referencedTableName));
        return stringToSql.apply(fieldsWithDataTypes());
    }
}
