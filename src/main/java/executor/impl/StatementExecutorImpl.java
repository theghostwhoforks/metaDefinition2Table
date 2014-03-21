package executor.impl;

import constant.Messages;
import exception.MetaDataServiceRuntimeException;
import executor.StatementExecutor;
import model.Query;
import org.apache.log4j.BasicConfigurator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class StatementExecutorImpl implements StatementExecutor {
    private static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(StatementExecutor.class);

    @Override
    public boolean createTable(Query query, Connection connection) {
        BasicConfigurator.configure();
        String sql = query.asSql();
        logger.info(String.format("Creating table. Query - %s", sql));
        return executeQuery(connection, sql, Messages.CREATE_TABLE_ERROR);
    }

    private boolean executeQuery(Connection connection, String sql,String message) {
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            return statement.execute();
        } catch (SQLException e) {
            logger.error(message);
            throw new MetaDataServiceRuntimeException(message,e);
        }
    }

    @Override
    public boolean insertIntoTable(Query query, Connection connection) {
        BasicConfigurator.configure();
        String sql = query.asSql();
        logger.info(String.format("Inserting into table. Query - %s", sql));
        return executeQuery(connection, sql,Messages.INSERT_INTO_TABLE_ERROR);
    }
}
