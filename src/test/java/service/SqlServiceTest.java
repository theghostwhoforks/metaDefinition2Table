package service;

import executor.StatementExecutor;
import model.Query;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import service.impl.SqlServiceImpl;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyObject;
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
        boolean isCreated = service.createTable(connection, data);

        assertEquals(true,isCreated);
        verify(executor).createTable(new Query("CREATE TABLE OOGA (ID SERIAL PRIMARY KEY,entityId VARCHAR(255),created_at timestamp default current_timestamp,BOOGA VARCHAR(255));"), connection);
    }

    @Test
    public void shouldInsertIntoATable() throws SQLException {
        String data = "{\"form\" : {\"bind_type\" : \"OOGA\", \"fields\" : [{\"name\" : \"BOOGA\",\"value\" : \"TEST\"},{\"name\" : \"SOOGA\"}]}}";
        service.createEntity(connection, data);

        verify(executor).insertIntoTable(new Query("INSERT INTO OOGA (BOOGA) VALUES ('TEST');"), connection);
    }

    @Test
    public void shouldCreateATableWithSubForms() throws SQLException, IOException {
        String data = FileUtils.readFileToString(FileUtils.toFile(this.getClass().getResource("/metamodel/sampleData.txt")));
        boolean isCreated = service.createTable(connection, data);

        Query query = new Query("CREATE TABLE OOGA (ID SERIAL PRIMARY KEY,entityId VARCHAR(255),created_at timestamp default current_timestamp,BOOGA VARCHAR(255));");
        Query query1 = new Query("CREATE TABLE medications_OOGA (ID SERIAL PRIMARY KEY,entityId VARCHAR(255),created_at timestamp default current_timestamp,BOOGA VARCHAR(255),parent_form_id Integer references OOGA (ID));");

        assertEquals(true,isCreated);
        verify(executor).createTable(query, connection);
        verify(executor).createTable(query1, connection);
    }

    @Test
    public void shouldInsertIntoNestedTables() throws SQLException, IOException {
        String data = FileUtils.readFileToString(FileUtils.toFile(this.getClass().getResource("/metamodel/sampleData.txt")));
        service.createTable(connection, data);
        Query query = new Query("INSERT INTO OOGA (BOOGA) VALUES ('TEST');");
        when(executor.insertIntoTable(query,connection)).thenReturn(1);
        service.createEntity(connection,data);
        Query query1 = new Query("INSERT INTO medications_OOGA (BOOGA,parent_form_id) VALUES ('TEST',1);");

        verify(executor).insertIntoTable(query, connection);
        verify(executor).insertIntoTable(query1, connection);
    }
}
