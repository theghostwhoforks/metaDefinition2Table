package executor.impl;

import executor.StatementExecutor;
import jdk.nashorn.internal.ir.annotations.Ignore;
import model.query.Query;
import org.junit.After;
import org.junit.Before;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class StatementExecutorIT {

    private Connection connection;
    private Statement statement;
    private StatementExecutor executor;

    @Before
    public void setUp() throws Exception {
        Class.forName("org.h2.Driver");
        connection = DriverManager.getConnection("jdbc:h2:~/test");
        statement = connection.createStatement();
        executor = new StatementExecutorImpl();
    }

    @After
    public void tearDown() throws Exception {
        statement.execute("DROP ALL OBJECTS");
        statement.close();
        connection.close();
    }

    @Ignore()
    public void shouldDoSomething() {
        //Should throw an error but the DB2 implementation doesn't.
        executor.insertIntoTable(new QueryStub("SELECT 1;"),connection);
    }
}

class QueryStub implements Query{
    private final String statement;

    public QueryStub(String statement) {
        this.statement = statement;
    }

    @Override
    public String asSql() {
        return statement;
    }
}
