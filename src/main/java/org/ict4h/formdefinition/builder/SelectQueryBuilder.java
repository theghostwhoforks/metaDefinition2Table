package org.ict4h.formdefinition.builder;

import com.google.gson.Gson;
import org.ict4h.formdefinition.model.Form;
import org.ict4h.formdefinition.model.FormDefinition;
import org.ict4h.formdefinition.model.SubForm;
import org.ict4h.formdefinition.model.query.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SelectQueryBuilder implements Builder {
    private FormDefinition definition;

    public static SelectQueryBuilder with() {
        return new SelectQueryBuilder();
    }

    private SelectQueryBuilder() {}

    public SelectQueryBuilder formDefinition(String dataAsJson) {
        this.definition = new Gson().fromJson(dataAsJson, FormDefinition.class);
        return this;
    }

    public SelectQuery nothing() {
       return new SelectQuery("",0);
    }

    public List<SelectQuery> createDescribeQuery() {
        String name = definition.getName();
        List<SelectQuery> queries = new ArrayList();
        queries.add(new SelectQuery(name));
        queries.addAll(definition.getForm().getSubForms().stream().filter(subForm -> subForm.getName() != null).map(subForm ->
                new SelectQuery(subFormTableName(name, subForm.getName()))).collect(Collectors.toList()));
        return queries;
    };

    public FormTableQueryMultiMap createSelectQueriesFor(int id) {
        Form form = definition.getForm();
        String formName = form.getName();
        List<SubForm> subForms = form.getSubForms();
        List<Query> nestedTableQueries = subForms.stream().map(subForm ->
                new SelectDependentQuery(subFormTableName(formName,subForm.getName()), id)).collect(Collectors.toList());
        return new FormTableQueryMultiMap(new SelectQuery(formName,id),nestedTableQueries);
    }

    public SelectQuery listMetadata(String beneficiaryId, String tableName) {
        return new SelectAllQuery(tableName,beneficiaryId);
    }
}