package builder;

import com.google.gson.Gson;
import model.*;
import model.query.Query;
import model.query.SimpleQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EntityQueryBuilder {
    private static final String ENTITY_ID = "entityId";
    private static final String DELIMITER = ",";
    public static final String PARENT_ID = "parent_id";
    private FormDefinition definition;

    public static EntityQueryBuilder with() {
        return new EntityQueryBuilder();
    }

    private EntityQueryBuilder() {
    }

    public EntityQueryBuilder formDefinition(String dataAsJson) {
        this.definition = new Gson().fromJson(dataAsJson, FormDefinition.class);
        return this;
    }

    public SimpleQuery nothing() {
        return new SimpleQuery("");
    }

    public Query createEntity() {
        final String formName = definition.getName();

        List<Field> fields = definition.getForm().getFields();

        List<String> columns = new ArrayList<>();
        List<String> values = new ArrayList<>();
        getColumnsAndValues(formName, fields, columns, values);

        String statement = getInsertStatement(formName, columns, values);
        return new SimpleQuery(statement);
    }

    private String getInsertStatement(String formName, List<String> columns, List<String> values) {
        return String.format("INSERT INTO %s (%s) VALUES (%s);", formName,
                columns.stream().collect(Collectors.joining(DELIMITER)),
                values.stream().collect(Collectors.joining(DELIMITER)));
    }

    private void getColumnsAndValues(String formName, List<Field> fields, List<String> columns, List<String> values) {
        fields.stream().filter(f -> f.hasValue()).map(f -> String.format("%s_id", formName).equals(f.getName()) ?
                new EntityField(ENTITY_ID, f.getValue()) : f)
                .forEach(f -> {
                    columns.add(f.getName());
                    values.add(String.format("'%s'", f.getValue()));
                });
    }

    public List<Query> createSubEntities(int foreignKey) {

        final String formName = definition.getName();
        List<SubForm> subForms = definition.getForm().getSubForms();
        List<Query> queries = new ArrayList<>();
        subForms.stream().forEach(f -> {
            f.getInstance().stream().forEach(map -> {
                List<String> values = new ArrayList();
                List<String> columns = new ArrayList();
                map.keySet().stream().forEach(key -> {
                    if (!key.equals("id")) {
                        values.add("'"+map.get(key)+"'");
                        columns.add(key);
                    }
                });
                values.add(foreignKey + "");
                columns.add(PARENT_ID);
                queries.add(new SimpleQuery(getInsertStatement(f.getName() + "_" + formName, columns, values)));
            });
        });
        return queries;
    }
}