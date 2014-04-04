package builder;

import model.query.Query;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;

public class EntityQueryBuilderTest {

    @Test
    public void shouldBuildAnInsertQuery() throws IOException {
        String data = FileUtils.readFileToString(FileUtils.toFile(this.getClass().getResource("/metamodel/sample.json")));
        Query query = EntityQueryBuilder.with().formDefinition(data).createEntity();
        String expected = "INSERT INTO delivery_details_and_pnc1 (uuid,today,entity_id) VALUES ('sample2','sample3','42');";
        assertEquals(expected, query.asSql());
    }

    @Test
    public void shouldBuildAInsertQueryWithSubForms() throws IOException {
        String data = FileUtils.readFileToString(FileUtils.toFile(this.getClass().getResource("/metamodel/subForms.json")));
        List<Query> queries = EntityQueryBuilder.with().formDefinition(data).createSubEntities(1);

        List<String> expectedList = new ArrayList<>();
        expectedList.add("INSERT INTO medications_doctor_visit (medicationName,parent_id) VALUES ('sample','1');");
        expectedList.add("INSERT INTO medications_doctor_visit (medicationName,parent_id) VALUES ('sample1','1');");
        expectedList.add("INSERT INTO tests_doctor_visit (testRequired,parent_id) VALUES ('yes','1');");
        expectedList.add("INSERT INTO tests_doctor_visit (testRequired,parent_id) VALUES ('no','1');");

        assertEquals(expectedList.get(0), queries.get(0).asSql());
        assertEquals(expectedList.get(1), queries.get(1).asSql());
        assertEquals(expectedList.get(2), queries.get(2).asSql());
        assertEquals(expectedList.get(3), queries.get(3).asSql());
    }
}
