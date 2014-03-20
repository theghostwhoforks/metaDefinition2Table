package builder;

import com.google.gson.Gson;
import model.FormDefinition;
import model.Query;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.IOException;

import static junit.framework.Assert.assertNotNull;

public class QueryBuilderTest {

    @Test
    public void shouldBuildAQuery() throws IOException {
        String data = FileUtils.readFileToString(FileUtils.toFile(this.getClass().getResource("/metamodel/sample.json")));
        FormDefinition definition = new Gson().fromJson(data, FormDefinition.class);
        Query query = QueryBuilder.formDefinition(definition).build();
        assertNotNull(query);
    }
}
