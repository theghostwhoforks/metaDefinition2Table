package builder;

import model.query.Query;
import model.query.SimpleQuery;
import model.query.TableCreateQuery;
import org.apache.commons.io.FileUtils;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static junit.framework.Assert.assertEquals;

public class TableQueryBuilderTest {

    @Test
    public void shouldBuildAQuery() throws IOException {
        String data = FileUtils.readFileToString(FileUtils.toFile(this.getClass().getResource("/metamodel/5_fields.json")));
        TableCreateQuery query = TableQueryBuilder.with().formDefinition(data).create();
        Query expected = new SimpleQuery("CREATE TABLE delivery_details_and_pnc1 (ID SERIAL PRIMARY KEY,entity_id VARCHAR(255),created_at timestamp default current_timestamp,uuid VARCHAR(255),today VARCHAR(255),pregnancyId VARCHAR(255),womanId VARCHAR(255));");
        assertEquals(expected, query.getTableQuery());
    }

    @Ignore
    //TODO: Fix ASAP
    public void shouldBuildACreateTableQueryWithSubForms() throws IOException {
        String data = FileUtils.readFileToString(FileUtils.toFile(this.getClass().getResource("/metamodel/subForms.json")));
        TableCreateQuery query = TableQueryBuilder.with().formDefinition(data).create();

        List<Query> expected = new ArrayList<>();
        expected.add(new SimpleQuery("CREATE TABLE doctor_visit (ID SERIAL PRIMARY KEY,entity_id VARCHAR(255),created_at timestamp default current_timestamp,instanceID VARCHAR(255));"));
        expected.add(new SimpleQuery("CREATE TABLE medications_doctor_visit (ID SERIAL PRIMARY KEY,entity_id VARCHAR(255),created_at timestamp default current_timestamp,medicationName VARCHAR(255),dose VARCHAR(255),parent_id Integer references doctor_visit (ID));"));
        expected.add(new SimpleQuery("CREATE TABLE tests_doctor_visit (ID SERIAL PRIMARY KEY,entity_id VARCHAR(255),created_at timestamp default current_timestamp,testRequired VARCHAR(255),testRequiredName VARCHAR(255),parent_id Integer references doctor_visit (ID));"));

        List<SimpleQuery> actual = query.getLinkedTableQueries().collect(Collectors.toList());

        assertEquals(expected.get(0), query.getTableQuery());
        assertEquals(expected.get(1), actual.get(0));
        assertEquals(expected.get(2), actual.get(1));
    }

    @Test
    public void shouldBuildADescribeQuery() throws IOException {
        String data = FileUtils.readFileToString(FileUtils.toFile(this.getClass().getResource("/metamodel/subForms.json")));
        SimpleQuery query = SelectQueryBuilder.with().formDefinition(data).createDescribeQuery();
        assertEquals(new SimpleQuery("SELECT * FROM doctor_visit LIMIT 1;"),query);
    }
}