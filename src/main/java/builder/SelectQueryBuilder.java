package builder;

import com.google.gson.Gson;
import model.FormDefinition;
import model.query.Query;
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

    public Query nothing() {
        return () -> "";
    }

    public List<Query> createDescribeQuery() {
        String name = definition.getName();
        List<Query> queries = new ArrayList();
        queries.add(new SelectQuery(name));

        queries.addAll(definition.getForm().getSubForms().stream().map(subForm ->
                new SelectQuery(subFormTableName(name, subForm.getName()))).collect(Collectors.toList()));

        return queries;
    };
}