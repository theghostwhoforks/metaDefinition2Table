package service.impl;

import builder.QueryBuilder;
import executor.StatementExecutor;
import model.Query;
import org.apache.log4j.BasicConfigurator;
import service.SqlService;

import java.sql.Connection;

public class SqlServiceImpl implements SqlService {
    private static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(SqlService.class);
    private final StatementExecutor executor;

    public SqlServiceImpl(StatementExecutor executor) {
        this.executor = executor;
    }

    @Override
    public boolean createTable(Connection connection, String data) {
        logger.info("Creating Table. Data supplied - {}",data);
        Query query = QueryBuilder.with().formDefinition(data).build();
        return executor.createTable(query, connection);
    }

    public boolean insertIntoATable(Connection connection, String data) {
        BasicConfigurator.configure();
        logger.info(String.format("Inserting into Table. Data supplied - %s", data));
        Query query = QueryBuilder.with().insert(data);
        return executor.insertIntoTable(query, connection);
    }
}
