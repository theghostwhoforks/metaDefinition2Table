package builder;

import com.google.gson.Gson;
import model.Field;
import model.Form;
import model.FormDefinition;
import model.ParentForm;
import model.query.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TableQueryBuilder implements Builder {

    private FormDefinition definition;

    public static TableQueryBuilder with() {
        return new TableQueryBuilder();
    }

    public TableQueryBuilder formDefinition(String dataAsJson) {
        this.definition = new Gson().fromJson(dataAsJson, FormDefinition.class);
        return this;
    }

    public Query nothing() {
        return () -> "";
    }

    public FormTableCreateQueryMultiMap create() {
        List<Query> linkedTableQueries = new ArrayList<>();
        ParentForm parentForm = definition.getForm();
        String formName = definition.getName();
        List<Field> fields = definition.getForm().getFields();

        CreateIndependentQuery independentQuery = new CreateIndependentQuery(fields, formName);
        linkedTableQueries.addAll(queriesForSubForms(parentForm));
        return new FormTableCreateQueryMultiMap(independentQuery,linkedTableQueries);
    }

    private List<Query> queriesForSubForms(final Form form) {
        return form.getSubForms().stream()
                .map(subForm -> new CreateDependentQuery(subForm.getFields(),subForm.getName(),form.getName()))
                .collect(Collectors.toList());
    }
}