package model.query;

import model.Field;

import java.util.List;
import java.util.function.Function;

public class CreateDependentQuery extends CreateIndependentQuery {

    private final String referencedTableName;
    Function<String,String> foreignKeyConstraint = references -> String.format("parent_id Integer references %s (ID) ON DELETE CASCADE",references);
    private static String defaultsForReferencedTableCreation = "ID SERIAL PRIMARY KEY,modified_at timestamp default current_timestamp,modified_by VARCHAR(255)";

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
