package model;

public class Field {
    protected String name;
    protected String value;

    protected Field(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() { return name; }
    public String getValue() { return value; }

    public boolean hasValue() {
        return value != null;
    }
}
