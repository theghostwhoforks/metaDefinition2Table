package builder;

import com.google.gson.Gson;
import junit.framework.Assert;
import model.FormDefinition;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.IOException;

import static junit.framework.Assert.assertNotNull;

public class QueryBuilderTest {

    @Test
    public void shouldBuildAQuery() throws IOException {
        String data = FileUtils.readFileToString(FileUtils.toFile(this.getClass().getResource("/metamodel/sample.json")));
        FormDefinition definition = new Gson().fromJson(data, FormDefinition.class);
        String build = QueryBuilder.formDefinition(definition).build();
        assertNotNull(build);
    }
}
