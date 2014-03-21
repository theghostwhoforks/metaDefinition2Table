package service.impl;

import builder.QueryBuilder;
import executor.StatementExecutor;
import model.Query;
import service.SqlService;

import java.sql.Connection;

public class SqlServiceImpl implements SqlService {

    private final StatementExecutor executor;

    public SqlServiceImpl(StatementExecutor executor){
        this.executor = executor;
    }

    @Override
    public boolean createTable(Connection connection, String data) {
        Query query = QueryBuilder.formDefinition(data).build();
        return executor.createTable(query,connection);
    }
}
