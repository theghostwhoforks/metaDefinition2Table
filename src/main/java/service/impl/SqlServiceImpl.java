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
import model.SubForm;
import model.query.CreateIndependentQuery;
import model.query.FormTableQueryMultiMap;
import model.query.Query;
import model.query.SelectQuery;
import service.SqlService;
import sql.ResultSetExtensions;

import java.sql.Connection;
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
        List<SelectQuery> queries = SelectQueryBuilder.with().formDefinition(data).createDescribeQuery();

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

    @Override
    public Form getDataFor(Connection connection, int id, String formName, String... subFormNames) {
        FormTableQueryMultiMap<SelectQuery> selectQueries = SelectQueryBuilder.with().createSelectQueriesFor(id, formName, Arrays.asList(subFormNames));

        SelectQuery tableQuery = selectQueries.getTableQuery();
        List<Field> fields = executor.selectDataFromTable(connection, tableQuery, x -> ResultSetExtensions.getParentTableFields(x));

        List<SubForm> subForms = new ArrayList();
        for (SelectQuery query : selectQueries.getLinkedTableQueries()){
            List<Map<String, String>> instances = executor.selectDataFromTable(connection, query, x -> ResultSetExtensions.tableAsAnInstance(x));
            subForms.add(new SubForm(query.getTableName(), instances));
        }
        return new ParentForm(tableQuery.getTableName(),fields,subForms);
    }


    private Set<String> getColumnNames(ResultSetMetaData resultSet) throws SQLException {
        Set<String> fields = new HashSet();
        int columnCount = resultSet.getColumnCount();
        for (int i = 1; i <= columnCount; i++)
            fields.add(resultSet.getColumnName(i));
        return fields;
    }
}
