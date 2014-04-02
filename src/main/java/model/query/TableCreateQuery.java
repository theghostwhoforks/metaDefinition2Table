package model.query;

import java.util.List;
import java.util.stream.Stream;

public class TableCreateQuery {
    private SimpleQuery tableQuery;
    private List<SimpleQuery> linkedTableQueries;

    public TableCreateQuery(SimpleQuery tableQuery, List<SimpleQuery> linkedTableQueries) {
        this.tableQuery = tableQuery;
        this.linkedTableQueries = linkedTableQueries;
    }

    public SimpleQuery getTableQuery() {
        return tableQuery;
    }

    public Stream<SimpleQuery> getLinkedTableQueries() {
        return linkedTableQueries.stream();
    }
}
