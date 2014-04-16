package org.ict4h.formdefinition.sql;

import org.ict4h.formdefinition.constant.Constants;
import org.ict4h.formdefinition.exception.MetaDataServiceRuntimeException;
import org.ict4h.formdefinition.model.Field;
import org.ict4h.formdefinition.model.FormMetadata;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class ResultSetExtensions {


    public static List<FormMetadata> mapToListOfFormMetadata(ResultSet resultSet, String formName){
        List<FormMetadata> metaDataList = new ArrayList<>();
        try{
            while (resultSet.next()){
                int instanceId = resultSet.getInt(Constants.ID);
                String createdAt = resultSet.getString(Constants.MODIFIED_AT);
                String createdBy = resultSet.getString(Constants.MODIFIED_BY);
                String entityId = resultSet.getString(Constants.ENTITY_ID);
                metaDataList.add(new FormMetadata(formName, instanceId, entityId, createdBy, createdAt));
            }
        } catch (SQLException e) {
            throw new MetaDataServiceRuntimeException("Error reading result set to peek at form metadata", e);
        }
        return metaDataList;
    }

    public static List<Field> getParentTableFields(ResultSet resultSet) {
        try {
            if(resultSet.next()){
                return getColumnNames(resultSet.getMetaData()).stream().map(x -> {
                    try {
                        return new Field(x, resultSet.getString(x));
                    } catch (SQLException e) {
                        throw new MetaDataServiceRuntimeException("",e);
                    }
                }).collect(Collectors.toList());
            }
            else{
                return null;
            }
        } catch (SQLException e) {
            throw new MetaDataServiceRuntimeException("could not get the data from tables", e);
        }
    }

    public static List<Map<String, String>> tableAsAnInstance(ResultSet resultSet) {
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

    public static Set<String> getColumnNames(ResultSetMetaData metaData) {
        Set<String> fields = new HashSet();
        int columnCount;
        try {
            columnCount = metaData.getColumnCount();
            for (int i = 1; i <= columnCount; i++)
                fields.add(metaData.getColumnName(i).toUpperCase());
        } catch (SQLException e) {
            throw new MetaDataServiceRuntimeException("could not get the data from tables", e);
        }
        return fields;
    }
}
