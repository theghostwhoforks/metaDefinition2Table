package service;

import executor.StatementExecutor;
import model.Field;
import model.query.*;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import service.impl.SqlServiceImpl;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
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

        InsertQuery parentQuery = new InsertQuery("doctor_visit",Stream.of(new Field("entity_id","B801")));
        InsertQuery subFormQuery = new InsertQuery("medications_doctor_visit", Stream.of(new Field("medicationName", "sample"), new Field("parent_id", "1")));
        when(executor.insertIntoTable(parentQuery,connection)).thenReturn(1);

        service.createEntity(connection, data);

        verify(executor).insertIntoTable(subFormQuery, connection);
    }

    @Ignore
    public void shouldDescribeATable() throws SQLException, IOException {
        ResultSetMetaData mock = mock(ResultSetMetaData.class);

        String data = FileUtils.readFileToString(FileUtils.toFile(this.getClass().getResource("/metamodel/sampleDataWithOneFieldExtra.json")));
        service.createTable(connection, data);
        Query query = new SimpleQuery("SELECT * FROM OOGA");
        when(executor.getDescribedData(query, connection)).thenReturn(mock);
        when(mock.getColumnCount()).thenReturn(1);
        when(mock.getColumnName(1)).thenReturn("BOOGA");

        service.updateTable(connection, data);

        verify(mock).getColumnCount();
        verify(mock).getColumnName(1);
        verify(executor).insertIntoTable(new SimpleQuery("INSERT INTO OOGA (BOOGA,SOOGA) VALUES ('TEST','TEST1');"), connection);
    }
}
