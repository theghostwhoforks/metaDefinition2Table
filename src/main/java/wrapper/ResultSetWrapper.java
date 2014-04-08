package wrapper;

import exception.MetaDataServiceRuntimeException;
import model.Field;
import org.slf4j.Logger;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class ResultSetWrapper {

    private Logger logger = org.slf4j.LoggerFactory.getLogger(ResultSetWrapper.class);
    private ResultSet resultSet;

    public ResultSetWrapper(ResultSet resultSet) {
        this.resultSet = resultSet;
    }

    private Set<String> getColumnNames(ResultSetMetaData resultSet) throws SQLException {
        Set<String> fields = new HashSet();
        int columnCount = resultSet.getColumnCount();
        for (int i = 1; i <= columnCount; i++)
            fields.add(resultSet.getColumnName(i));
        return fields;
    }

    public List<Field> getParentTableFields() {
        try {
            if(resultSet.next()){
                return getFields();
            }
            else{
                return null;
            }
        } catch (SQLException e) {
            throw new MetaDataServiceRuntimeException("could not get the data from tables", e);
        }
    }

    private List<Field> getFields() throws SQLException {
        return getColumnNames(resultSet.getMetaData()).stream().map(x -> {
            try {
                return new Field(x, resultSet.getString(x));
            } catch (SQLException e) {
                logger.error("Could not find column {}", x);
                throw new MetaDataServiceRuntimeException("",e);
            }
        }).collect(Collectors.toList());
    }

    public List<Map<String, String>> tableAsAnInstance() {
        List<Map<String, String>> instances = new ArrayList<>();
        try {
            while (resultSet.next()) {
                Map<String, String> instance = new HashMap<>();
                for (String s : getColumnNames(resultSet.getMetaData())) {
                    instance.put(s,resultSet.getString(s));
                }
                instances.add(instance);
            }
        } catch (SQLException ex) {
            throw new MetaDataServiceRuntimeException("could not get the data from nested tables", ex);
        }
        return instances;
    }
}