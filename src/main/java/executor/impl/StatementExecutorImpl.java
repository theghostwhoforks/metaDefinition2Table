package executor.impl;

import constant.Messages;
import exception.MetaDataServiceRuntimeException;
import executor.StatementExecutor;
import model.Query;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class StatementExecutorImpl implements StatementExecutor {
    private static Logger logger = Logger.getLogger(StatementExecutor.class);

    @Override
    public boolean createTable(Query query, Connection connection) {
        String sql = query.asSql();
        logger.info(String.format("Creating table. Query - %s",sql));

        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            return statement.execute();
        } catch (SQLException e) {
            logger.error(String.format("There was an error while creating a table. Offending query  -%s",sql));
            throw new MetaDataServiceRuntimeException(Messages.CREATE_TABLE_ERROR,e);
        }
    }
}
