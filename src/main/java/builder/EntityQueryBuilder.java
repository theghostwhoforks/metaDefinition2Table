package builder;

import com.google.gson.Gson;
import model.EntityField;
import model.Field;
import model.FormDefinition;
import model.query.Query;
import model.query.SimpleQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EntityQueryBuilder {
    private static final String ENTITY_ID = "entityId";
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
        fields.stream().filter(f -> f.hasValue()).map(f -> String.format("%s.id",formName).equals(f.getName()) ?
                new EntityField(ENTITY_ID,f.getValue()) : f)
                .forEach(f -> {
                    columns.add(f.getName());
                    values.add(String.format("'%s'", f.getValue()));
                });
    }

    public List<Query> createSubEntities(int foreignKey) {
        final String formName = definition.getName();

        return definition.getForm().getSubForms().stream().map(f -> {
            List<String> columns = new ArrayList<>();
            List<String> values = new ArrayList<>();
            List<Field> fields = f.getFields();
            getColumnsAndValues(formName, fields, columns, values);
            values.add(foreignKey + "");
            columns.add("parent_form_id");
            return new SimpleQuery(getInsertStatement(f.getName() + "_" + formName, columns, values));
        }).collect(Collectors.toList());
    }
}