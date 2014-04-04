package builder;

import model.query.Query;
import model.query.SimpleQuery;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;

import static junit.framework.Assert.assertEquals;

public class UpdateQueryBuilderTest {
    @Test
    public void shouldReturnTrueWhenUpdateTableIsRequired() throws IOException {
        String data = FileUtils.readFileToString(FileUtils.toFile(this.getClass().getResource("/metamodel/5_fields.json")));
        boolean isRequired = UpdateQueryBuilder.with().formDefinition(data).isRequired(0);
        assertEquals(true,isRequired);
    }

    @Test
    public void shouldReturnFalseWhenUpdateTableIsNotRequired() throws IOException {
        String data = FileUtils.readFileToString(FileUtils.toFile(this.getClass().getResource("/metamodel/5_fields.json")));
        boolean isRequired = UpdateQueryBuilder.with().formDefinition(data).isRequired(10);
        assertEquals(false,isRequired);
    }

    @Test
    public void shouldBuildAnUpdateQuery() throws IOException {
        String data = FileUtils.readFileToString(FileUtils.toFile(this.getClass().getResource("/metamodel/5_fields.json")));

        ArrayList<String> columns = new ArrayList<>();
        columns.add("pregnancyId");
        columns.add("today");
        columns.add("uuid");
        columns.add("formhub");

        Query query = UpdateQueryBuilder.with().formDefinition(data).update(columns);
        Query expected = new SimpleQuery("ALTER TABLE delivery_details_and_pnc1 ADD COLUMN sectionA VARCHAR(255),ADD COLUMN womanId VARCHAR(255);");

        assertEquals(expected,query);
    }

}
