package org.ict4h.formdefinition.builder;


import com.google.gson.Gson;
import org.ict4h.formdefinition.model.FormDefinition;
import org.ict4h.formdefinition.model.query.DeleteEntityQuery;
import org.ict4h.formdefinition.model.query.Query;

public class DeleteQueryBuilder implements Builder {
    private FormDefinition definition;

    public static DeleteQueryBuilder with() {
        return new DeleteQueryBuilder();
    }

    private DeleteQueryBuilder() {
    }

    public DeleteQueryBuilder formDefinition(String dataAsJson) {
        this.definition = new Gson().fromJson(dataAsJson,  FormDefinition.class);
        return this;
    }

    public Query nothing() {
        return () -> "";
    }

    public Query deleteQueryFor(int id) {
        return new DeleteEntityQuery(definition.getName(),id);
    }
}