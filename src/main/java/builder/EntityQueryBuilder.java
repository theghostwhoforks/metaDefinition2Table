package builder;


import com.google.gson.Gson;
import model.EntityField;
import model.Field;
import model.FormDefinition;
import model.query.InsertQuery;
import model.query.Query;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EntityQueryBuilder implements Builder {
    private static final String ENTITY_ID = "entity_id";
    public static final String REFERENCED_FIELD_ID = "parent_id";
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

    public Query nothing() {
        return () -> "";
    }

    public Query createEntity() {
        final String formName = definition.getName();
        Stream<Field> fields = definition.getForm().getFields().stream().filter(Field::hasValue)
                .map(f -> String.format("%s_id", formName).equals(f.getName()) ?
                        new EntityField(ENTITY_ID, f.getValue()) : f);
        return new InsertQuery(formName, fields);
    }

    public List<Query> createSubEntities(Integer foreignKey) {
        final String formName = definition.getName();
        final Field foreignKeyField = new Field(REFERENCED_FIELD_ID, foreignKey.toString());
        return definition.getForm().getSubForms().stream()
                .<InsertQuery>flatMap(subForm ->
                subForm.getFieldValues().map(instance ->
                    new InsertQuery(subFormTableName(formName,subForm.getName()),
                                    Stream.concat(instance, Stream.of(foreignKeyField)))
                )).collect(Collectors.toList());
    }
}