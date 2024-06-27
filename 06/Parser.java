
/**
 * Represents a parser.
 * <br> (Part of Homework 06 in the Nand2Tetris course, Efi Arazi School of CS)
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileReader;

public class Parser {

    private ArrayList<String> commandList;
    private int parser_loc;
    private String currentLine;
    private BufferedReader scan;

    /** Constructs a Parser, starting at a given line */
    public Parser(String theFile) throws IOException {
        parser_loc = 0;
        commandList = new ArrayList<>();

        try {
            File open = new File(theFile);
            scan = new BufferedReader(new FileReader(open));
            //while (hasMoreLines()) {
                //currentLine = scan.nextLine();
                advance();
                /*if (!currentLine.equals("") && !currentLine.startsWith("//")) { // Ignore all whitespace
                    String realValue = currentLine.replaceAll("\\s+", "");
                    int balancedVal = realValue.indexOf("//");

                    if (balancedVal != -1) {
                        realValue = realValue.substring(0, balancedVal);
                    }
                    if (!realValue.equals("")) {
                        commandList.add(realValue);
                    }
                }*/
            //}
        } catch (FileNotFoundException exce) {
            System.out.println("please insert a valid file");
        }

    }

    public String symbol() {
        if (this.instructionType().equals("Label")) {
            return currentLine.substring(currentLine.indexOf("(") + 1, currentLine.indexOf(")"));
        } else if (this.instructionType().equals("A_Instruction")) {
            return currentLine.trim().substring(1);
        }
        return "not A_Instruction or Label";
    }

    public String dest() {
        if (this.instructionType().equals("C_Instruction")) {
            if (currentLine.contains("=")) {
                return currentLine.substring(0, currentLine.indexOf('='));
            }
        }
        return "";
    }

    public String comp() {
        if (this.instructionType().equals("C_Instruction")) {
            String comp;
            if (currentLine.contains("=")) {
                comp = currentLine.substring(currentLine.indexOf("=") + 1);
                if (currentLine.contains(";")) {
                    comp = comp.substring(0, currentLine.indexOf(";"));
                }
                return comp;
            } else if (currentLine.contains(";")) {
                return currentLine.substring(0, currentLine.indexOf(';'));
            } else {
                return currentLine;
            }
        }
        return "";
    }

    public String jump() {
        if (this.instructionType().equals("C_Instruction")) {
            if (currentLine.contains(";")) {
                return currentLine.substring(currentLine.indexOf(";") + 1);
            }
        }
        return "";
    }

    /** Checks if there is more work to do */
    public boolean hasMoreLines() {
        return currentLine != null; // return scan.hasnextline
    }

    /** Gets the next instruction and makes it current instruction */
   /* public void advance() {
        if (!hasMoreLines())
            throw new Error("Already reached the end");
        parser_loc += 1; // Add 1 to the parser_loc count in order to advance to the next instruction
        if (parser_loc + 1 < commandList.size() && commandList.get(parser_loc + 1).startsWith("//"))
            this.advance();
    }*/
    public void advance() throws IOException {
        if((currentLine = scan.readLine()) != null) {
            currentLine = currentLine.trim();
            currentLine = currentLine.split(" ")[0];
            while(currentLine.trim().length() == 0 || currentLine.trim().charAt(0) == '/') {
                currentLine = scan.readLine().trim();
            }
        }
    }

    /** Returns the current instruction type */
    // We use class TypeOfCommand.java to build instructionType function.
    public String instructionType() {
        //String commandInList = commandList.get(parser_loc); // Get from the list by a variable parser_loc of type int.
        if (currentLine.contains("@"))
            return "A_Instruction"; // Return A command type
        else if (currentLine.contains("(") && currentLine.contains(")"))
            return "Label"; // Return L command Type
        else
            return "C_Instruction"; // Return C command type
    }
}