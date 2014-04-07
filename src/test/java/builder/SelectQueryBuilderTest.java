package builder;

import model.query.FormTableQueryMultiMap;
import model.query.Query;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
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

    @Test
    public void shouldBuildASelectQueryForProvidedId() throws IOException {

        String formName = "doctor_visit";
        List<String> subForms = Arrays.asList("medications_doctor_visit", "tests_doctor_visit");
        int id = 1;
        FormTableQueryMultiMap queries = SelectQueryBuilder.with().createSelectQueriesFor(id, formName, subForms);

        String parentTableQuery = "SELECT * FROM doctor_visit WHERE ID = 1;";
        String firstDependentTableQuery = "SELECT * FROM medications_doctor_visit WHERE parent_id = 1;";
        String secondDependentTableQuery = "SELECT * FROM tests_doctor_visit WHERE parent_id = 1;";

        assertEquals(parentTableQuery, queries.getTableQuery().asSql());
        assertEquals(firstDependentTableQuery, queries.getLinkedTableQueries().get(0).asSql());
        assertEquals(secondDependentTableQuery, queries.getLinkedTableQueries().get(1).asSql());
    }
}
