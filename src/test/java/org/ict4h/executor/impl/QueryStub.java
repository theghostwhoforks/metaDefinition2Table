package org.ict4h.executor.impl;

import org.ict4h.model.query.Query;

public class QueryStub implements Query {
    private final String statement;

    public QueryStub(String statement) {
        this.statement = statement;
    }

    @Override
    public String asSql() {
        return statement;
    }
}
