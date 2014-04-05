package constant;

import java.util.Arrays;
import java.util.List;

public class Constants {
    public final static String CREATE_TABLE_ERROR = "There was an error while creating a table.";
    public static final String INSERT_INTO_TABLE_ERROR = "There was an error while inserting into a table.";
    public static final String UPDATE_TABLE_ERROR = "There was an error while updating the table.";
    public static final String UPDATE_ENTITY_ERROR = "There was an error while updating the Entity.";
    public static final List<String> RESERVED_KEYWORDS = Arrays.asList("start","end","formhub","meta");
    public static final String SECTION = "section";
    public static final String DESCRIBE_TABLE_ERROR = "There was an error while getting details of the table";
    public static final String DELIMITER = ",";
    public static final String ENTITY_ID = "entity_id";
    public static final String REFERENCED_FIELD_ID = "parent_id";
    public static final String ID_SUFFIX = "_id";
}