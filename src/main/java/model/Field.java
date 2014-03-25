package model;

import constant.Constants;

public class Field {


    private String name;
    private String value;

    protected Field(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() { return name; }
    public String getValue() { return value; }

    public boolean hasValue() {
        return value != null;
    }


    public boolean isNotReservedKeyword(){
        return !Constants.RESERVED_KEYWORDS.contains(name.toLowerCase());
    }
}
