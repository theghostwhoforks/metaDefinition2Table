package executor;

import model.query.Query;

import java.sql.Connection;

public interface StatementExecutor {
    public boolean createTable(Query query, Connection connection);
    public int insertIntoTable(Query query, Connection connection);
}
