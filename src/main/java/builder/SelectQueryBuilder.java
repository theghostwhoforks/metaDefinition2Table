package builder;

import com.google.gson.Gson;
import model.FormDefinition;
import model.query.FormTableQueryMultiMap;
import model.query.Query;
import model.query.SelectDependentQuery;
import model.query.SelectQuery;

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

    //TODO: Use formDefinition
    @Deprecated
    public FormTableQueryMultiMap createSelectQueriesFor(int id, String formName, List<String> subForms) {
        List<Query> nestedTableQueries = subForms.stream().map(tableName ->
                new SelectDependentQuery(tableName, id)).collect(Collectors.toList());
        return new FormTableQueryMultiMap(new SelectQuery(formName,id),nestedTableQueries);
    }
}