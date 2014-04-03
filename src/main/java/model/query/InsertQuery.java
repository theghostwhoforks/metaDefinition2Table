package model.query;

import constant.Constants;
import model.Field;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class InsertQuery implements Query {

    public static final String INSERT_TEMPLATE = "INSERT INTO %s (%s) VALUES (%s);";
    private final String tableName;
    private final List<Field> fields;

    public InsertQuery(String tableName, Stream<Field> fields) {
        this.tableName = tableName;
        this.fields = fields.collect(Collectors.toList());
    }

    @Override
    //TODO - Ugly ugly code
    public String asSql() {
        Stream<Field> fieldStream = fields.stream().filter(filterKeywordsAndSections());
        Stream<Field> duplicateStream = fields.stream().filter(filterKeywordsAndSections());
        String columnNames = fieldStream.map(Field::getName).collect(Collectors.joining(Constants.DELIMITER));
        String values = duplicateStream.map(x -> String.format("'%s'",x.getValue())).collect(Collectors.joining(Constants.DELIMITER));
        return String.format(INSERT_TEMPLATE,tableName,columnNames,values);
    }
}
