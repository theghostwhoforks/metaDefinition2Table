package model.query;

import constant.Constants;
import model.Field;

import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class UpdateQuery implements Query {
    private String formName;
    private List<Field> fields;
    private Set<String> currentColumns;

    public UpdateQuery(String formName, List<Field> fields, Set<String> currentColumns) {
        this.formName = formName;
        this.currentColumns = currentColumns;
        this.fields = fields;
    }

    @Override
    public String asSql() {
        Function<String, String> converter = str -> String.format("ALTER TABLE %s %s;", formName, str);
        String statement = converter.apply(fields.stream()
                        .filter(filterKeywordsAndSections().and(field -> !currentColumns.contains(field.getName().toUpperCase()))
                                .and(fi-> !String.format("%s_id", formName).equals(fi.getName())))
                        .map(f -> String.format("%s VARCHAR(255)", String.format("ADD COLUMN %s", f.getName())))
                        .collect(Collectors.joining(Constants.DELIMITER)));
        return statement;
    }

    @Override
    public String toString() {
        return asSql();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UpdateQuery)) return false;

        UpdateQuery that = (UpdateQuery) o;

        return asSql().equals(that.asSql());
    }

    @Override
    public int hashCode() {
        return asSql().hashCode();
    }
}