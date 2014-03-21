package executor;

import model.Query;

import java.sql.Connection;

public interface StatementExecutor {
    public boolean createTable(Query query, Connection connection);
    public boolean insertIntoTable(Query query, Connection connection);
}