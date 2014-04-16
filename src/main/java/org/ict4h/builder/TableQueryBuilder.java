package org.ict4h.builder;

import com.google.gson.Gson;
import org.ict4h.model.Field;
import org.ict4h.model.Form;
import org.ict4h.model.FormDefinition;
import org.ict4h.model.query.CreateDependentQuery;
import org.ict4h.model.query.CreateIndependentQuery;
import org.ict4h.model.query.FormTableQueryMultiMap;
import org.ict4h.model.query.Query;

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

    public FormTableQueryMultiMap create() {
        List<Query> linkedTableQueries = new ArrayList<>();
        Form form = definition.getForm();
        String formName = definition.getName();
        List<Field> fields = definition.getForm().getFields();

        CreateIndependentQuery independentQuery = new CreateIndependentQuery(fields, formName);
        linkedTableQueries.addAll(queriesForSubForms(form));
        return new FormTableQueryMultiMap(independentQuery,linkedTableQueries);
    }

    private List<Query> queriesForSubForms(final Form form) {
        return form.getSubForms().stream()
                .filter(subForm -> subForm.getName() != null)
                .map(subForm -> new CreateDependentQuery(subForm.getFields(),subForm.getName(),form.getName()))
                .collect(Collectors.toList());
    }
}