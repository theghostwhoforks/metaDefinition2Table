package org.ict4h.builder;

import org.ict4h.model.query.CreateIndependentQuery;
import org.ict4h.model.query.FormTableQueryMultiMap;
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
        FormTableQueryMultiMap<CreateIndependentQuery> map = TableQueryBuilder.with().formDefinition(data).create();
        String expected = "CREATE TABLE delivery_details_and_pnc1 (ID SERIAL PRIMARY KEY,entity_id VARCHAR(255),modified_at timestamp default current_timestamp,modified_by VARCHAR(255),uuid VARCHAR(255),today VARCHAR(255),pregnancyId VARCHAR(255),womanId VARCHAR(255));";
        assertEquals(expected, map.getTableQuery().asSql());
    }

    @Test
    public void shouldBuildACreateTableQueryWithSubForms() throws IOException {
        String data = FileUtils.readFileToString(FileUtils.toFile(this.getClass().getResource("/metamodel/subForms.json")));
        FormTableQueryMultiMap<CreateIndependentQuery> query = TableQueryBuilder.with().formDefinition(data).create();

        String expectedIndependentTableCreationQuery = "CREATE TABLE doctor_visit (ID SERIAL PRIMARY KEY,entity_id VARCHAR(255),modified_at timestamp default current_timestamp,modified_by VARCHAR(255),doctor_name VARCHAR(255));";
        assertEquals(expectedIndependentTableCreationQuery, query.getTableQuery().asSql());

        List<String> expected = new ArrayList<>();
        expected.add("CREATE TABLE medications_doctor_visit (ID SERIAL PRIMARY KEY,modified_at timestamp default current_timestamp,modified_by VARCHAR(255),medicationName VARCHAR(255),dose VARCHAR(255),parent_id Integer references doctor_visit (ID) ON DELETE CASCADE);");
        expected.add("CREATE TABLE tests_doctor_visit (ID SERIAL PRIMARY KEY,modified_at timestamp default current_timestamp,modified_by VARCHAR(255),testRequired VARCHAR(255),testRequiredName VARCHAR(255),parent_id Integer references doctor_visit (ID) ON DELETE CASCADE);");

        List<CreateIndependentQuery> actual = query.getLinkedTableQueries();

        assertEquals(expected.get(0), actual.get(0).asSql());
        assertEquals(expected.get(1), actual.get(1).asSql());
    }
}