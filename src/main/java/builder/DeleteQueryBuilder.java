package builder;


import com.google.gson.Gson;
import model.FormDefinition;
import model.query.DeleteEntityQuery;
import model.query.Query;

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

    public Query DeleteEntity(int id) {
        return new DeleteEntityQuery(definition.getName(),id);
    }
}