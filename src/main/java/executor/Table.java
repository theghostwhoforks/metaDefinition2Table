package executor;

import model.Query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Table {

    public boolean create(Connection connection, Query query) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(query.asSql());
        return statement.execute();
    }
}
