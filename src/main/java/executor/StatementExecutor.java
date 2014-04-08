package executor;

import javafx.util.Pair;
import model.query.Query;
import model.query.SelectQuery;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.function.Function;

public interface StatementExecutor {
    public boolean createTable(Query query, Connection connection);
    public int insertIntoTable(Query query, Connection connection);

    Pair<String, ResultSetMetaData> getDescribedData(SelectQuery query, Connection connection);

    public boolean updateTable(Connection connection, Query updateQuery);

    <T> T selectDataFromTable(Connection connection, Query query,Function<ResultSet,T> function);

    boolean deleteEntity(Connection connection, Query query);
}

