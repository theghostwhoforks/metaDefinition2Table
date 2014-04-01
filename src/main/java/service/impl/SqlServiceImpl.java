package service.impl;

import builder.EntityQueryBuilder;
import builder.TableQueryBuilder;
import exception.MetaDataServiceRuntimeException;
import executor.StatementExecutor;
import executor.impl.StatementExecutorImpl;
import model.Query;
import service.SqlService;
import java.sql.Connection;
import java.util.List;

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

        try {
            List<Query> queries = TableQueryBuilder.with().formDefinition(data).create();
            for (Query query : queries) executor.createTable(query, connection);
        }catch (MetaDataServiceRuntimeException ex){
            logger.error("Error creating table(s) for form with data - {}",data);
            throw ex;
        }
        return true;
    }

    @Override
    public int createEntity(Connection connection, String data) {
        logger.info(String.format("Inserting into Table. Data supplied - %s", data));
        Query query = EntityQueryBuilder.with().formDefinition(data).create();
        logger.info("Inserting into Table. Query - {}", query.asSql());
        return executor.insertIntoTable(query, connection);
    }
}
