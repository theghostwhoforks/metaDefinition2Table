package executor.impl;

import constant.Constants;
import exception.MetaDataServiceRuntimeException;
import executor.StatementExecutor;
import model.Query;

import java.sql.*;

public class StatementExecutorImpl implements StatementExecutor {
    private static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(StatementExecutor.class);

    @Override
    public boolean createTable(Query query, Connection connection) {
        String sql = query.asSql();
        logger.info(String.format("Creating table. Query - %s", sql));
        return executeQuery(connection, sql, Constants.CREATE_TABLE_ERROR);
    }

    private boolean executeQuery(Connection connection, String sql,String message) {
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            return statement.execute();
        } catch (SQLException e) {
            logger.error(message);
            logger.error(e.getMessage());
            throw new MetaDataServiceRuntimeException(message,e);
        }
    }

    private int executeQueryReturningInsertedId(Connection connection, String sql, String message) {
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.execute();
            ResultSet keys = statement.getGeneratedKeys();
            int id = -1;
            if(keys.next()){
                id = keys.getInt(1);
            }
            return id;
        } catch (SQLException e) {
            logger.error(message);
            logger.error(e.getMessage());
            throw new MetaDataServiceRuntimeException(message,e);
        }
    }

    @Override
    public int insertIntoTable(Query query, Connection connection) {
        String sql = query.asSql();
        logger.info(String.format("Inserting into table. Query - %s", sql));
        return executeQueryReturningInsertedId(connection, sql, Constants.INSERT_INTO_TABLE_ERROR);
    }
}
