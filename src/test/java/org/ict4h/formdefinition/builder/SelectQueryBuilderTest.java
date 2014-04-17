package org.ict4h.formdefinition.builder;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.ict4h.formdefinition.model.query.FormTableQueryMultiMap;
import org.ict4h.formdefinition.model.query.Query;
import org.ict4h.formdefinition.model.query.SelectQuery;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static junit.framework.Assert.assertEquals;

public class SelectQueryBuilderTest {

    @Test
    public void shouldBuildADescribeQuery() throws IOException {
        String data = FileUtils.readFileToString(FileUtils.toFile(this.getClass().getResource("/metamodel/subForms.json")));
        List<SelectQuery> queries = SelectQueryBuilder.with().formDefinition(data).createDescribeQuery();

        String ParentTableQuery = "SELECT * FROM doctor_visit LIMIT 1;";
        String firstDependentTableQuery = "SELECT * FROM medications_doctor_visit LIMIT 1;";
        String secondDependentTableQuery = "SELECT * FROM tests_doctor_visit LIMIT 1;";

        assertEquals(ParentTableQuery, queries.get(0).createDescribeQuery());
        assertEquals(firstDependentTableQuery, queries.get(1).createDescribeQuery());
        assertEquals(secondDependentTableQuery, queries.get(2).createDescribeQuery());
    }

    @Test
    public void shouldBuildASelectQueryForProvidedId() throws IOException {
        int id = 1;
        String definition = FileUtils.readFileToString(FileUtils.toFile(this.getClass().getResource("/metamodel/subForms.json")));
        FormTableQueryMultiMap<SelectQuery> queries = SelectQueryBuilder.with().formDefinition(definition).createSelectQueriesFor(id);

        String parentTableQuery = "SELECT * FROM doctor_visit WHERE ID = 1;";
        String firstDependentTableQuery = "SELECT * FROM medications_doctor_visit WHERE parent_id = 1;";
        String secondDependentTableQuery = "SELECT * FROM tests_doctor_visit WHERE parent_id = 1;";

        assertEquals(parentTableQuery, queries.getTableQuery().asSql());
        assertEquals(firstDependentTableQuery, queries.getLinkedTableQueries().get(0).asSql());
        assertEquals(secondDependentTableQuery, queries.getLinkedTableQueries().get(1).asSql());
    }

    @Test
    public void shouldBuildASelectQueryForProvidedBeneficiaryId() throws IOException {
        String entityId = "W313";
        String tableName = "doctor_visit";
        SelectQuery selectQuery = SelectQueryBuilder.with().listMetadata(entityId, tableName);

        String expected = "SELECT id,modified_by,modified_at FROM doctor_visit WHERE ENTITY_ID = 'W313';";

        assertEquals(expected, selectQuery.asSql());
    }

    @Test
    public void shouldBuildWhereQueryWithSpecifiedFieldAndValue() throws Exception {
        Query query = SelectQueryBuilder.with().createSelectQueryFor("doctor_visit", Pair.of("OOGA", "BOOGA"));
        assertEquals("SELECT * FROM doctor_visit WHERE OOGA = 'BOOGA';",query.asSql());
    }
}
