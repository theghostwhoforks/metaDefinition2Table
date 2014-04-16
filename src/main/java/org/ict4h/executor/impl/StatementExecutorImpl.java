package org.ict4h.executor.impl;

import org.ict4h.constant.Constants;
import org.ict4h.exception.MetaDataServiceRuntimeException;
import org.ict4h.executor.StatementExecutor;
import org.ict4h.model.query.Query;
import org.ict4h.model.query.SelectQuery;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.sql.*;
import java.util.function.Function;

public class StatementExecutorImpl implements StatementExecutor {
    private static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(StatementExecutor.class);

    @Override
    public boolean createTable(Query query, Connection connection) {
        String sqlStatement = query.asSql();
        logger.info(String.format("Creating table. Query - %s", sqlStatement));
        return executeStatement(connection, sqlStatement, Constants.CREATE_TABLE_ERROR);
    }

    @Override
    public int insertIntoTable(Query query, Connection connection) {
        String sqlStatement = query.asSql();
        logger.info(String.format("Inserting into table. Query - %s", sqlStatement));
        return executeQueryReturningInsertedId(connection, sqlStatement, Constants.INSERT_INTO_TABLE_ERROR);
    }

    @Override
    public Pair<String, ResultSetMetaData> getDescribedData(SelectQuery query, Connection connection) {
        ResultSetMetaData metaData = null;
        try {
            PreparedStatement statement = connection.prepareStatement((query).createDescribeQuery());
            metaData = statement.getMetaData();
        } catch (SQLException e) {
            throwException(Constants.DESCRIBE_TABLE_ERROR,e);
        }
        return Pair.of(query.getTableName().toUpperCase(), metaData);
    }

    @Override
    public boolean updateTable(Connection connection, Query updateQuery) {
        executeStatement(connection, updateQuery.asSql(), Constants.UPDATE_TABLE_ERROR);
        return true;
    }

    @Override
    public <T> T selectDataFromTable(Connection connection, Query query,Function<ResultSet,T> function) {
        PreparedStatement statement = null;
        T returnValue = null;
        try {
            statement = connection.prepareStatement(query.asSql());
            ResultSet resultSet = statement.executeQuery();
            returnValue = function.apply(resultSet);
        } catch (SQLException e) {
            throwException(Constants.SELECT_DATA_FROM_TABLE_ERROR,e);
        }finally {
            DbUtils.closeQuietly(statement);
        }
        return returnValue;
    }

    @Override
    public boolean deleteEntity(Connection connection, Query query) {
        executeStatement(connection,query.asSql(),Constants.DELETE_ENTITY_ERROR);
        return true;
    }


    private int executeQueryReturningInsertedId(Connection connection, String sqlStatement, String message) {
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(sqlStatement, new String[]{"id"});
            statement.execute();
            ResultSet resultSet = statement.getGeneratedKeys();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            } else {
                logger.error("Could not find generated key for - {}", sqlStatement);
                throw new MetaDataServiceRuntimeException("Could not find generated key");
            }
        } catch (SQLException e) {
            logger.error(String.format("Inserting into table Failed. Query - %s", sqlStatement));
            return throwException(message, e);
        } finally {
            DbUtils.closeQuietly(statement);
        }
    }

    private int throwException(String message, SQLException e) {
        logger.error(message);
        logger.error(e.getMessage());
        throw new MetaDataServiceRuntimeException(message, e);
    }

    private boolean executeStatement(Connection connection, String sqlStatement, String message) {
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(sqlStatement);
            statement.execute();
        } catch (SQLException e) {
            throwException(message, e);
        }finally {
            DbUtils.closeQuietly(statement);
        }
        return true;
    }
}
