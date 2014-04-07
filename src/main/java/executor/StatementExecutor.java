package executor;

import model.query.Query;
import model.query.SelectQuery;

import java.sql.Connection;
import java.sql.ResultSetMetaData;

public interface StatementExecutor {
    public boolean createTable(Query query, Connection connection);
    public int insertIntoTable(Query query, Connection connection);

    ResultSetMetaData getDescribedData(SelectQuery query, Connection connection);

    public boolean updateTable(Connection connection, Query updateQuery);

    wrapper.ResultSetWrapper selectDataFromTable(Connection connection, Query query);
}
