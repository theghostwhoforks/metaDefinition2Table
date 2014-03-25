package builder;

import model.Query;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.IOException;

import static junit.framework.Assert.assertEquals;

public class QueryBuilderTest {
    @Test
    public void shouldBuildAQuery() throws IOException {
        String data = FileUtils.readFileToString(FileUtils.toFile(this.getClass().getResource("/metamodel/5_fields.json")));
        Query query = QueryBuilder.with().formDefinition(data).createTable();
        String expected = "CREATE TABLE delivery_details_and_pnc1 (ID SERIAL PRIMARY KEY,entityId VARCHAR(255),created_at timestamp default current_timestamp,uuid VARCHAR(255),today VARCHAR(255),pregnancyId VARCHAR(255),womanId VARCHAR(255));";
        assertEquals(expected, query.asSql());
    }

    @Test
    public void shouldBuildAnInsertQuery() throws IOException {
        String data = FileUtils.readFileToString(FileUtils.toFile(this.getClass().getResource("/metamodel/sample.json")));
        Query query = QueryBuilder.with().formDefinition(data).createEntity();
        String expected = "INSERT INTO delivery_details_and_pnc1 (formhub,uuid,today,entityId) VALUES ('sample1','sample2','sample3','42');";
        assertEquals(expected, query.asSql());
    }
}