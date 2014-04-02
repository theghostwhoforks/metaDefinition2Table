package builder;

import com.google.gson.Gson;
import model.*;
import model.query.TableCreateQuery;
import model.query.SimpleQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class TableQueryBuilder {
    private static final String ENTITY_ID = "entityId";
    private static final String DELIMITER = ",";
    private FormDefinition definition;
    private static final Predicate<Field> filterPredicate =
            ((Predicate<Field>) f -> f.isNotReservedKeyword()).and(f -> f.isNotSection());
    Function<String,String> foreignKeyConstraint = tableName -> String.format("parent_form_id Integer references %s (ID)",tableName);
    private static String defaultsForCreate =  String.format("ID SERIAL PRIMARY KEY,%s VARCHAR(255),created_at timestamp default current_timestamp",ENTITY_ID);

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
        Form form = definition.getForm();
        String formName = definition.getName();
        SimpleQuery createTableSimpleQuery = createQueryFor(form, formName);
        linkedTableQueries.addAll(queriesForSubForms(form));
        return new TableCreateQuery(createTableSimpleQuery,linkedTableQueries);
    }

    private String collectQuery(Form form) {
        return form.getFields().stream()
                .filter(filterPredicate)
                .map(f -> String.format("%s VARCHAR(255)", f.getName())).collect(Collectors.joining(DELIMITER));
    }

    private SimpleQuery createQueryFor(Form form, String tableName) {
        Function<String, String> converter = query -> String.format("CREATE TABLE %s (%s,%s);", tableName, defaultsForCreate,query);
        return new SimpleQuery(converter.apply(collectQuery(form)));
    }

    private String createQueryFor(Form form, String tableName, String foreignKey) {
        Function<String, String> converter = query -> String.format("CREATE TABLE %s (%s,%s,%s);", tableName, defaultsForCreate,query,foreignKey);
        return converter.apply(collectQuery(form));
    }

    private List<SimpleQuery> queriesForSubForms(final Form form) {
        return form.getSubForms().stream().map((subForm) -> {
            String tableName = subForm.getName() + "_" + form.getTableName();
            String foreignKey = foreignKeyConstraint.apply(form.getTableName());
            return new SimpleQuery(createQueryFor(subForm, tableName, foreignKey));
        }).collect(Collectors.toList());
    }
}