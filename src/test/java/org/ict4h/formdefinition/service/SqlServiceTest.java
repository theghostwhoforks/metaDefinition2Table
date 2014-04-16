package org.ict4h.formdefinition.service;

import org.ict4h.formdefinition.executor.StatementExecutor;
import org.ict4h.formdefinition.model.Field;
import org.ict4h.formdefinition.model.query.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Before;
import org.junit.Test;
import org.ict4h.formdefinition.service.impl.SqlServiceImpl;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;

public class SqlServiceTest {

    private StatementExecutor executor;
    private SqlServiceImpl service;
    private Connection connection;

    @Before
    public void setUp() {
        executor = mock(StatementExecutor.class);
        connection = mock(Connection.class);
        service = new SqlServiceImpl(executor);
    }

    @Test
    public void shouldCreateATable() throws SQLException {
        String data = "{\"form\" : {\"bind_type\" : \"OOGA\", \"fields\" : [{\"name\" : \"BOOGA\"}]}}";

        String tableName = "OOGA";
        List<Field> list = new ArrayList<>();
        list.add(new Field("BOOGA", null));

        CreateIndependentQuery independentQuery = new CreateIndependentQuery(list, tableName);

        service.createTable(connection, data);
        verify(executor).createTable(independentQuery, connection);
    }

    @Test
    public void shouldInsertIntoATable() throws SQLException {
        String data = "{\"form\" : {\"bind_type\" : \"OOGA\", \"fields\" : [{\"name\" : \"BOOGA\",\"value\" : \"TEST\"},{\"name\" : \"SOOGA\"}]}}";
        service.createEntity(connection, data, "dataEntry1");
        verify(executor).insertIntoTable(any(InsertQuery.class), any(Connection.class));
    }

    @Test
    public void shouldCreateATableWithSubForms() throws SQLException, IOException {
        String data = FileUtils.readFileToString(FileUtils.toFile(this.getClass().getResource("/metamodel/sampleData.json")));

        String tableName = "OOGA";
        String dependentTableName = "medications";

        List<Field> list = new ArrayList<>();
        list.add(new Field("BOOGA", null));

        CreateIndependentQuery independentQuery = new CreateIndependentQuery(list, tableName);
        CreateDependentQuery dependentQuery = new CreateDependentQuery(list, dependentTableName, tableName);

        when(executor.createTable(independentQuery, connection)).thenReturn(true);
        service.createTable(connection, data);

        verify(executor).createTable(dependentQuery, connection);
    }

    @Test
    public void shouldInsertIntoNestedTables() throws SQLException, IOException {
        String data = FileUtils.readFileToString(FileUtils.toFile(this.getClass().getResource("/metamodel/subForms.json")));

        InsertQuery parentQuery = new InsertQuery("doctor_visit", Stream.of(new Field("entity_id", "B801"), new Field("modified_by", "dataEntry1")));
        InsertQuery subFormQuery = new InsertQuery("medications_doctor_visit", Stream.of(new Field("medicationName", "sample"), new Field("parent_id", "1"), new Field("modified_by", "dataEntry1")));
        when(executor.insertIntoTable(parentQuery, connection)).thenReturn(1);

        service.createEntity(connection, data, "dataEntry1");

        verify(executor).insertIntoTable(subFormQuery, connection);
    }

    @Test
    public void shouldDeleteAnEntity() throws SQLException, IOException {
        String data = FileUtils.readFileToString(FileUtils.toFile(this.getClass().getResource("/metamodel/5_fields.json")));
        int id = 1;
        service.updateEntity(connection, data, id, "dataEntry1");

        DeleteEntityQuery query = new DeleteEntityQuery("delivery_details_and_pnc1",id);

        verify(executor).deleteEntity(connection, query);
        verify(executor).insertIntoTable(any(InsertQuery.class),any(Connection.class));
    }

    @Test
    public void shouldUpdateATable() throws SQLException, IOException {
        ResultSetMetaData parentTableResultSetMock = mock(ResultSetMetaData.class);
        ResultSetMetaData firstDependentTableResultSetMock = mock(ResultSetMetaData.class);
        ResultSetMetaData secondDependentTableResultSetMock = mock(ResultSetMetaData.class);

        String data = FileUtils.readFileToString(FileUtils.toFile(this.getClass().getResource("/metamodel/update/sub_forms_002.json")));
        SelectQuery parentTableSelectQuery = new SelectQuery("doctor_visit");
        SelectQuery firstDependentTableSelectQuery = new SelectQuery("medications_doctor_visit");
        SelectQuery secondDependentTableSelectQuery = new SelectQuery("tests_doctor_visit");

        when(executor.getDescribedData(parentTableSelectQuery, connection)).thenReturn(Pair.of("DOCTOR_VISIT", parentTableResultSetMock));

        when(parentTableResultSetMock.getColumnCount()).thenReturn(1);
        when(parentTableResultSetMock.getColumnName(1)).thenReturn("DOCTOR_NAME");

        when(executor.getDescribedData(firstDependentTableSelectQuery, connection)).thenReturn(Pair.of("MEDICATIONS_DOCTOR_VISIT", firstDependentTableResultSetMock));
        when(firstDependentTableResultSetMock.getColumnCount()).thenReturn(1);
        when(firstDependentTableResultSetMock.getColumnName(1)).thenReturn("MEDICATIONNAME");

        when(executor.getDescribedData(secondDependentTableSelectQuery, connection)).thenReturn(Pair.of("TESTS_DOCTOR_VISIT", secondDependentTableResultSetMock));
        when(secondDependentTableResultSetMock.getColumnCount()).thenReturn(1);
        when(secondDependentTableResultSetMock.getColumnName(1)).thenReturn("TESTREQUIRED");

        Field field = new Field("doctor_first_name", "");
        List<Field> fields = new ArrayList();
        fields.add(field);
        Set<String> updatedParentTableData = new HashSet();
        updatedParentTableData.add(field.getName());

        Field field1 = new Field("dose", "");
        List<Field> fields1 = new ArrayList();
        fields1.add(field1);
        Set<String> firstDependentTableData = new HashSet();
        firstDependentTableData.add(field1.getName());

        Field field2 = new Field("testRequiredName", "");
        List<Field> fields2 = new ArrayList();
        fields2.add(field2);
        Set<String> secondDependentTableData = new HashSet();
        secondDependentTableData.add(field2.getName());

        service.updateTable(connection, data);

        verify(executor).updateTable(connection, new UpdateQuery("doctor_visit", fields, updatedParentTableData));
        verify(executor).updateTable(connection, new UpdateQuery("medications_doctor_visit", fields1, firstDependentTableData));
        verify(executor).updateTable(connection, new UpdateQuery("tests_doctor_visit", fields2, secondDependentTableData));
    }
}
