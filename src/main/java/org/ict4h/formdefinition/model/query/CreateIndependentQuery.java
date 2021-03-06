package org.ict4h.formdefinition.model.query;

import org.ict4h.formdefinition.constant.Constants;
import org.ict4h.formdefinition.model.Field;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class CreateIndependentQuery implements Query{

    private static final Predicate<Field> filterPredicate =
            ((Predicate<Field>) f -> f.isNotReservedKeyword()).and(f -> f.isNotSection());

    private static String defaultsForIndependentTableCreation =  String.format("%s SERIAL PRIMARY KEY,%s VARCHAR(255),%s timestamp default current_timestamp,%s VARCHAR(255)",
                                                                 Constants.ID,
                                                                 Constants.ENTITY_ID,
                                                                 Constants.MODIFIED_AT,
                                                                 Constants.MODIFIED_BY
    );
    protected List<Field> fields;
    protected String tableName;

    public CreateIndependentQuery(List<Field> fields, String tableName) {
        this.fields = fields;
        this.tableName = tableName;
    }

    @Override
    public String asSql() {
        Function<String, String> stringToSql = query -> String.format("CREATE TABLE %s (%s,%s);", tableName, defaultsForIndependentTableCreation,query);
        return stringToSql.apply(fieldsWithDataTypes());
    }

    protected String fieldsWithDataTypes() {
        return fields.stream()
                .filter(filterPredicate.and(field -> rejectIfFieldNameIsEntityId(field, tableName)))
                .map(f -> String.format("%s VARCHAR(255)", f.getName()))
                .collect(Collectors.joining(Constants.DELIMITER));
    }

    private boolean rejectIfFieldNameIsEntityId(Field field, String formName) {
        return !field.getName().equals(formName + Constants.ID_SUFFIX);
    }

    @Override
    public String toString() {
        return asSql();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CreateIndependentQuery)) return false;
        return asSql().equals(((Query)o).asSql());
    }

    @Override
    public int hashCode() {
        return asSql().hashCode();
    }
}
