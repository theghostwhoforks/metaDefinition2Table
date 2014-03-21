package executor.impl;

import exception.MetaDataServiceRuntimeException;
import executor.StatementExecutor;
import model.Query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class StatementExecutorImpl implements StatementExecutor {

    @Override
    public boolean createTable(Query query, Connection connection) {
        try {
            PreparedStatement statement = connection.prepareStatement(query.asSql());
            return statement.execute();
        } catch (SQLException e) {
            throw new MetaDataServiceRuntimeException("There was an error while creating a table.",e);
        }
    }
}
