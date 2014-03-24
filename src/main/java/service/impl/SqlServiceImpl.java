package service.impl;

import builder.QueryBuilder;
import executor.StatementExecutor;
import executor.impl.StatementExecutorImpl;
import model.Query;
import service.SqlService;
import java.sql.Connection;

public class SqlServiceImpl implements SqlService {
    private static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(SqlService.class);
    private final StatementExecutor executor;

    public SqlServiceImpl(StatementExecutor executor) {
        this.executor = executor;
    }

    public SqlServiceImpl() {
        executor = new StatementExecutorImpl();
    }

    @Override
    public boolean createTable(Connection connection, String data) {
        logger.info("Creating Table. Data supplied - {}",data);
        Query query = QueryBuilder.with().formDefinition(data).createTable();
        return executor.createTable(query, connection);
    }

    @Override
    public boolean createEntity(Connection connection, String data) {
        logger.info(String.format("Inserting into Table. Data supplied - %s", data));
        Query query = QueryBuilder.with().formDefinition(data).createEntity();
        logger.info("Inserting into Table. Query - {}", query.asSql());
        return executor.insertIntoTable(query, connection);
    }
}
