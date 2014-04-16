package org.ict4h.executor.impl;

import org.ict4h.builder.EntityQueryBuilder;
import org.ict4h.builder.SelectQueryBuilder;
import org.ict4h.builder.UpdateQueryBuilder;
import org.ict4h.constant.Constants;
import org.ict4h.exception.MetaDataServiceRuntimeException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Function;

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
        executor.createTable(EntityQueryBuilder.with().nothing(), connection);
        verify(statement).execute();
    }

    @Test
    public void shouldThrowAnExceptionWhenStatementExecutionFails() throws SQLException {
        thrown.expect(MetaDataServiceRuntimeException.class);
        thrown.expectMessage(Constants.CREATE_TABLE_ERROR);
        when(connection.prepareStatement(anyString())).thenThrow(new SQLException());
        executor.createTable(EntityQueryBuilder.with().nothing(), connection);
    }

    @Test
    public void shouldDeleteAnEntity() throws SQLException {
        PreparedStatement statement = mock(PreparedStatement.class);
        when(connection.prepareStatement(anyString())).thenReturn(statement);
        executor.deleteEntity(connection, EntityQueryBuilder.with().nothing());
        verify(statement).execute();
    }

    @Test
    public void shouldThrowAnExceptionWhenDeleteStatementExecutionFails() throws SQLException {
        thrown.expect(MetaDataServiceRuntimeException.class);
        thrown.expectMessage(Constants.DELETE_ENTITY_ERROR);
        when(connection.prepareStatement(anyString())).thenThrow(new SQLException());
        executor.deleteEntity(connection, EntityQueryBuilder.with().nothing());
    }

    @Test
    public void shouldInsertIntoTable() throws SQLException {
        PreparedStatement statement = mock(PreparedStatement.class);
        ResultSet resultSet = mock(ResultSet.class);

        when(connection.prepareStatement(anyString(), (String[]) anyObject())).thenReturn(statement);
        when(statement.getGeneratedKeys()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt(1)).thenReturn(1);

        executor.insertIntoTable(EntityQueryBuilder.with().nothing(), connection);
        verify(statement).execute();
    }

    @Test
    public void shouldThrowAnExceptionWhenInsertStatementExecutionFails() throws SQLException {
        thrown.expect(MetaDataServiceRuntimeException.class);
        thrown.expectMessage(Constants.INSERT_INTO_TABLE_ERROR);
        when(connection.prepareStatement(anyString(), (String[]) anyObject())).thenThrow(new SQLException());
        executor.insertIntoTable(EntityQueryBuilder.with().nothing(), connection);
    }

    @Test
    public void shouldThrowAnExceptionWhenDescribeStatementExecutionFails() throws SQLException {
        thrown.expect(MetaDataServiceRuntimeException.class);
        thrown.expectMessage(Constants.DESCRIBE_TABLE_ERROR);
        when(connection.prepareStatement(anyString())).thenThrow(new SQLException());
        executor.getDescribedData(SelectQueryBuilder.with().nothing(), connection);
    }


    @Test
    public void shouldGiveMetaDataOfATable() throws SQLException {
        PreparedStatement statement = mock(PreparedStatement.class);
        when(connection.prepareStatement(anyString())).thenReturn(statement);
        executor.getDescribedData(SelectQueryBuilder.with().nothing(), connection);
        verify(statement).getMetaData();
    }

    @Test
    public void shouldThrowAnExceptionWhenUpdateStatementExecutionFails() throws SQLException {
        thrown.expect(MetaDataServiceRuntimeException.class);
        thrown.expectMessage(Constants.UPDATE_TABLE_ERROR);
        when(connection.prepareStatement(anyString())).thenThrow(new SQLException());
        executor.updateTable(connection, UpdateQueryBuilder.with().nothing());
    }

    @Test
    public void shouldUpdateATable() throws SQLException {
        PreparedStatement statement = mock(PreparedStatement.class);
        when(connection.prepareStatement(anyString())).thenReturn(statement);
        executor.updateTable(connection, UpdateQueryBuilder.with().nothing());
        verify(statement).execute();
    }

    @Test
    public void shouldThrowAnExceptionWhenSelectStatementExecutionFails() throws SQLException {
        thrown.expect(MetaDataServiceRuntimeException.class);
        thrown.expectMessage(Constants.SELECT_DATA_FROM_TABLE_ERROR);
        when(connection.prepareStatement(anyString())).thenThrow(new SQLException());
        executor.selectDataFromTable(connection, SelectQueryBuilder.with().nothing(), Function.identity());
    }

    @Test
    public void shouldGetDataFromATable() throws SQLException {
        PreparedStatement statement = mock(PreparedStatement.class);
        when(connection.prepareStatement(anyString())).thenReturn(statement);
        executor.selectDataFromTable(connection, SelectQueryBuilder.with().nothing(),Function.identity());
        verify(statement).executeQuery();
    }
}