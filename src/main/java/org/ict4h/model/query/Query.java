package org.ict4h.model.query;

import org.ict4h.model.Field;

import java.util.function.Predicate;

public interface Query {
    String asSql();

    default Predicate<Field> filterKeywordsAndSections() {
        return ((Predicate<Field>) f -> f.isNotReservedKeyword()).and(f -> f.isNotSection());
    }
}
