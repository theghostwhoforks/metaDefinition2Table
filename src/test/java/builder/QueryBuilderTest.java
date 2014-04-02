package builder;


import model.query.Query;
import model.query.SimpleQuery;
import model.query.TableCreateQuery;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

public class QueryBuilderTest {
    @Test
    public void shouldBuildAQuery() throws IOException {
        String data = FileUtils.readFileToString(FileUtils.toFile(this.getClass().getResource("/metamodel/5_fields.json")));
        TableCreateQuery query = TableQueryBuilder.with().formDefinition(data).create();
        Query expected = new SimpleQuery("CREATE TABLE delivery_details_and_pnc1 (ID SERIAL PRIMARY KEY,entityId VARCHAR(255),created_at timestamp default current_timestamp,uuid VARCHAR(255),today VARCHAR(255),pregnancyId VARCHAR(255),womanId VARCHAR(255));");
        assertEquals(expected, query.getTableQuery());
    }

    @Test
    public void shouldBuildAnInsertQuery() throws IOException {
        String data = FileUtils.readFileToString(FileUtils.toFile(this.getClass().getResource("/metamodel/sample.json")));
        Query query = EntityQueryBuilder.with().formDefinition(data).createEntity();
        Query expected = new SimpleQuery("INSERT INTO delivery_details_and_pnc1 (formhub,uuid,today,entityId) VALUES ('sample1','sample2','sample3','42');");
        assertEquals(expected, query);
    }

    @Test
    public void shouldBuildACreateTableQueryWithSubForms() throws IOException {
        String data = FileUtils.readFileToString(FileUtils.toFile(this.getClass().getResource("/metamodel/subForms.json")));
        TableCreateQuery query = TableQueryBuilder.with().formDefinition(data).create();

        List<Query> expected = new ArrayList<>();
        expected.add(new SimpleQuery("CREATE TABLE doctor_visit (ID SERIAL PRIMARY KEY,entityId VARCHAR(255),created_at timestamp default current_timestamp,instanceID VARCHAR(255));"));
        expected.add(new SimpleQuery("CREATE TABLE medications_doctor_visit (ID SERIAL PRIMARY KEY,entityId VARCHAR(255),created_at timestamp default current_timestamp,medicationName VARCHAR(255),dose VARCHAR(255),parent_form_id Integer references doctor_visit (ID));"));
        expected.add(new SimpleQuery("CREATE TABLE tests_doctor_visit (ID SERIAL PRIMARY KEY,entityId VARCHAR(255),created_at timestamp default current_timestamp,testRequired VARCHAR(255),testRequiredName VARCHAR(255),parent_form_id Integer references doctor_visit (ID));"));

        List<SimpleQuery> actual = query.getLinkedTableQueries().collect(Collectors.toList());

        assertEquals(expected.get(0), query.getTableQuery());
        assertEquals(expected.get(1), actual.get(0));
        assertEquals(expected.get(2), actual.get(1));
    }

    @Test
    public void shouldBuildAInsertQueryWithSubForms() throws IOException {
        String data = FileUtils.readFileToString(FileUtils.toFile(this.getClass().getResource("/metamodel/subForms.json")));
        List<Query> queries = EntityQueryBuilder.with().formDefinition(data).createSubEntities(1);

        List<Query> list = new ArrayList<>();
        list.add(new SimpleQuery("INSERT INTO medications_doctor_visit (medicationName,parent_form_id) VALUES ('sample',1);"));
        list.add(new SimpleQuery("INSERT INTO tests_doctor_visit (testRequired,parent_form_id) VALUES ('sample2',1);"));

        assertEquals(list.get(0), queries.get(0));
        assertEquals(list.get(1), queries.get(1));
    }

    @Test
    public void shouldBuildADescribeQuery() throws IOException {
        String data = FileUtils.readFileToString(FileUtils.toFile(this.getClass().getResource("/metamodel/subForms.json")));
        SimpleQuery query = SelectQueryBuilder.with().formDefinition(data).createDescribeQuery();
        assertEquals(new SimpleQuery("SELECT * FROM doctor_visit"),query);
    }

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