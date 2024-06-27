import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;
/**
 * creates a new file and prepares it for writingThe VM Writer module generates code in the VM language.
 * It has functions to write VM commands such as arithmetic and memory operations, branching and function call commands.
 * It also keeps track of the current function and its local variables, and generates appropriate VM commands accordingly.
 */
public class VMWriter {
    public enum SEGMENT {CONST,ARG,LOCAL,STATIC,THIS,THAT,POINTER,TEMP,NONE};
    public enum OPERATION {ADD,SUB,NEG,EQ,GT,LT,AND,OR,NOT};
    private static HashMap<SEGMENT,String> segment = new HashMap<SEGMENT, String>();
    private static HashMap<OPERATION,String> operation = new HashMap<OPERATION, String>();
    private PrintWriter printWriter;

    /**
     * Opens a new output file and prepares it for writing VM commands.
     */
    public VMWriter(File fOut) throws FileNotFoundException {
        segment.put(SEGMENT.CONST,"constant");
        segment.put(SEGMENT.ARG,"argument");
        segment.put(SEGMENT.LOCAL,"local");
        segment.put(SEGMENT.STATIC,"static");
        segment.put(SEGMENT.THIS,"this");
        segment.put(SEGMENT.THAT,"that");
        segment.put(SEGMENT.POINTER,"pointer");
        segment.put(SEGMENT.TEMP,"temp");

        operation.put(OPERATION.ADD,"add");
        operation.put(OPERATION.SUB,"sub");
        operation.put(OPERATION.NEG,"neg");
        operation.put(OPERATION.EQ,"eq");
        operation.put(OPERATION.GT,"gt");
        operation.put(OPERATION.LT,"lt");
        operation.put(OPERATION.AND,"and");
        operation.put(OPERATION.OR,"or");
        operation.put(OPERATION.NOT,"not");
            printWriter = new PrintWriter(fOut);

    }

    /**
     * gets the printWriter of this VMWriter
     */
    public PrintWriter getPrintWriter() {
        return printWriter;
    }

    /**
     * writes a VM command for pushing a value onto the stack.
     */
    public void writePush(SEGMENT segment, int index){
        String stringIndex = String.valueOf(index);
        printWriter.print("push " + VMWriter.segment.get(segment) + " " + stringIndex + "\n");
    }

    /**
     * writes a VM command for popping a value onto the stack.
     */
    public void writePop(SEGMENT segment, int index){
        String stringIndex = String.valueOf(index);
        printWriter.print("pop " + VMWriter.segment.get(segment) + " " + stringIndex + "\n");
    }

    /**
     * writes a VM arithmetic command for preforming an arithmetic operations.
     */
    public void writeArithmetic(OPERATION opp){
        printWriter.print(operation.get(opp) + "\n");
    }

    /**
     * writes a VM command for labels.
     */
    public void writeLabel(String label){
        printWriter.print("label " + label + "\n");
    }

    /**
     * writes a VM command for goto.
     */
    public void writeGoto(String label){
        printWriter.print("goto " + label + "\n");
    }
    /**
     * writes a VM command for if-goto.
     */
    public void writeIf(String label){
        printWriter.print("if-goto " + label + "\n");
    }

    /**
     * writes a VM command that calls a subroutine.
     */
    public void writeCall(String name, int nArgs){
        String stringArgs = String.valueOf(nArgs);
        printWriter.print("call " + name + " " + stringArgs + "\n");
    }

    /**
     * writes a VM command that writes the subroutine.
     */
    public void writeFunction(String name, int nLocals){
        String stringLocals = String.valueOf(nLocals);
        printWriter.print("function " + name + " " + stringLocals + "\n");
    }

    /**
     * writes a VM command for returning a value.
     */
    public void writeReturn(){
        printWriter.print("return" + "\n");
    }

    public void writeCommand(String command, String arg1, String arg2){

        printWriter.print(command + " " + arg1 + " " + arg2 + "\n");

    }

    /**
     * Generates the appropriate assembly code for the given type of memory segment.
     */
    public SEGMENT getSegment(Symbol.KIND kind){
        if (kind == Symbol.KIND.FIELD) {
            return VMWriter.SEGMENT.THIS;
        } else if (kind == Symbol.KIND.STATIC) {
            return VMWriter.SEGMENT.STATIC;
        } else if (kind == Symbol.KIND.VAR) {
            return VMWriter.SEGMENT.LOCAL;
        } else if (kind == Symbol.KIND.ARG) {
            return VMWriter.SEGMENT.ARG;
        }
        return VMWriter.SEGMENT.NONE;
    }

    /**
     * close the output file
     */
    public void close(){
        printWriter.close();
    }
}