package org.ict4h.builder;

import com.google.gson.Gson;
import org.ict4h.model.Form;
import org.ict4h.model.FormDefinition;
import org.ict4h.model.query.Query;
import org.ict4h.model.query.UpdateQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class UpdateQueryBuilder implements Builder{
    private FormDefinition definition;

    public static UpdateQueryBuilder with() {
        return new UpdateQueryBuilder();
    }

    private UpdateQueryBuilder() {
    }

    public UpdateQueryBuilder formDefinition(String dataAsJson) {
        this.definition = new Gson().fromJson(dataAsJson, FormDefinition.class);
        return this;
    }

    public Query nothing() { return () -> "" ;}

    public List<Query> update(Map<String, Set<String>> columns) {
        Form form = definition.getForm();
        String formName = form.getName();
        List<Query> queries = new ArrayList();
        queries.add(new UpdateQuery(formName,form.getFields(),columns.get(formName.toUpperCase())));
        columns.remove(formName.toUpperCase());

        queries.addAll(form.getSubForms().stream().filter(subForm -> subForm.getName() != null).map(subForm -> {
            String tableName = subFormTableName(formName, subForm.getName());
            return new UpdateQuery(tableName, subForm.getFields(), columns.get(tableName.toUpperCase()));
        }).collect(Collectors.toList()));
        return queries;
    }
}