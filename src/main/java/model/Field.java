package model;

import constant.Constants;

public class Field {


    private String name;
    private String value;

    public Field(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() { return name; }
    public String getValue() { return value; }

    public boolean hasValue() {
        return value != null;
    }


    public Boolean isNotReservedKeyword(){
        return !Constants.RESERVED_KEYWORDS.contains(name.toLowerCase());
    }

    public Boolean isNotSection(){
        return !name.toLowerCase().startsWith(Constants.SECTION);
    }
}
