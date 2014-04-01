package builder;

import com.google.gson.Gson;
import model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class QueryBuilder {
    private static final String ENTITY_ID = "entityId";
    private static String defaultsForCreate =  String.format("ID SERIAL PRIMARY KEY,%s VARCHAR(255),created_at timestamp default current_timestamp",ENTITY_ID);
    private static final String DELIMITER = ",";
    private FormDefinition definition;

    public static QueryBuilder with() {
        return new QueryBuilder();
    }

    private QueryBuilder() {}

    public QueryBuilder formDefinition(String dataAsJson) {
        this.definition = new Gson().fromJson(dataAsJson, FormDefinition.class);
        return this;
    }

    public Query nothing() {
        return new Query("");
    }

    public Query createEntity() {
        final String formName = definition.getName();

        List<Field> fields = definition.getForm().getFields();

        List<String> columns = new ArrayList<>();
        List<String> values = new ArrayList<>();
        fields.stream().filter(f -> f.hasValue()).map(f -> String.format("%s.id",formName).equals(f.getName()) ?
                new EntityField(ENTITY_ID,f.getValue()) : f)
                .forEach(f -> {
                    columns.add(f.getName());
                    values.add(String.format("'%s'", f.getValue()));
                });

        String statement = String.format("INSERT INTO %s (%s) VALUES (%s);", formName,
                columns.stream().collect(Collectors.joining(DELIMITER)),
                values.stream().collect(Collectors.joining(DELIMITER)));
        return new Query(statement);
    }

    private String createQueryFor(Form form, String formName, String foreignKey) {
        Function<String, String> converter = s -> String.format("CREATE TABLE %s (%s,%s%s);", formName, defaultsForCreate,s,foreignKey);
        Predicate<Field> filterPredicate = ((Predicate<Field>) f -> f.isNotReservedKeyword()).and(f -> f.isNotSection());
        String statement = converter.apply(form.getFields().stream()
                .filter(filterPredicate)
                .map(f -> String.format("%s VARCHAR(255)", f.getName())).collect(Collectors.joining(DELIMITER)));
        return statement;
    }

    public boolean hasSubFormsInFormDefinition() {
        return definition.hasSubFormsInForm();
    }

    public List<Query> createNestedTables() {
        List<Query> queries = new ArrayList<>();
        queries.add(new Query(createQueryFor(definition.getForm(), definition.getName(), "")));
        List<Form> subForms = definition.getForm().getSubForms();
        queries.addAll(subForms.stream().map((subForm) -> {
            String formName = subForm.getSubFormName() + "_" + subForm.getName();
            String foreignKey = ",parent_form_id Integer references " + subForm.getName() + " (ID)";
            String queryFor = createQueryFor(subForm, formName, foreignKey);
            return new Query(queryFor);
        }).collect(Collectors.toList()));
        return queries;
    }
}