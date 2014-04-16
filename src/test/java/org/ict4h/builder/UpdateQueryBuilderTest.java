package org.ict4h.builder;

import org.ict4h.model.query.Query;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.IOException;
import java.util.*;

import static junit.framework.Assert.assertEquals;

public class UpdateQueryBuilderTest {
    @Test
    public void shouldBuildAnUpdateQuery() throws IOException {
        String data = FileUtils.readFileToString(FileUtils.toFile(this.getClass().getResource("/metamodel/5_fields.json")));

        Set<String> columns = new HashSet();
        columns.add("PREGNANCYID");
        columns.add("TODAY");
        columns.add("UUID");
        columns.add("FORMHUB");

        Map<String,Set<String>> allColumns = new HashMap();
        allColumns.put("DELIVERY_DETAILS_AND_PNC1",columns);

        List<Query> query = UpdateQueryBuilder.with().formDefinition(data).update(allColumns);
        String expected = "ALTER TABLE delivery_details_and_pnc1 ADD COLUMN womanId VARCHAR(255);";

        assertEquals(expected,query.get(0).asSql());
    }

    @Test
    public void shouldGiveAnEmptyStringWhenThereAreNoExtraFields() throws IOException {
        String data = FileUtils.readFileToString(FileUtils.toFile(this.getClass().getResource("/metamodel/5_fields.json")));

        Set<String> columns = new HashSet();
        columns.add("PREGNANCYID");
        columns.add("TODAY");
        columns.add("UUID");
        columns.add("FORMHUB");
        columns.add("WOMANID");

        Map<String,Set<String>> allColumns = new HashMap();
        allColumns.put("DELIVERY_DETAILS_AND_PNC1",columns);

        List<Query> query = UpdateQueryBuilder.with().formDefinition(data).update(allColumns);
        String expected = "";

        assertEquals(expected,query.get(0).asSql());
    }

    @Test
    public void shouldBuildAListOfUpdateQueriesForLinkedTables() throws IOException {
        String data = FileUtils.readFileToString(FileUtils.toFile(this.getClass().getResource("/metamodel/update/sub_forms_002.json")));

        Set<String> parentTableColumns = new HashSet();
        parentTableColumns.add("DOCTOR_NAME");
        parentTableColumns.add("ENTITY_ID");

        Set<String> firstDependentTableColumns = new HashSet();
        firstDependentTableColumns.add("MEDICATIONNAME");

        Set<String> secondDependentTableColumns = new HashSet();
        secondDependentTableColumns.add("TESTREQUIRED");

        Map<String,Set<String>> columns = new HashMap();
        columns.put("DOCTOR_VISIT", parentTableColumns);
        columns.put("MEDICATIONS_DOCTOR_VISIT", firstDependentTableColumns);
        columns.put("TESTS_DOCTOR_VISIT", secondDependentTableColumns);


        List<Query> updateQueries = UpdateQueryBuilder.with().formDefinition(data).update(columns);
        String parentTableUpdateQuery = "ALTER TABLE doctor_visit ADD COLUMN doctor_first_name VARCHAR(255);";
        String firstDependentTableUpdateQuery = "ALTER TABLE medications_doctor_visit ADD COLUMN dose VARCHAR(255);";
        String secondDependentTableUpdateQuery = "ALTER TABLE tests_doctor_visit ADD COLUMN testRequiredName VARCHAR(255);";

        assertEquals(parentTableUpdateQuery ,updateQueries.get(0).asSql());
        assertEquals(firstDependentTableUpdateQuery ,updateQueries.get(1).asSql());
        assertEquals(secondDependentTableUpdateQuery , updateQueries.get(2).asSql());
    }



}
