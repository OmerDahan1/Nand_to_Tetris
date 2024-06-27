import java.util.HashMap;

/**
 * The symbol table module manages the scope of identifiers in the Jack language
 * by assigning them a running index and associating them with properties such as type and kind.
 * It supports two nested scopes (class and subroutine) and can handle static, field, argument, and var identifiers.
 * It is used for error-free compilation and does not keep subroutine or class names (class/subroutine).
 */
public class SymbolTable {

    private HashMap<String,Symbol> classScope;//for STATIC, FIELD
    private HashMap<String,Symbol> subroutineScope;//for ARG, VAR
    private HashMap<Symbol.KIND,Integer> indices;

    /**
     * initializes an empty symbol table with all indices reset.
     */
    public SymbolTable() {
        classScope = new HashMap<>();
        subroutineScope = new HashMap<>();
        indices = new HashMap<>();
        indices.put(Symbol.KIND.ARG,0);
        indices.put(Symbol.KIND.FIELD,0);
        indices.put(Symbol.KIND.STATIC,0);
        indices.put(Symbol.KIND.VAR,0);
    }

    /**
     Begins a new subroutine scope and clears all previous subroutine symbols.
     */
    public void startSubroutine(){
        subroutineScope.clear();
        indices.put(Symbol.KIND.VAR,0);
        indices.put(Symbol.KIND.ARG,0);
    }

    /**
     * Assigns a new identifier of the given name, type, and kind and assigns it a running index.
     * Identifiers with "STATIC" and "FIELD" kinds have a class scope,
     * while "ARG" and "VAR" identifiers have a subroutine scope.
     */
    public void define(String name, String type, Symbol.KIND kind){
        Symbol symbol;
        if (kind == Symbol.KIND.ARG || kind == Symbol.KIND.VAR){
            int index = indices.get(kind);
            symbol = new Symbol(type,kind,index);
            indices.put(kind, index + 1);
            subroutineScope.put(name, symbol);
        }else if(kind == Symbol.KIND.STATIC || kind == Symbol.KIND.FIELD){
            int index = indices.get(kind);
            symbol = new Symbol(type, kind, index);
            indices.put(kind, index + 1);
            classScope.put(name, symbol);
        }
    }

    /**
     * Returns the number of variables of the given kind that already defined in the current scope.
     */
    public int varCount(Symbol.KIND kind){
        return indices.get(kind);
    }

    /**
     * Checks if a symbol with the given name exists in the current scope
     * and then determines the category of the specified identifier in the current scope,
     * if the identifier is not found in the current scope it returns "NONE".
     */
    public Symbol.KIND kindOf(String name){
        Symbol symbol;
        if (classScope.get(name) != null){
            symbol = classScope.get(name);
        }else if (subroutineScope.get(name) != null){
            symbol = subroutineScope.get(name);
        }else {
            return null;
        }
        if (symbol != null) {
            return symbol.getKind();
        }
        return Symbol.KIND.NONE;
    }

    /**
     * Checks if a symbol with the given name exists in the current scope
     * and if it is, returns the type of the given identifier in the current scope.
     */
    public String typeOf(String name){
        Symbol symbol;
        if (classScope.get(name) != null){
            symbol = classScope.get(name);
        }else if (subroutineScope.get(name) != null){
            symbol = subroutineScope.get(name);
        }else {
            symbol = null;
        }
        if (symbol != null){
            return symbol.getType();
        }
        return "";
    }

    /**
     * Checks if a symbol with the given name exists in the current scope
     * and if it is, returns the assigned index for a named identifier
     */
    public int indexOf(String name){
        Symbol symbol;
        if (classScope.get(name) != null){
            symbol = classScope.get(name);
        }else if (subroutineScope.get(name) != null){
            symbol = subroutineScope.get(name);
        }else {
            symbol = null;
        }
        if (symbol != null){
            return symbol.getIndex();
        }
        return -1;
    }
}