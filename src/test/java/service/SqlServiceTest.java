package service;

import executor.StatementExecutor;
import model.Field;
import model.query.*;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import service.impl.SqlServiceImpl;

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
        service.createEntity(connection, data);
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

        InsertQuery parentQuery = new InsertQuery("doctor_visit", Stream.of(new Field("entity_id", "B801")));
        InsertQuery subFormQuery = new InsertQuery("medications_doctor_visit", Stream.of(new Field("medicationName", "sample"), new Field("parent_id", "1")));
        when(executor.insertIntoTable(parentQuery, connection)).thenReturn(1);

        service.createEntity(connection, data);

        verify(executor).insertIntoTable(subFormQuery, connection);
    }

    @Test
    public void shouldUpdateATable() throws SQLException, IOException {
        ResultSetMetaData parentTableResultSetMock = mock(ResultSetMetaData.class);
        ResultSetMetaData firstDependentTableResultSetMock = mock(ResultSetMetaData.class);
        ResultSetMetaData secondDependentTableResultSetMock = mock(ResultSetMetaData.class);

        String data = FileUtils.readFileToString(FileUtils.toFile(this.getClass().getResource("/metamodel/update/sub_forms_002.json")));
        Query parentTableSelectQuery = new SelectQuery("doctor_visit");
        Query firstDependentTableSelectQuery = new SelectQuery("medications_doctor_visit");
        Query secondDependentTableSelectQuery = new SelectQuery("tests_doctor_visit");

        when(executor.getDescribedData(parentTableSelectQuery, connection)).thenReturn(parentTableResultSetMock);

        when(parentTableResultSetMock.getColumnCount()).thenReturn(1);
        when(parentTableResultSetMock.getColumnName(1)).thenReturn("DOCTOR_NAME");
        when(parentTableResultSetMock.getTableName(1)).thenReturn("DOCTOR_VISIT");

        when(executor.getDescribedData(firstDependentTableSelectQuery, connection)).thenReturn(firstDependentTableResultSetMock);
        when(firstDependentTableResultSetMock.getColumnCount()).thenReturn(1);
        when(firstDependentTableResultSetMock.getTableName(1)).thenReturn("MEDICATIONS_DOCTOR_VISIT");
        when(firstDependentTableResultSetMock.getColumnName(1)).thenReturn("MEDICATIONNAME");

        when(executor.getDescribedData(secondDependentTableSelectQuery, connection)).thenReturn(secondDependentTableResultSetMock);
        when(secondDependentTableResultSetMock.getColumnCount()).thenReturn(1);
        when(secondDependentTableResultSetMock.getColumnName(1)).thenReturn("TESTREQUIRED");
        when(secondDependentTableResultSetMock.getTableName(1)).thenReturn("TESTS_DOCTOR_VISIT");

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

//    @Ignore
//    public void shouldGiveDataForSpecifiedId() throws Exception {
//        String formName = "doctor_visit";
//        String firstDependentTableName = "medications_doctor_visit";
//        String secondDependentTableName = "tests_doctor_visit";
//        List<String> subForms = Arrays.asList(firstDependentTableName, secondDependentTableName);
//        int id = 1;
//
//        ResultSet parentTableResultSetMock = mock(ResultSet.class);
//        ResultSet firstDependentTableResultSetMock = mock(ResultSet.class);
//        ResultSet secondDependentTableResultSetMock = mock(ResultSet.class);
//
//        ResultSetMetaData parentTableMetaData = mock(ResultSetMetaData.class);
//        ResultSetMetaData firstDependentTableMetaData = mock(ResultSetMetaData.class);
//        ResultSetMetaData secondDependentTableMetaData = mock(ResultSetMetaData.class);
//
//        Query parentTableQuery = new SelectQuery(formName, "ID", 1);
//        Query firstDependentTableQuery = new SelectQuery(firstDependentTableName, "parent_id", 1);
//        Query secondDependentTableQuery = new SelectQuery(secondDependentTableName, "parent_id", 1);
//
//        when(executor.selectDataFromTable(connection,parentTableQuery)).thenReturn(parentTableResultSetMock);
//        when(parentTableResultSetMock.getMetaData()).thenReturn(parentTableMetaData);
//        when(parentTableResultSetMock.getString("first")).thenReturn("first");
//        when(parentTableMetaData.getColumnCount()).thenReturn(1);
//        when(parentTableMetaData.getColumnName(1)).thenReturn("first");
//        when(parentTableMetaData.getTableName(1)).thenReturn(formName);
//
//        when(executor.selectDataFromTable(connection,firstDependentTableQuery)).thenReturn(firstDependentTableResultSetMock);
//        when(firstDependentTableResultSetMock.getMetaData()).thenReturn(firstDependentTableMetaData);
//        when(firstDependentTableResultSetMock.getString("second")).thenReturn("second");
//        when(firstDependentTableMetaData.getColumnCount()).thenReturn(1);
//        when(firstDependentTableMetaData.getColumnName(1)).thenReturn("second");
//
//        when(executor.selectDataFromTable(connection,secondDependentTableQuery)).thenReturn(secondDependentTableResultSetMock);
//        when(secondDependentTableResultSetMock.getMetaData()).thenReturn(secondDependentTableMetaData);
//        when(secondDependentTableResultSetMock.getString("third")).thenReturn("third");
//        when(secondDependentTableMetaData.getColumnCount()).thenReturn(1);
//        when(secondDependentTableMetaData.getColumnName(1)).thenReturn("third");
//
//        Form form = service.getDataFor(connection, id, formName, subForms);
//
//        assertNotNull(form);
//        assertEquals(2, form.getSubForms().size());
//        assertEquals(1, form.getFields().size());
//        assertEquals(formName,form.getName());
//
//
//        verify(executor).selectDataFromTable(connection, parentTableQuery);
//
//        verify(executor).selectDataFromTable(connection, firstDependentTableQuery);
//
//        verify(executor).selectDataFromTable(connection, secondDependentTableQuery);
//    }
}
