package builder;

import com.google.gson.Gson;
import model.*;

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

    public Query nothing() {
        return new Query("");
    }

    public List<Query> create() {
        List<Query> queries = new ArrayList<>();
        Form form = definition.getForm();
        String formName = definition.getName();
        queries.add(new Query(createQueryFor(form, formName)));
        queries.addAll(queriesForSubForms(form));
        return queries;
    }

    private String collectQuery(Form form) {
        return form.getFields().stream()
                .filter(filterPredicate)
                .map(f -> String.format("%s VARCHAR(255)", f.getName())).collect(Collectors.joining(DELIMITER));
    }

    private String createQueryFor(Form form, String tableName) {
        Function<String, String> converter = query -> String.format("CREATE TABLE %s (%s,%s);", tableName, defaultsForCreate,query);
        return converter.apply(collectQuery(form));
    }

    private String createQueryFor(Form form, String tableName, String foreignKey) {
        Function<String, String> converter = query -> String.format("CREATE TABLE %s (%s,%s,%s);", tableName, defaultsForCreate,query,foreignKey);
        return converter.apply(collectQuery(form));
    }

    private List<Query> queriesForSubForms(final Form form) {
        return form.getSubForms().stream().map((subForm) -> {
            String tableName = subForm.getName() + "_" + form.getTableName();
            String foreignKey = foreignKeyConstraint.apply(form.getTableName());
            return new Query(createQueryFor(subForm, tableName, foreignKey));
        }).collect(Collectors.toList());
    }
}