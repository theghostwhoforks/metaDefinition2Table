package builder;

import com.google.gson.Gson;
import model.FormDefinition;
import model.query.SimpleQuery;

public class SelectQueryBuilder {
    private FormDefinition definition;

    public static SelectQueryBuilder with() {
        return new SelectQueryBuilder();
    }

    private SelectQueryBuilder() {}

    public SelectQueryBuilder formDefinition(String dataAsJson) {
        this.definition = new Gson().fromJson(dataAsJson, FormDefinition.class);
        return this;
    }

    public SimpleQuery nothing() {
        return new SimpleQuery("");
    }

    public SimpleQuery createDescribeQuery() {
        String name = definition.getName();

        return new SimpleQuery(String.format("SELECT * FROM %s LIMIT 1;",name));
    };
}