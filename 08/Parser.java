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

    private String currentLine;
    private BufferedReader scan;


    /**
     * Constructs a Parser, starting at a given line
     */
    public Parser(String theFile) throws IOException {
        File open = new File(theFile);
        scan = new BufferedReader(new FileReader(open));
        advance();
    }

    /**
     * Checks if there is more lines in the file
     */
    public boolean hasMoreLines() {
        return currentLine != null;
    }

    /**
     * Gets the next instruction and makes it current instruction
     */
    public void advance() throws IOException {
        if ((currentLine = scan.readLine()) != null) {
            currentLine = currentLine.trim();
            while (currentLine.trim().length() == 0 || currentLine.trim().charAt(0) == '/') {
                currentLine = scan.readLine().trim();
            }
        }
    }

    /**
     * Returns the current command type
     */
    public String commandType() {
        currentLine = currentLine.split("//")[0];
        currentLine = currentLine.trim();
        if (currentLine != null) {
            if (currentLine.contains("push"))
                return "C_PUSH";
            else if (currentLine.contains("pop"))
                return "C_POP";
            else if (currentLine.contains("call"))
                return "C_CALL";
            else if (currentLine.contains("function"))
                return "C_FUNCTION";
            else if (currentLine.contains("add") || currentLine.contains("sub") || currentLine.contains("neg")
                     || currentLine.contains("eq") || currentLine.contains("gt") || currentLine.contains("lt")
                     || currentLine.contains("and") || currentLine.contains("or") || currentLine.contains("not"))
                return "C_ARITHMETICS";
            else if(currentLine.contains("label"))
                return "C_LABEL";
            else if (currentLine.contains("if"))
                return "C_IF";
            else if(currentLine.contains("goto"))
                return "C_GOTO";
            else if (currentLine.contains("return"))
                return "C_RETURN";
            else throw new IllegalStateException("The command type is invalid");
        }
        return "";
    }

    public String arg1(){
        if (!(currentLine.contains("return") || this.commandType().equals("C_ARITHMETICS"))){
            return currentLine.split(" ")[1];
        }else if (this.commandType().equals("C_ARITHMETICS")){
            return currentLine;
        }
        else throw new IllegalStateException("The command type called is invalid");
    }
    public int arg2(){
        String s;
        if (currentLine.contains("pop") || currentLine.contains("push")
            || currentLine.contains("function") || currentLine.contains("call")){
            s = currentLine.split(" ")[2];
            return Integer.parseInt(s);
        }
        else throw new IllegalStateException("The command type called is invalid");
    }
}