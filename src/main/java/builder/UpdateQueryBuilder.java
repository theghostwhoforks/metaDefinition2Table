package builder;

import com.google.gson.Gson;
import model.Field;
import model.FormDefinition;
import model.query.Query;
import model.query.SimpleQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class UpdateQueryBuilder {
    private static final String ENTITY_ID = "entityId";
    private static final String DELIMITER = ",";
    private FormDefinition definition;

    public static UpdateQueryBuilder with() {
        return new UpdateQueryBuilder();
    }

    private UpdateQueryBuilder() {
    }

    public UpdateQueryBuilder formDefinition(String dataAsJson) {
        this.definition = new Gson().fromJson(dataAsJson, FormDefinition.class);
        return this;
    }

    public SimpleQuery nothing() {
        return new SimpleQuery("");
    }

    public Query update(List<String> columns) {
        List<Field> fieldsToBeUpdated = new ArrayList<>();
        definition.getForm().getFields().stream().forEach(field -> {
            if (!columns.contains(field.getName())) fieldsToBeUpdated.add(field);
        });
        Function<String, String> converter = str -> String.format("ALTER TABLE %s %s;", definition.getName(), str);
        String statement = converter.apply(fieldsToBeUpdated.stream().map(f -> {
            return String.format("%s VARCHAR(255)", String.format("ADD COLUMN %s", f.getName()));
        }).reduce((x, y) -> x + "," + y).get());
        return new SimpleQuery(statement);
    }

    public boolean isRequired(int columnCount) {
        return columnCount < definition.getForm().getFields().size() + 2;
    }
}