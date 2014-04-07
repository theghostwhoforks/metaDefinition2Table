package builder;

import model.query.Query;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static junit.framework.Assert.assertEquals;

public class SelectQueryBuilderTest {

    @Test
    public void shouldBuildADescribeQuery() throws IOException {
        String data = FileUtils.readFileToString(FileUtils.toFile(this.getClass().getResource("/metamodel/subForms.json")));
        List<Query> queries = SelectQueryBuilder.with().formDefinition(data).createDescribeQuery();

        String ParentTableQuery = "SELECT * FROM doctor_visit LIMIT 1;";
        String firstDependentTableQuery = "SELECT * FROM medications_doctor_visit LIMIT 1;";
        String secondDependentTableQuery = "SELECT * FROM tests_doctor_visit LIMIT 1;";

        assertEquals(ParentTableQuery, queries.get(0).asSql());
        assertEquals(firstDependentTableQuery, queries.get(1).asSql());
        assertEquals(secondDependentTableQuery, queries.get(2).asSql());
    }
}
