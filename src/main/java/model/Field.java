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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Field)) return false;

        Field field = (Field) o;

        if (!name.equals(field.name)) return false;
        if (value != null ? !value.equals(field.value) : field.value != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }
}
