package org.ict4h.formdefinition.executor.impl;

import org.ict4h.formdefinition.model.query.Query;

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
