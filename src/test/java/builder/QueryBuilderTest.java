package builder;

import model.Query;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

public class QueryBuilderTest {
    @Test
    public void shouldBuildAQuery() throws IOException {
        String data = FileUtils.readFileToString(FileUtils.toFile(this.getClass().getResource("/metamodel/5_fields.json")));
        List<Query> query = QueryBuilder.with().formDefinition(data).createNestedTables();
        String expected = "CREATE TABLE delivery_details_and_pnc1 (ID SERIAL PRIMARY KEY,entityId VARCHAR(255),created_at timestamp default current_timestamp,uuid VARCHAR(255),today VARCHAR(255),pregnancyId VARCHAR(255),womanId VARCHAR(255));";
        assertEquals(expected, query.get(0).asSql());
    }

    @Test
    public void shouldBuildAnInsertQuery() throws IOException {
        String data = FileUtils.readFileToString(FileUtils.toFile(this.getClass().getResource("/metamodel/sample.json")));
        Query query = QueryBuilder.with().formDefinition(data).createEntity();
        String expected = "INSERT INTO delivery_details_and_pnc1 (formhub,uuid,today,entityId) VALUES ('sample1','sample2','sample3','42');";
        assertEquals(expected, query.asSql());
    }

    @Test
    public void shouldReturnTrueWhenFormDefinitionDoesHaveSubForms() throws IOException {
        String data = FileUtils.readFileToString(FileUtils.toFile(this.getClass().getResource("/metamodel/subForms.json")));
        boolean hasSubForms = QueryBuilder.with().formDefinition(data).hasSubFormsInFormDefinition();

        assertEquals(true,hasSubForms);
    }

    @Test
    public void shouldReturnFalseWhenFormDefinitionDoesNotHaveSubForms() throws IOException {
        String data = FileUtils.readFileToString(FileUtils.toFile(this.getClass().getResource("/metamodel/sample.json")));
        boolean hasSubForms = QueryBuilder.with().formDefinition(data).hasSubFormsInFormDefinition();

        assertEquals(false,hasSubForms);
    }

    @Test
    public void shouldBuildACreateTableQueryWithSubForms() throws IOException {
        String data = FileUtils.readFileToString(FileUtils.toFile(this.getClass().getResource("/metamodel/subForms.json")));
        List<Query> queries = QueryBuilder.with().formDefinition(data).createNestedTables();

        ArrayList<Query> list = new ArrayList<>();
        list.add(new Query("CREATE TABLE doctor_visit (ID SERIAL PRIMARY KEY,entityId VARCHAR(255),created_at timestamp default current_timestamp,instanceID VARCHAR(255));"));
        list.add(new Query("CREATE TABLE medications_doctor_visit (ID SERIAL PRIMARY KEY,entityId VARCHAR(255),created_at timestamp default current_timestamp,medicationName VARCHAR(255),dose VARCHAR(255),parent_form_id Integer references doctor_visit (ID));"));
        list.add(new Query("CREATE TABLE tests_doctor_visit (ID SERIAL PRIMARY KEY,entityId VARCHAR(255),created_at timestamp default current_timestamp,testRequired VARCHAR(255),testRequiredName VARCHAR(255),parent_form_id Integer references doctor_visit (ID));"));

        assertEquals(queries.get(0).asSql(), list.get(0).asSql());
        assertEquals(queries.get(1).asSql(),list.get(1).asSql());
        assertEquals(queries.get(2).asSql(),list.get(2).asSql());
    }
}