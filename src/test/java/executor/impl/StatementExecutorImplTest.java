package executor.impl;

import builder.QueryBuilder;
import model.Query;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class StatementExecutorImplTest {

    private StatementExecutorImpl executor;
    private Connection connection;

    @Before
    public void setUp() {
        executor = new StatementExecutorImpl();
        connection = mock(Connection.class);
    }

    @Test
    public void shouldCreateTable() throws SQLException {
        Query emptyQuery = QueryBuilder.empty();
        PreparedStatement statement = mock(PreparedStatement.class);
        when(connection.prepareStatement("")).thenReturn(statement);
        executor.createTable(emptyQuery, connection);
        verify(statement).execute();
    }
}
