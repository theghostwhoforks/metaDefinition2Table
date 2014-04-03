package executor.impl;

import constant.Constants;
import exception.MetaDataServiceRuntimeException;
import executor.StatementExecutor;
import model.query.Query;
import org.apache.commons.dbutils.DbUtils;

import java.sql.*;

public class StatementExecutorImpl implements StatementExecutor {
    private static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(StatementExecutor.class);

    @Override
    public boolean createTable(Query query, Connection connection) {
        String sqlStatement = query.asSql();
        logger.info(String.format("Creating table. Query - %s", sqlStatement));
        return executeQuery(connection, sqlStatement, Constants.CREATE_TABLE_ERROR);
    }

    private boolean executeQuery(Connection connection, String sqlStatement, String message) {
        PreparedStatement statement = null;
        try {
            statement = executeStatement(connection, sqlStatement, message);
        } finally {
            try {
                DbUtils.close(statement);
            } catch (SQLException e) {
                throwException("Failed to close statement", e);
            }
        }
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

    private PreparedStatement executeStatement(Connection connection, String sqlStatement, String message) {
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(sqlStatement);
            statement.execute();
        } catch (SQLException e) {
            throwException(message, e);
        }
        return statement;
    }

    @Override
    public int insertIntoTable(Query query, Connection connection) {
        String sqlStatement = query.asSql();
        logger.info(String.format("Inserting into table. Query - %s", sqlStatement));
        return executeQueryReturningInsertedId(connection, sqlStatement, Constants.INSERT_INTO_TABLE_ERROR);
    }

    public ResultSetMetaData getDescribedData(Query query, Connection connection) {
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(query.asSql());
            return statement.getMetaData();
        } catch (SQLException e) {
            throwException(Constants.DESCRIBE_TABLE_ERROR,e);
        }

        return null;
    }

    @Override
    public boolean updateTable(Connection connection, Query updateQuery) {
        executeStatement(connection,updateQuery.asSql(),Constants.UPDATE_TABLE_ERROR);
        return true;
    }
}
