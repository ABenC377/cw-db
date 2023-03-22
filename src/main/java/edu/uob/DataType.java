package edu.uob;

public enum DataType {
    STRING("string"),
    BOOLEAN("boolean"),
    INTEGER("integer"),
    FLOAT("float"),
    NULL("null");
    
    private final String string;
    
    DataType(String name) {
        string = name;
    }
    
    @Override
    public String toString() {
        return string;
    }
}
