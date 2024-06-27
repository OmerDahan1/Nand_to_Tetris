/**
 * The Symbol class represents a symbol in a symbol table.
 * Each symbol has a type, a kind, and an index.
 * The type represents the data type of the symbol, such as "int" or "String".
 * The kind represents the category of the symbol, such as "static", "field", "arg", or "var".
 * The index represents the position of the symbol in its category.
 * The class contains getters for each of these fields, as well as a toString method for debugging purposes.
 * The class can be used to store and retrieve information about a symbol in a symbol table.
 */
public class Symbol {

    public enum KIND {STATIC, FIELD, ARG, VAR, NONE};

    private String type;
    private KIND kind;
    private int index;

    public Symbol(String type, KIND kind, int index) {
        this.type = type;
        this.kind = kind;
        this.index = index;
    }

    public String getType() {
        return type;
    }

    public KIND getKind() {
        return kind;
    }

    public int getIndex() {
        return index;
    }

    public String toString() {
        return "Symbol{" +
                "type='" + type + '\'' +
                ", kind=" + kind +
                ", index=" + index +
                '}';
    }
}