package model;

public class Field {
    private String name;
    private String value;

    public String getName() {
        return name;
    }

    public String getValue() { return value; }

    public boolean hasValue() {
        return value != null;
    }
}
