package builder;

import com.google.gson.Gson;
import model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class EntityQueryBuilder {
    private static final String ENTITY_ID = "entityId";
    private static String defaultsForCreate =  String.format("ID SERIAL PRIMARY KEY,%s VARCHAR(255),created_at timestamp default current_timestamp",ENTITY_ID);
    private static final String DELIMITER = ",";
    private FormDefinition definition;

    public static EntityQueryBuilder with() {
        return new EntityQueryBuilder();
    }

    private EntityQueryBuilder() {}

    public EntityQueryBuilder formDefinition(String dataAsJson) {
        this.definition = new Gson().fromJson(dataAsJson, FormDefinition.class);
        return this;
    }

    public Query nothing() {
        return new Query("");
    }

    public Query create() {
        final String formName = definition.getName();

        List<Field> fields = definition.getForm().getFields();

        List<String> columns = new ArrayList<>();
        List<String> values = new ArrayList<>();
        getColumnsAndValues(formName, fields, columns, values);

        String statement = getInsertStatement(formName, columns, values);
        return new Query(statement);
    }

    private String getInsertStatement(String formName, List<String> columns, List<String> values) {
        return String.format("INSERT INTO %s (%s) VALUES (%s);", formName,
                    columns.stream().collect(Collectors.joining(DELIMITER)),
                    values.stream().collect(Collectors.joining(DELIMITER)));
    }

    private void getColumnsAndValues(String formName, List<Field> fields, List<String> columns, List<String> values) {
        fields.stream().filter(f -> f.hasValue()).map(f -> String.format("%s.id",formName).equals(f.getName()) ?
                new EntityField(ENTITY_ID,f.getValue()) : f)
                .forEach(f -> {
                    columns.add(f.getName());
                    values.add(String.format("'%s'", f.getValue()));
                });
    }

    public List<Query> create(int foreignKey) {
        final String formName = definition.getName();
        List<Query> queries = new ArrayList<>();

        definition.getForm().getSubForms().stream().forEach(f -> {
            List<String> columns = new ArrayList<>();
            List<String> values = new ArrayList<>();
            List<Field> fields = f.getFields();
            getColumnsAndValues(formName, fields, columns, values);
            values.add(foreignKey + "");
            columns.add("parent_form_id");
            queries.add(new Query(getInsertStatement(f.getName() + "_" + formName, columns, values)));
        });
        return queries;
    }
}