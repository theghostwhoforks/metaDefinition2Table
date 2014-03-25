package executor.impl;

import builder.QueryBuilder;
import constant.Constants;
import exception.MetaDataServiceRuntimeException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.mockito.Mockito.*;

public class StatementExecutorImplTest {

    private StatementExecutorImpl executor;
    private Connection connection;

    @Rule
    public ExpectedException thrown= ExpectedException.none();

    @Before
    public void setUp() {
        executor = new StatementExecutorImpl();
        connection = mock(Connection.class);
    }

    @Test
    public void shouldCreateTable() throws SQLException {
        PreparedStatement statement = mock(PreparedStatement.class);
        when(connection.prepareStatement(anyString())).thenReturn(statement);
        executor.createTable(QueryBuilder.with().nothing(), connection);
        verify(statement).execute();
    }

    @Test
    public void shouldThrowAnExceptionWhenStatementExecutionFails() throws SQLException {
        thrown.expect(MetaDataServiceRuntimeException.class);
        thrown.expectMessage(Constants.CREATE_TABLE_ERROR);
        when(connection.prepareStatement(anyString())).thenThrow(new SQLException());
        executor.createTable(QueryBuilder.with().nothing(), connection);
    }

    @Test
    public void shouldInsertIntoTable() throws SQLException {
        PreparedStatement statement = mock(PreparedStatement.class);
        when(connection.prepareStatement(anyString())).thenReturn(statement);
        executor.insertIntoTable(QueryBuilder.with().nothing(), connection);
        verify(statement).execute();
    }

    @Test
    public void shouldThrowAnExceptionWhenInsertStatementExecutionFails() throws SQLException {
        thrown.expect(MetaDataServiceRuntimeException.class);
        thrown.expectMessage(Constants.INSERT_INTO_TABLE_ERROR);
        when(connection.prepareStatement(anyString())).thenThrow(new SQLException());
        executor.insertIntoTable(QueryBuilder.with().nothing(), connection);
    }
}