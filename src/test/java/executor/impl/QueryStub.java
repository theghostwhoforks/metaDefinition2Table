package executor.impl;

import model.query.Query;

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
