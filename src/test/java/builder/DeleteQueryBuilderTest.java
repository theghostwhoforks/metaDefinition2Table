package builder;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DeleteQueryBuilderTest {
    @Test
    public void shouldBuildADeleteQueryFromFormDefinition() throws Exception {
        String data = FileUtils.readFileToString(FileUtils.toFile(this.getClass().getResource("/metamodel/5_fields.json")));
        int id = 1;

        String statement = DeleteQueryBuilder.with().formDefinition(data).DeleteEntity(id).asSql();

        assertEquals("DELETE FROM delivery_details_and_pnc1 WHERE ID = 1;",statement);

    }
}
