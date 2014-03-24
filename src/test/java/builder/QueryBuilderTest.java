package builder;

import model.Query;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.IOException;

import static junit.framework.Assert.assertEquals;

public class QueryBuilderTest {
    @Test
    public void shouldBuildAQuery() throws IOException {
        String data = FileUtils.readFileToString(FileUtils.toFile(this.getClass().getResource("/metamodel/sample.json")));
        QueryBuilder queryBuilder = QueryBuilder.with().formDefinition(data);
        Query query = queryBuilder.build();
        assertEquals(true, query.asSql().startsWith("CREATE TABLE"));
    }

    @Test
    public void shouldBuildAnInsertQuery() throws IOException {
        String data = FileUtils.readFileToString(FileUtils.toFile(this.getClass().getResource("/metamodel/sample.json")));
        Query query = QueryBuilder.with().formDefinition(data).insert();
        assertEquals(true, query.asSql().startsWith("INSERT INTO "));
        assertEquals(true, query.asSql().contains("VALUES"));
    }

    @Test
    public void shouldBuildAnUpdateTableQuery() throws IOException {
        String data = FileUtils.readFileToString(FileUtils.toFile(this.getClass().getResource("/metamodel/sample.json")));
        Query query = QueryBuilder.with().formDefinition(data).update();
        assertEquals(true, query.asSql().startsWith("ALTER TABLE "));
        assertEquals(true, query.asSql().contains("ADD COLUMN"));
    }
}