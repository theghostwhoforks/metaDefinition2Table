package builder;


import com.google.gson.Gson;
import constant.Constants;
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
    private static final String MODIFIED_BY_USER = "modified_by";
    public static final String REFERENCED_FIELD_ID = "parent_id";
    private FormDefinition definition;
    private String modifiedByUser;

    public static EntityQueryBuilder with() {
        return new EntityQueryBuilder();
    }

    private EntityQueryBuilder() {
    }

    public EntityQueryBuilder formDefinition(String dataAsJson) {
        this.definition = new Gson().fromJson(dataAsJson, FormDefinition.class);
        return this;
    }

    public EntityQueryBuilder modifiedByUser(String modifiedByUser) {
        this.modifiedByUser = modifiedByUser;
        return this;
    }

    public Query nothing() {
        return () -> "";
    }

    public Query createEntity() {
        final String formName = definition.getName();
        Stream<Field> fields = definition.getForm().getFields().stream().filter(Field::hasValue)
                .map(f -> String.format("%s%s", formName, Constants.ID_SUFFIX).equals(f.getName()) ?
                        new EntityField(ENTITY_ID, f.getValue()) : f);
        return new InsertQuery(formName, Stream.concat(fields, Stream.of(new Field(MODIFIED_BY_USER, modifiedByUser))));
    }

    public List<Query> createSubEntities(Integer foreignKey) {
        final String formName = definition.getName();
        final Field foreignKeyField = new Field(REFERENCED_FIELD_ID, foreignKey.toString());
        return definition.getForm().getSubForms().stream()
                .filter(subForm -> subForm.getName() != null)
                .<InsertQuery>flatMap(subForm ->
                subForm.instancesAsStreamOfFields().map(instance ->
                    new InsertQuery(subFormTableName(formName,subForm.getName()),
                                    Stream.concat(instance, Stream.of(foreignKeyField, new Field(MODIFIED_BY_USER, modifiedByUser))))
                )).collect(Collectors.toList());
    }
}