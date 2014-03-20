package builder;

import model.FormDefinition;
import model.Query;

import java.util.function.Function;

public class QueryBuilder {
    private final FormDefinition formDefinition;


    public static QueryBuilder formDefinition(FormDefinition formDefinition){
        return new QueryBuilder(formDefinition);
    }

    private QueryBuilder(FormDefinition formDefinition) {
        this.formDefinition = formDefinition;
    }

    public Query build(){
        final String formName = formDefinition.getName();
        Function<String,String> converter = (str) -> String.format("CREATE TABLE %s (%s)",formName,str);
        String statement = converter.apply(formDefinition.getForm().getFields().stream().map(f -> String.format("%s text", f.getName())).reduce((x, y) -> x + "," + y).get());
        return new Query(statement);
    }
}
