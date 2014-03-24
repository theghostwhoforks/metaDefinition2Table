package service;

import executor.StatementExecutor;
import model.Query;
import org.junit.Before;
import org.junit.Test;
import service.impl.SqlServiceImpl;

import java.sql.Connection;
import java.sql.SQLException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

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
        service.createTable(connection,data);
        verify(executor).createTable(new Query("CREATE TABLE OOGA (ID SERIAL PRIMARY KEY,BOOGA VARCHAR(255));"), connection);
    }

    @Test
    public void shouldInsertIntoATable() throws SQLException {
        String data = "{\"form\" : {\"bind_type\" : \"OOGA\", \"fields\" : [{\"name\" : \"BOOGA\",\"value\" : \"TEST\"},{\"name\" : \"SOOGA\"}]}}";
        service.createEntity(connection, data);
        verify(executor).insertIntoTable(new Query("INSERT INTO OOGA (BOOGA) VALUES ('TEST');"), connection);
    }
}
