package org.ict4h.formdefinition.model.query;

import java.util.List;

public class FormTableQueryMultiMap<T> {
    private T tableQuery;
    private List<T> linkedTableQueries;

    public FormTableQueryMultiMap(T tableQuery, List<T> linkedTableQueries) {
        this.tableQuery = tableQuery;
        this.linkedTableQueries = linkedTableQueries;
    }

    public T getTableQuery() {
        return tableQuery;
    }

    public List<T> getLinkedTableQueries() {
        return linkedTableQueries;
    }
}
