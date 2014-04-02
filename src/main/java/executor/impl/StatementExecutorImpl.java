package executor.impl;

import constant.Constants;
import exception.MetaDataServiceRuntimeException;
import executor.StatementExecutor;
import model.query.Query;
import org.apache.commons.dbutils.DbUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
        try{
            statement = executeStatement(connection, sqlStatement, message);
        }finally {
            try{
                DbUtils.close(statement);
            } catch (SQLException e) {
                throwException("Failed to close statement", e);
            }
        }
        return true;
    }

    private int executeQueryReturningInsertedId(Connection connection, String sqlStatement, String message) {
        PreparedStatement statement = executeStatement(connection, sqlStatement, message);
        try {
            ResultSet keys = statement.getGeneratedKeys();
            int id = -1;
            if (keys.next()) id = keys.getInt(1);
            return id;
        } catch (SQLException e) {
            return throwException(message, e);
        }finally {
            try
            {
                DbUtils.close(statement);
            }catch (SQLException sqlException){
                throwException("Failed to close statement",sqlException);
            }
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
}
