package builder;

import com.google.gson.Gson;
import model.FormDefinition;
import model.Query;

import java.util.function.Function;

public class QueryBuilder {
    private final String dataAsJson;


    public static QueryBuilder formDefinition(String dataAsJson){
        return new QueryBuilder(dataAsJson);
    }

    private QueryBuilder(String dataAsJson) {
        this.dataAsJson = dataAsJson;
    }

    public Query build(){
        FormDefinition definition = new Gson().fromJson(dataAsJson, FormDefinition.class);
        final String formName = definition.getName();
        Function<String,String> converter = str -> String.format("CREATE TABLE %s (%s)",formName,str);
        String statement = converter.apply(definition.getForm().getFields().stream().map(f -> String.format("%s text", f.getName())).
                                           reduce((x, y) -> x + "," + y).get());
        return new Query(statement);
    }
}
