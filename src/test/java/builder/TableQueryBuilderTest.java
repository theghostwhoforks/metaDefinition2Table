package builder;

import model.query.FormTableQueryMultiMap;
import model.query.Query;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;

public class TableQueryBuilderTest {

    @Test
    public void shouldBuildAQuery() throws IOException {
        String data = FileUtils.readFileToString(FileUtils.toFile(this.getClass().getResource("/metamodel/5_fields.json")));
        FormTableQueryMultiMap map = TableQueryBuilder.with().formDefinition(data).create();
        String expected = "CREATE TABLE delivery_details_and_pnc1 (ID SERIAL PRIMARY KEY,entity_id VARCHAR(255),created_at timestamp default current_timestamp,uuid VARCHAR(255),today VARCHAR(255),pregnancyId VARCHAR(255),womanId VARCHAR(255));";
        assertEquals(expected, map.getTableQuery().asSql());
    }

    @Test
    public void shouldBuildACreateTableQueryWithSubForms() throws IOException {
        String data = FileUtils.readFileToString(FileUtils.toFile(this.getClass().getResource("/metamodel/subForms.json")));
        FormTableQueryMultiMap query = TableQueryBuilder.with().formDefinition(data).create();

        String expectedIndependentTableCreationQuery = "CREATE TABLE doctor_visit (ID SERIAL PRIMARY KEY,entity_id VARCHAR(255),created_at timestamp default current_timestamp,doctor_name VARCHAR(255));";
        assertEquals(expectedIndependentTableCreationQuery, query.getTableQuery().asSql());

        List<String> expected = new ArrayList<>();
        expected.add("CREATE TABLE medications_doctor_visit (ID SERIAL PRIMARY KEY,created_at timestamp default current_timestamp,medicationName VARCHAR(255),dose VARCHAR(255),parent_id Integer references doctor_visit (ID) ON DELETE CASCADE);");
        expected.add("CREATE TABLE tests_doctor_visit (ID SERIAL PRIMARY KEY,created_at timestamp default current_timestamp,testRequired VARCHAR(255),testRequiredName VARCHAR(255),parent_id Integer references doctor_visit (ID) ON DELETE CASCADE);");

        List<Query> actual = query.getLinkedTableQueries();

        assertEquals(expected.get(0), actual.get(0).asSql());
        assertEquals(expected.get(1), actual.get(1).asSql());
    }
}