package builder;

import com.google.gson.Gson;
import model.Field;
import model.FormDefinition;
import model.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class QueryBuilder {
    private String formDefinition;

    public static QueryBuilder with() {
        return new QueryBuilder();
    }

    private QueryBuilder() {
    }

    public QueryBuilder formDefinition(String dataAsJson) {
        this.formDefinition = dataAsJson;
        return this;
    }

    public Query createTable() {
        FormDefinition definition = new Gson().fromJson(formDefinition, FormDefinition.class);
        final String formName = definition.getName();
        Function<String, String> converter = str -> String.format("CREATE TABLE %s (ID SERIAL PRIMARY KEY,%s);", formName, str);
        String statement = converter.apply(definition.getForm().getFields().stream().map(f -> String.format("%s VARCHAR(255)", f.getName())).
                reduce((x, y) -> x + "," + y).get());
        return new Query(statement);
    }

    public Query nothing() {
        return new Query(" ");
    }

    public Query createEntity() {
        FormDefinition definition = new Gson().fromJson(formDefinition, FormDefinition.class);
        final String formName = definition.getName();

        List<Field> fields = definition.getForm().getFields();

        List<String> columnNames = new ArrayList<>();
        List<String> values = new ArrayList<>();
        fields.stream().filter(f -> f.hasValue()).forEach(f -> {
                    columnNames.add(f.getName());
                    values.add(String.format("'%s'",f.getValue()));
                });

        String stringColumns = columnNames.stream().reduce((x, y) -> String.format("%s,%s", x, y)).get();
        String stringValues = values.stream().reduce((x, y) -> String.format("%s,%s", x, y)).get();

        String statement = String.format("INSERT INTO %s (%s) VALUES (%s);", formName, stringColumns, stringValues);
        return new Query(statement);
    }
}
