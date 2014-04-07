package service.impl;

import builder.EntityQueryBuilder;
import builder.SelectQueryBuilder;
import builder.TableQueryBuilder;
import builder.UpdateQueryBuilder;
import exception.MetaDataServiceRuntimeException;
import executor.StatementExecutor;
import executor.impl.StatementExecutorImpl;
import model.Field;
import model.Form;
import model.ParentForm;
import model.query.FormTableCreateQueryMultiMap;
import model.query.Query;
import service.SqlService;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
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
                    try {
                        Set<String> fields = getColumnNames(resultSet);
                        allColumns.put(resultSet.getTableName(1), fields);
                    } catch (SQLException e2) {
                        throw new MetaDataServiceRuntimeException("could not get the data from tables", e2);
                    }
                });
        List<Query> updateQuery = UpdateQueryBuilder.with().formDefinition(data).update(allColumns);
        updateQuery.stream().forEach(query -> executor.updateTable(connection, query));
        return true;
    }

    private Set<String> getColumnNames(ResultSetMetaData resultSet) throws SQLException {
        Set<String> fields = new HashSet();
        int columnCount = resultSet.getColumnCount();
        for (int i = 1; i <= columnCount; i++)
            fields.add(resultSet.getColumnName(i));
        return fields;
    }

    @Override
    public Form getDataFor(Connection connection, int id, String formName, List<String> subFormNames) {
        FormTableCreateQueryMultiMap selectQueries = SelectQueryBuilder.with().createSelectQueriesFor(id, formName, subFormNames);
        ResultSet resultSet = executor.selectDataFromTable(connection, selectQueries.getTableQuery());
        List<Field> fields = new ArrayList();
        try {
            if(!resultSet.next()){
                //TODO: Iterate better
                return null;
            }

            Set<String> columnNames = getColumnNames(resultSet.getMetaData());
            for (String columnName : columnNames) {
                String value = resultSet.getString(columnName);
                fields.add(new Field(columnName, value));
            }
        } catch (SQLException e) {
            throw new MetaDataServiceRuntimeException("could not get the data from tables", e);
        }
//        List<SubForm> subForms = selectQueries.getLinkedTableQueries().stream().map(query -> executor.selectDataFromTable(connection, query))
//                .map(resultSet -> {
//                    HashMap<String, String> instance = new HashMap<>();
//                    String tableName = null;
//                    try {
//                        ResultSetMetaData metaData = resultSet.getMetaData();
//                        Set<String> columnNames = getColumnNames(metaData);
//                        tableName = metaData.getTableName(1);
//                        for (String columnName : columnNames) {
//                            instance.put(columnName, resultSet.getString(columnName));
//                        }
//                    } catch (SQLException e) {
//                        e.printStackTrace();
//                    }
//                    List<Map<String, String>> instances = new ArrayList();
//                    instances.add(instance);
//                    return new SubForm(tableName, instances);
//                }).collect(Collectors.toList());
        return new ParentForm(fields,null);
    }
}
