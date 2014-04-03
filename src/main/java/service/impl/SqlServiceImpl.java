package service.impl;

import builder.SelectQueryBuilder;
import builder.EntityQueryBuilder;
import builder.TableQueryBuilder;
import builder.UpdateQueryBuilder;
import exception.MetaDataServiceRuntimeException;
import executor.StatementExecutor;
import executor.impl.StatementExecutorImpl;
import model.query.Query;
import model.query.TableCreateQuery;
import service.SqlService;

import java.sql.Connection;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
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
        Query query = entityQueryBuilder.createEntity();
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

    @Override
    public boolean updateTable(Connection connection, String data) {
        Query query = SelectQueryBuilder.with().formDefinition(data).createDescribeQuery();
        ResultSetMetaData describedData = executor.getDescribedData(query, connection);
        List<String> columns = new ArrayList();
        int columnCount = 0;
        try {
            columnCount = describedData.getColumnCount();
        } catch (SQLException e) {
            throw new MetaDataServiceRuntimeException("could not get the column count",e);
        }
        for (int i = 1; i <= columnCount; i++) {
            try {
                columns.add(describedData.getColumnName(i));
            } catch (SQLException e) {
                throw new MetaDataServiceRuntimeException("could not get the column name for this index : " + i,e);
            }
        }
        UpdateQueryBuilder updateQueryBuilder = UpdateQueryBuilder.with().formDefinition(data);
        if (updateQueryBuilder.isRequired(columnCount)){
            Query updateQuery = updateQueryBuilder.update(columns);
            executor.updateTable(connection,updateQuery);
        }
        return createEntity(connection,data);
    }
}
