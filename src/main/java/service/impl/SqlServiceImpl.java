package service.impl;

import builder.*;
import exception.MetaDataServiceRuntimeException;
import executor.StatementExecutor;
import executor.impl.StatementExecutorImpl;
import model.Field;
import model.Form;
import model.SubForm;
import model.query.CreateIndependentQuery;
import model.query.FormTableQueryMultiMap;
import model.query.Query;
import model.query.SelectQuery;
import service.SqlService;
import sql.ResultSetExtensions;

import java.sql.Connection;
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
            FormTableQueryMultiMap<CreateIndependentQuery> createTable = TableQueryBuilder.with().formDefinition(data).create();
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
    public int createEntity(Connection connection, String data, String modifiedByUser) {
        logger.info(String.format("Inserting into Table. Data supplied - %s", data));
        EntityQueryBuilder entityQueryBuilder = EntityQueryBuilder.with().formDefinition(data).modifiedByUser(modifiedByUser);
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
        return foreignKey;
    }

    @Override
    public int updateEntity(Connection connection, String data, int id, String modifiedByUser) {
        Query query = DeleteQueryBuilder.with().formDefinition(data).DeleteEntity(id);
        executor.deleteEntity(connection, query);
        return createEntity(connection, data, modifiedByUser);
    }

    @Override
    public boolean updateTable(Connection connection, String data) {
        List<SelectQuery> queries = SelectQueryBuilder.with().formDefinition(data).createDescribeQuery();
        Map<String, Set<String>> allColumns = new HashMap();
        queries.stream()
                .map(query -> executor.getDescribedData(query, connection))
                .forEach(pair -> allColumns.put(pair.getKey(), ResultSetExtensions.getColumnNames(pair.getValue())));
        List<Query> updateQuery = UpdateQueryBuilder.with().formDefinition(data).update(allColumns);
        updateQuery.stream().filter(query -> !query.asSql().isEmpty()).forEach(query -> executor.updateTable(connection, query));
        return true;
    }

    @Override
    public Form selectEntity(Connection connection, int id, String formDefinition) {
        FormTableQueryMultiMap<SelectQuery> selectQueries = SelectQueryBuilder.with().formDefinition(formDefinition).createSelectQueriesFor(id);

        SelectQuery tableQuery = selectQueries.getTableQuery();
        List<Field> fields = executor.selectDataFromTable(connection, tableQuery, x -> ResultSetExtensions.getParentTableFields(x));

        List<SubForm> subForms = new ArrayList();
        for (SelectQuery query : selectQueries.getLinkedTableQueries()) {
            List<Map<String, String>> instances = executor.selectDataFromTable(connection, query, x -> ResultSetExtensions.tableAsAnInstance(x));
            subForms.add(new SubForm(query.getTableName(), instances));
        }
        return new Form(tableQuery.getTableName(), fields, subForms);
    }
}