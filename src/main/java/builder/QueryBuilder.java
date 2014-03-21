package builder;

import com.google.gson.Gson;
import model.Field;
import model.FormDefinition;
import model.Query;

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

    public Query build() {
        FormDefinition definition = new Gson().fromJson(formDefinition, FormDefinition.class);
        final String formName = definition.getName();
        Function<String, String> converter = str -> String.format("CREATE TABLE %s (%s)", formName, str);
        String statement = converter.apply(definition.getForm().getFields().stream().map(f -> String.format("%s text", f.getName())).
                                            reduce((x, y) -> x + "," + y).get());
        return new Query(statement);
    }

    public Query nothing() {
        return new Query("");
    }

    public Query insert(String data) {
        FormDefinition definition = new Gson().fromJson(data, FormDefinition.class);
        final String formName = definition.getName();
        Function<String, String> converter = (str) -> String.format("INSERT INTO %s ({FIELDS}) VALUES ('%s')", formName,str);
        List<Field> fields = definition.getForm().getFields();
        String statement = converter.apply(fields.stream().filter(f -> f.getValue() != null).map(f -> String.format("%s", f.getValue()))
                .reduce((x, y) -> x + "'" + ",'" + y).get());
        String fieldNames = fields.stream().filter(f -> f.getValue() != null).map(f -> String.format("%s", f.getName()))
                .reduce((x, y) -> x + "," + y).get();
        return new Query(statement.replace("{FIELDS}",fieldNames));
    }
}
