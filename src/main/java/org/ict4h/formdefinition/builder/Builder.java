package org.ict4h.formdefinition.builder;

public interface Builder {
    default String subFormTableName(String formName, String subFormName) {
        return String.format("%s_%s", subFormName, formName);
    }
}
