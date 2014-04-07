package service.impl;

import builder.EntityQueryBuilder;
import builder.SelectQueryBuilder;
import builder.TableQueryBuilder;
import builder.UpdateQueryBuilder;
import exception.MetaDataServiceRuntimeException;
import executor.StatementExecutor;
import executor.impl.StatementExecutorImpl;
import model.query.FormTableCreateQueryMultiMap;
import model.query.Query;
import service.SqlService;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

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
        logger.info("Creating Table. Data supplied - {}", data);

        try {
            FormTableCreateQueryMultiMap createTable = TableQueryBuilder.with().formDefinition(data).create();
            executor.createTable(createTable.getTableQuery(), connection);
            createTable.getLinkedTableQueries().forEach(query -> {
                executor.createTable(query, connection);
            });
        } catch (MetaDataServiceRuntimeException ex) {
            logger.error("Error creating table(s) for form with data - {}", data);
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
        } catch (MetaDataServiceRuntimeException ex) {
            logger.error("Error inserting into table(s) for form with data - {}", data);
            throw ex;
        }
        return true;
    }

    @Override
    public boolean updateTable(Connection connection, String data) {
        List<Query> queries = SelectQueryBuilder.with().formDefinition(data).createDescribeQuery();

        Map<String, Set<String>> allColumns = new HashMap();

        queries.stream().map(query -> executor.getDescribedData(query, connection))
                .forEach(resultSet -> {
                    Set<String> fields = new HashSet();
                    try {
                        int columnCount = resultSet.getColumnCount();
                        for (int i = 1; i <= columnCount; i++)
                            fields.add(resultSet.getColumnName(i));
                        allColumns.put(resultSet.getTableName(1), fields);
                    } catch (SQLException e2) {
                        throw new MetaDataServiceRuntimeException("could not get the data from tables", e2);
                    }
                });
        List<Query> updateQuery = UpdateQueryBuilder.with().formDefinition(data).update(allColumns);
        updateQuery.stream().forEach(query -> executor.updateTable(connection, query));
        return true;
    }
}
