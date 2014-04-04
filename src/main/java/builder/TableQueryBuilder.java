package builder;

import com.google.gson.Gson;
import model.Field;
import model.Form;
import model.FormDefinition;
import model.ParentForm;
import model.query.SimpleQuery;
import model.query.TableCreateQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class TableQueryBuilder {
    private static final String ENTITY_ID = "entity_id";
    private static final String DELIMITER = ",";
    public static final String ID_SUFFIX = "_id";

    private FormDefinition definition;

    private static final Predicate<Field> filterPredicate =
            ((Predicate<Field>) f -> f.isNotReservedKeyword()).and(f -> f.isNotSection());
    Function<String,String> foreignKeyConstraint = tableName -> String.format("parent_id Integer references %s (ID) ON DELETE CASCADE",tableName);
    private static String defaultsForFormCreate =  String.format("ID SERIAL PRIMARY KEY,%s VARCHAR(255),created_at timestamp default current_timestamp",ENTITY_ID);
    private static String defaultsForSubFormCreate = "ID SERIAL PRIMARY KEY,created_at timestamp default current_timestamp";

    public static TableQueryBuilder with() {
        return new TableQueryBuilder();
    }

    public TableQueryBuilder formDefinition(String dataAsJson) {
        this.definition = new Gson().fromJson(dataAsJson, FormDefinition.class);
        return this;
    }

    public SimpleQuery nothing() {
        return new SimpleQuery("");
    }

    public TableCreateQuery create() {
        List<SimpleQuery> linkedTableQueries = new ArrayList<>();
        ParentForm parentForm = definition.getForm();
        String formName = definition.getName();
        SimpleQuery createTableSimpleQuery = createQueryFor(parentForm, formName);
        linkedTableQueries.addAll(queriesForSubForms(parentForm));
        return new TableCreateQuery(createTableSimpleQuery,linkedTableQueries);
    }

    private SimpleQuery createQueryFor(ParentForm form, String tableName) {
        Function<String, String> converter = query -> String.format("CREATE TABLE %s (%s,%s);", tableName, defaultsForFormCreate,query);
        return new SimpleQuery(converter.apply(collectQuery(form)));
    }

    private String createQueryForSubForm(Form form, String tableName, String foreignKey) {
        Function<String, String> converter = query -> String.format("CREATE TABLE %s (%s,%s,%s);", tableName, defaultsForSubFormCreate,query,foreignKey);
        return converter.apply(collectQuery(form));
    }

    private String collectQuery(Form form) {
        return form.getFields().stream()
                .filter(filterPredicate.and(field -> rejectIfFieldNameIsEntityId(field, form.getName())))
                .map(f -> String.format("%s VARCHAR(255)", f.getName())).collect(Collectors.joining(DELIMITER));
    }

    private boolean rejectIfFieldNameIsEntityId(Field field, String formName) {
        return !field.getName().equals(formName + ID_SUFFIX);
    }

    private List<SimpleQuery> queriesForSubForms(final ParentForm parentForm) {
        return parentForm.getSubForms().stream().map((subForm) -> {
            String tableName = subForm.getName() + "_" + parentForm.getName();
            String foreignKey = foreignKeyConstraint.apply(parentForm.getName());
            return new SimpleQuery(createQueryForSubForm(subForm, tableName, foreignKey));
        }).collect(Collectors.toList());
    }
}