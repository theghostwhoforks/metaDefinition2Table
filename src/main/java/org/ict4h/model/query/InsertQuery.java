package org.ict4h.model.query;

import org.ict4h.constant.Constants;
import org.ict4h.model.Field;
import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class InsertQuery implements Query {
    public static final String INSERT_TEMPLATE = "INSERT INTO %s (%s) VALUES (%s);";
    private final String tableName;
    private final List<Field> fields;

    public InsertQuery(String tableName, Stream<Field> fields) {
        this.tableName = tableName;
        this.fields = fields.filter(filterKeywordsAndSections()).collect(Collectors.toList());
    }

    @Override
    public String asSql() {
        Function<? super Field, ? extends String> quoteValue = (x) -> String.format("'%s'",x.getValue().replace("'",""));

        Collector<Field, ?, SortedMap<String, String>> collector = Collectors.toMap(Field::getName, quoteValue, (x,y) -> x + ": " + y, TreeMap<String, String>::new);
        SortedMap<String, String> collect = fields.stream().collect(collector);

        String columns = StringUtils.join(collect.keySet().toArray(), Constants.DELIMITER);
        String values = StringUtils.join(collect.values().toArray(), Constants.DELIMITER);
        return String.format(INSERT_TEMPLATE,tableName, columns, values);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof InsertQuery)) return false;
        return asSql().equals(((InsertQuery) other).asSql());
    }

    @Override
    public int hashCode() {
        return asSql().hashCode();
    }

    @Override
    public String toString() {
        return asSql();
    }
}