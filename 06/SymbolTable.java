import java.util.*;

public class SymbolTable {

    private HashMap<String, Integer> symbol_Table;// stores the symbol table

    /**
     * Constructs a SymbolTable
     */
    public SymbolTable() {
        symbol_Table = new HashMap<String, Integer>();

        /** Constructs The predefined */
        for (int i = 0; i <= 15; i++) {
            // We want the Register to remain the same for all of the time
            String key = "R" + i;
            symbol_Table.put(key, i);
        }
        symbol_Table.put("SCREEN", 16384);
        symbol_Table.put("KBD", 24576);
        symbol_Table.put("SP", 0);
        symbol_Table.put("LCL", 1);
        symbol_Table.put("ARG", 2);
        symbol_Table.put("THIS", 3);
        symbol_Table.put("THAT", 4);
    }

    /**
     * Adds <symbol, address> to the table
     */
    public void addEntry(String symbol, int address) {
        symbol_Table.put(symbol, address);
    }

    /**
     * Checks <symbol, address> is in the table
     */
    public boolean contains(String symbol) {
        return symbol_Table.containsKey(symbol);
    }
    // Returns the address (int) associated with a symbol
    public int getAddress(String symbol) {
        return symbol_Table.get(symbol);
    }

    public void printTable(){
        System.out.println(this.symbol_Table);
    }
}
