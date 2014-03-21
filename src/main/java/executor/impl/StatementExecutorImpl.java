package executor.impl;

import constant.Messages;
import exception.MetaDataServiceRuntimeException;
import executor.StatementExecutor;
import model.Query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class StatementExecutorImpl implements StatementExecutor {
    private static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(StatementExecutor.class);

    @Override
    public boolean createTable(Query query, Connection connection) {
        String sql = query.asSql();
        logger.info("Creating table. Query - {}",sql);

        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            return statement.execute();
        } catch (SQLException e) {
            logger.error("There was an error while creating a table. Offending query - {}",sql);
            throw new MetaDataServiceRuntimeException(Messages.CREATE_TABLE_ERROR,e);
        }
    }
}
