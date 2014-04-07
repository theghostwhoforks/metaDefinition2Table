package model.query;

import java.util.List;

public class FormTableQueryMultiMap {
    private Query tableQuery;
    private List<Query> linkedTableQueries;

    public FormTableQueryMultiMap(Query tableQuery, List<Query> linkedTableQueries) {
        this.tableQuery = tableQuery;
        this.linkedTableQueries = linkedTableQueries;
    }

    public Query getTableQuery() {
        return tableQuery;
    }

    public List<Query> getLinkedTableQueries() {
        return linkedTableQueries;
    }
}
