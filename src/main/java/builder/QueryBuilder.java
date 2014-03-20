package builder;

import model.FormDefinition;

import java.util.Optional;
import java.util.function.Function;

public class QueryBuilder {
    private final FormDefinition formDefinition;


    public static QueryBuilder formDefinition(FormDefinition formDefinition){
        return new QueryBuilder(formDefinition);
    }

    private QueryBuilder(FormDefinition formDefinition) {
        this.formDefinition = formDefinition;
    }

    public String build(){
        // CREATE TABLE FORM_NAME {
        // A INT,
        // B TEXT
        // }
        final String formName = formDefinition.getName();
        Function<String,String> converter = (str) -> String.format("CREATE TABLE %s (%s)",formName,str);
        return converter.apply(formDefinition.getForm().getFields().stream().map((f) -> String.format("%s text", f.getName())).reduce((x, y) -> x + "," + y).get());
    }
}
