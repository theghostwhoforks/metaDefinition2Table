package executor;

import model.query.Query;

import java.sql.Connection;
import java.sql.ResultSetMetaData;

public interface StatementExecutor {
    public boolean createTable(Query query, Connection connection);
    public int insertIntoTable(Query query, Connection connection);

    ResultSetMetaData getDescribedData(Query query, Connection connection);

    public boolean updateTable(Connection connection, Query updateQuery);
}
