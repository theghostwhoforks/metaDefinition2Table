package org.ict4h.formdefinition.constant;

import java.util.Arrays;
import java.util.List;

public class Constants {
    public final static String CREATE_TABLE_ERROR = "There was an error while creating a table.";
    public static final String INSERT_INTO_TABLE_ERROR = "There was an error while inserting into a table.";
    public static final String UPDATE_TABLE_ERROR = "There was an error while updating the table.";
    public static final List<String> RESERVED_KEYWORDS = Arrays.asList("start","end","formhub","meta");
    public static final String SECTION = "section";
    public static final String DESCRIBE_TABLE_ERROR = "There was an error while getting details of the table";
    public static final String SELECT_DATA_FROM_TABLE_ERROR = "There was an error while getting data from the table";
    public static final String DELIMITER = ",";
    public static final String ID_SUFFIX = ".id";
    public static final String DELETE_ENTITY_ERROR = "There was an error while deleting an entity";

    public static final String ID = "id";
    public static final String ENTITY_ID = "entity_id";
    public static final String MODIFIED_AT = "modified_at";
    public static final String MODIFIED_BY = "modified_by";
}