package service.impl;

import builder.EntityQueryBuilder;
import builder.TableQueryBuilder;
import exception.MetaDataServiceRuntimeException;
import executor.StatementExecutor;
import executor.impl.StatementExecutorImpl;
import model.query.Query;
import model.query.SimpleQuery;
import model.query.TableCreateQuery;
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
            TableCreateQuery createTable = TableQueryBuilder.with().formDefinition(data).create();
            executor.createTable(createTable.getTableQuery(),connection);
            createTable.getLinkedTableQueries().forEach(query -> {
                 executor.createTable(query,connection);
            });
        }catch (MetaDataServiceRuntimeException ex){
            logger.error("Error creating table(s) for form with data - {}",data);
            throw ex;
        }
        return true;
    }

    @Override
    public boolean createEntity(Connection connection, String data) {
        logger.info(String.format("Inserting into Table. Data supplied - %s", data));
        EntityQueryBuilder entityQueryBuilder = EntityQueryBuilder.with().formDefinition(data);
        SimpleQuery query = entityQueryBuilder.createEntity();
        logger.info("Inserting into Table. Query - {}", query.asSql());
        int foreignKey = executor.insertIntoTable(query, connection);

        try {
            List<Query> queries = entityQueryBuilder.createSubEntities(foreignKey);
            for (Query insertQuery : queries) executor.insertIntoTable(insertQuery, connection);
        }
        catch (MetaDataServiceRuntimeException ex) {
            logger.error("Error inserting into table(s) for form with data - {}",data);
            throw ex;
        }
        return true;
    }
}
