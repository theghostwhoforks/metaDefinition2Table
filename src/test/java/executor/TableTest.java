package executor;

import model.Query;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TableTest {

    private Connection connection;
    private PreparedStatement statement;

    @Before
    public void setUp() {
        connection = mock(Connection.class);
        statement = mock(PreparedStatement.class);
    }

    @Test
    public void shouldCreateATable() throws SQLException {
        String sql = "CreateTableStatement";
        Query query = new Query(sql);
        when(connection.prepareStatement(query.asSql())).thenReturn(statement);
        new Table().create(connection, query);
        verify(statement).execute();
    }
}
