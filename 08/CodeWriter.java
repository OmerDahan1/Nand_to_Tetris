import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class CodeWriter {
    BufferedWriter writer;
    File output;
    int labelC = 0;
    int callC = 0;
    String filename;


    /**
     * Opens an output file / stream and gets ready to write into it
     */
    public CodeWriter(String fileName) throws IOException {
        output = new File(fileName);
        FileWriter t = new FileWriter(output);
        writer = new BufferedWriter(t);
    }

    public void writeInit() throws IOException {
        // Set the SP register to the top of the stack
        writer.write("@256\n" +
                "D=A\n" +
                "@SP\n" +
                "M=D\n");
        // Call the Sys.init function
        writeCall("Sys.init", 0);
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    /**
     * Writes into the output file the assembly code that implements the given arithmetic-logical command
     */
    public void writeArithmetic(String command) throws IOException {
        if (command.equals("add")) {
            writer.write("@SP\n" +
                    "AM=M-1\n" +
                    "D=M\n" +
                    "A=A-1\n" +
                    "M=M+D\n");
        }else if (command.equals("sub")) {
            writer.write("@SP\n" +
                    "AM=M-1\n" +
                    "D=M\n" +
                    "A=A-1\n" +
                    "M=M-D\n");
        }else if (command.equals("neg")) {
            writer.write("@SP\n" +
                    "A=M-1\n" +
                    "M=-M\n");
        } else if (command.equals("eq")) {
            String label1 = "LABEL" + labelC++;
            String label2 = "LABEL" + labelC++;
            writer.write("@SP\n" +
                    "AM=M-1\n" +
                    "D=M\n" +
                    "A=A-1\n" +
                    "D=M-D\n" +
                    "@" + label1 + "\n" +
                    "D;JEQ\n" +
                    "@SP\n" +
                    "A=M-1\n" +
                    "M=0\n" +
                    "@" + label2 + "\n" +
                    "0;JMP\n" +
                    "(" + label1 + ")\n" +
                    "@SP\n" +
                    "A=M-1\n" +
                    "M=-1\n" +
                    "(" + label2 + ")\n");
        } else if (command.equals("gt")) {
            String label1 = "LABEL" + labelC++;
            String label2 = "LABEL" + labelC++;
            writer.write("@SP\n" +
                    "AM=M-1\n" +
                    "D=M\n" +
                    "A=A-1\n" +
                    "D=M-D\n" +
                    "@" + label1 + "\n" +
                    "D;JGT\n" +
                    "@SP\n" +
                    "A=M-1\n" +
                    "M=0\n" +
                    "@" + label2 + "\n" +
                    "0;JMP\n" +
                    "(" + label1 + ")\n" +
                    "@SP\n" +
                    "A=M-1\n" +
                    "M=-1\n" +
                    "(" + label2 + ")\n");
        } else if (command.equals("lt")) {
            String label1 = "LABEL" + labelC++;
            String label2 = "LABEL" + labelC++;
            writer.write("@SP\n" +
                    "AM=M-1\n" +
                    "D=M\n" +
                    "A=A-1\n" +
                    "D=M-D\n" +
                    "@" + label1 + "\n" +
                    "D;JLT\n" +
                    "@SP\n" +
                    "A=M-1\n" +
                    "M=0\n" +
                    "@" + label2 + "\n" +
                    "0;JMP\n" +
                    "(" + label1 + ")\n" +
                    "@SP\n" +
                    "A=M-1\n" +
                    "M=-1\n" +
                    "(" + label2 + ")\n");
        }else if (command.equals("and")) {
            writer.write("@SP\n" +
                    "AM=M-1\n" +
                    "D=M\n" +
                    "A=A-1\n" +
                    "M=M&D\n");
        } else if (command.equals("or")) {
            writer.write("@SP\n" +
                    "AM=M-1\n" +
                    "D=M\n" +
                    "A=A-1\n" +
                    "M=M|D\n");
        } else if (command.equals("not")) {
            writer.write("@SP\n" +
                    "A=M-1\n" +
                    "M=!M\n");
        }
    }

    /**
     * Writes into the output file the assembly code that implements the given label command
     */
    public void writeLabel(String strLabel) throws IOException {
        writer.write("(" + strLabel + ")\n");
    }

    /**
     * Writes into the output file the assembly code that implements the given goto command
     */
    public void writeGoto(String strGoto) throws IOException {
        writer.write("@" + strGoto + "\n");
        writer.write("0;JMP\n");
    }

    /**
     * Writes into the output file the assembly code that implements the given if-goto command
     */
    public void writeIf(String strIf) throws IOException {
        writer.write("@SP\n");
        writer.write("AM=M-1\n");
        writer.write("D=M\n");
        writer.write("@" + strIf + "\n");
        writer.write("D;JNE\n");
    }


    /**
     * Writes into the output file the assembly code that implements the given call-to-function command
     */


    public void writeCall(String functionName, int numArgs) throws IOException {
        // Create a unique label for the return address
        String returnLabel = functionName + "$ret." + callC;

        // Push the return address label onto the stack
        writer.write("@" + returnLabel + "\n");
        writer.write("D=A\n");
        writer.write("@SP\n");
        writer.write("A=M\n");
        writer.write("M=D\n");
        writer.write("@SP\n");
        writer.write("M=M+1\n");

        // Push the current values of the LCL, ARG, THIS, and THAT segments onto the stack
        writer.write("@LCL\n");
        writer.write("D=M\n");
        writer.write("@SP\n");
        writer.write("A=M\n");
        writer.write("M=D\n");
        writer.write("@SP\n");
        writer.write("M=M+1\n");

        writer.write("@ARG\n");
        writer.write("D=M\n");
        writer.write("@SP\n");
        writer.write("A=M\n");
        writer.write("M=D\n");
        writer.write("@SP\n");
        writer.write("M=M+1\n");

        writer.write("@THIS\n");
        writer.write("D=M\n");
        writer.write("@SP\n");
        writer.write("A=M\n");
        writer.write("M=D\n");
        writer.write("@SP\n");
        writer.write("M=M+1\n");

        writer.write("@THAT\n");
        writer.write("D=M\n");
        writer.write("@SP\n");
        writer.write("A=M\n");
        writer.write("M=D\n");
        writer.write("@SP\n");
        writer.write("M=M+1\n");

        // Reposition the ARG segment to point to the first argument of the new function
        writer.write("@5\n");
        writer.write("D=A\n");
        writer.write("@" + numArgs + "\n");
        writer.write("D=D+A\n");
        writer.write("@SP\n");
        writer.write("D=M-D\n");
        writer.write("@ARG\n");
        writer.write("M=D\n");

        // Reposition the LCL segment to point to the top of the stack
        writer.write("@SP\n");
        writer.write("D=M\n");
        writer.write("@LCL\n");
        writer.write("M=D\n");

        // Jump to the function being called
        writer.write("@" + functionName + "\n");
        writer.write("0;JMP\n");
        // Define the return address label
        this.writeLabel(returnLabel);
        callC = callC + 1;
    }

    /**
     * Writes into the output file the assembly code that implements the given function command
     */
    public void writeFunction(String strFunc, int nVars) throws IOException {
        writer.write("(" + strFunc + ")\n");
        for (int i = 0; i < nVars; i++) {
            writePushPop("C_PUSH", "constant", 0);
        }
    }

    /**
     * Writes into the output file the assembly code that implements the return command
     */
    public void writeReturn() throws IOException {
        // this command is Storing the return address in a temporary var and returning it later to the caller
        writer.write("@LCL\n" +
                "D=M\n" +
                "@endFrame\n" +
                "M=D\n" +
                "D=M\n" +
                "@5\n" +
                "A=D-A\n" +
                "D=M\n" +
                "@retAddress\n" +
                "M=D\n");
        //this command is Popping the return value and put it in the return register
        writer.write("@SP\n" +
                "M=M-1\n" +
                "A=M\n" +
                "D=M\n" +
                "@ARG\n" +
                "A=M\n" +
                "M=D\n");
        //this commands is Restoring the caller's SP, LCL, ARG, THIS, and THAT
        writer.write("@ARG\n" +
                "D=M+1\n" +
                "@SP\n" +
                "M=D\n");
        writer.write("@endFrame\n" +
                "D=M\n" +
                "A=D-1\n" +
                "D=M\n" +
                "@THAT\n" +
                "M=D\n");
        writer.write("@endFrame\n" +
                "D=M\n" +
                "@2\n" +
                "A=D-A\n" +
                "D=M\n" +
                "@THIS\n" +
                "M=D\n");
        writer.write("@endFrame\n" +
                "D=M\n" +
                "@3\n" +
                "A=D-A\n" +
                "D=M\n" +
                "@ARG\n" +
                "M=D\n");
        writer.write("@endFrame\n" +
                "D=M\n" +
                "@4\n" +
                "A=D-A\n" +
                "D=M\n" +
                "@LCL\n" +
                "M=D\n");
        //this command is Jumping to the return address
        writer.write("@retAddress\n" +
                "A=M\n" +
                "0;JMP\n");
    }


    /**
     * Writes into the output file the assembly code that implements the given push or pop command
     */

    public void writePushPop(String command, String segment, int index) throws IOException {
        if (command.equals("C_PUSH")) {
            if (segment.equals("constant")) {
                //writer.write("//" + "RAM[SP] = i\n");
                writer.write("@" + index + "\n" +
                        "D=A\n" +
                        "@SP\n" +
                        "A=M\n" +
                        "M=D\n" +
                        "@SP\n" +
                        "M=M+1\n");
            } else if (segment.equals("local")) {
                //writer.write("//" + "RAM[SP] = RAM[LCL+i]\n");
                writer.write("@LCL" + "\n" +
                        "D=M\n" +
                        "@" + index + "\n" +
                        "A=D+A\n" +
                        "D=M\n" +
                        "@SP\n" +
                        "A=M\n" +
                        "M=D\n" +
                        "@SP\n" +
                        "M=M+1\n");

            } else if (segment.equals("argument")) {
                //writer.write("//" + "RAM[SP] = RAM[ARG+i]\n");
                writer.write("@ARG" + "\n" +
                        "D=M\n" +
                        "@" + index + "\n" +
                        "A=D+A\n" +
                        "D=M\n" +
                        "@SP\n" +
                        "A=M\n" +
                        "M=D\n" +
                        "@SP\n" +
                        "M=M+1\n");

            } else if (segment.equals("this")) {
                //writer.write("//" + "RAM[SP] = RAM[" + segment.toUpperCase() + "+i]\n");
                writer.write("@THIS" + "\n" +
                        "D=M\n" +
                        "@" + index + "\n" +
                        "A=D+A\n" +
                        "D=M\n" +
                        "@SP\n" +
                        "A=M\n" +
                        "M=D\n" +
                        "@SP\n" +
                        "M=M+1\n");

            } else if (segment.equals("that")) {
                //writer.write("//" + "RAM[SP] = RAM[" + segment.toUpperCase() + "+i]\n");
                writer.write("@THAT" + "\n" +
                        "D=M\n" +
                        "@" + index + "\n" +
                        "A=D+A\n" +
                        "D=M\n" +
                        "@SP\n" +
                        "A=M\n" +
                        "M=D\n" +
                        "@SP\n" +
                        "M=M+1\n");

            }
            else if (segment.equals("static")) {
                //writer.write("//" + "RAM[SP] = RAM[static+i]\n");
                writer.write("@" + filename + "." + index + "\n" +
                        "D=M\n" +
                        "@SP\n" +
                        "A=M\n" +
                        "M=D\n" +
                        "@SP\n" +
                        "M=M+1\n");
            } else if (segment.equals("temp")) {
                //writer.write("//" + "RAM[SP] = RAM[5+i]\n");
                writer.write("@5" + "\n" +
                        "D=A\n" +
                        "@" + index + "\n" +
                        "A=D+A\n" +
                        "D=M\n" +
                        "@SP\n" +
                        "A=M\n" +
                        "M=D\n" +
                        "@SP\n" +
                        "M=M+1\n");
            }else if (segment.equals("pointer")) {
                //writer.write("//" + "RAM[SP] = RAM[3+i]\n");
                writer.write("@3" + "\n" +
                        "D=A\n" +
                        "@" + index + "\n" +
                        "A=D+A\n" +
                        "D=M\n" +
                        "@SP\n" +
                        "A=M\n" +
                        "M=D\n" +
                        "@SP\n" +
                        "M=M+1\n");
            }
        }
        else if (command.equals("C_POP")) {

            if (segment.equals("local")) {
                //writer.write("//" + "RAM[LCL+i] = RAM[SP]\n");
                writer.write("@LCL" + "\n" +
                        "D=M\n" +
                        "@" + index + "\n" +
                        "D=D+A\n" +
                        "@R13\n" +
                        "M=D\n" +
                        "@SP\n" +
                        "AM=M-1\n" +
                        "D=M\n" +
                        "@R13\n" +
                        "A=M\n" +
                        "M=D\n");

            } else if (segment.equals("argument")) {
                //writer.write("//" + "RAM[ARG+i] = RAM[SP]\n");
                writer.write("@ARG" + "\n" +
                        "D=M\n" +
                        "@" + index + "\n" +
                        "D=D+A\n" +
                        "@R13\n" +
                        "M=D\n" +
                        "@SP\n" +
                        "AM=M-1\n" +
                        "D=M\n" +
                        "@R13\n" +
                        "A=M\n" +
                        "M=D\n");

            } else if (segment.equals("this")) {
                //writer.write("//" + "RAM[" + segment.toUpperCase() + "+i] = RAM[SP]\n");
                writer.write("@THIS" + "\n" +
                        "D=M\n" +
                        "@" + index + "\n" +
                        "D=D+A\n" +
                        "@R13\n" +
                        "M=D\n" +
                        "@SP\n" +
                        "AM=M-1\n" +
                        "D=M\n" +
                        "@R13\n" +
                        "A=M\n" +
                        "M=D\n");
            }else if (segment.equals("that")) {
                //writer.write("//" + "RAM[" + segment.toUpperCase() + "+i] = RAM[SP]\n");
                writer.write("@THAT" + "\n" +
                        "D=M\n" +
                        "@" + index + "\n" +
                        "D=D+A\n" +
                        "@R13\n" +
                        "M=D\n" +
                        "@SP\n" +
                        "AM=M-1\n" +
                        "D=M\n" +
                        "@R13\n" +
                        "A=M\n" +
                        "M=D\n");
            }else if (segment.equals("static")) {
                //writer.write("//" + "RAM[static+i] = RAM[SP]\n");
                writer.write("@" + filename + "." + index + "\n" +
                        "D=A\n" +
                        "@R13\n" +
                        "M=D\n" +
                        "@SP\n" +
                        "AM=M-1\n" +
                        "D=M\n" +
                        "@R13\n" +
                        "A=M\n" +
                        "M=D\n");
            }else if (segment.equals("temp")){
                //writer.write("//" + "RAM[5+i] = RAM[SP]\n");
                writer.write("@5" + "\n" +
                        "D=A\n" +
                        "@" + index + "\n" +
                        "D=D+A\n" +
                        "@R13\n" +
                        "M=D\n" +
                        "@SP\n" +
                        "AM=M-1\n" +
                        "D=M\n" +
                        "@R13\n" +
                        "A=M\n" +
                        "M=D\n");
            }else if (segment.equals("pointer")){
                //writer.write("//" + "RAM[3+i] = RAM[SP]\n");
                writer.write("@3" + "\n" +
                        "D=A\n" +
                        "@" + index + "\n" +
                        "D=D+A\n" +
                        "@R13\n" +
                        "M=D\n" +
                        "@SP\n" +
                        "AM=M-1\n" +
                        "D=M\n" +
                        "@R13\n" +
                        "A=M\n" +
                        "M=D\n");
            }
        }
    }



    /**
     * Close the output file
     */
    public void close() throws IOException {
        writer.write("(END)" + "\n");
        writer.write("@END" + "\n");
        writer.write("0;JMP");
        writer.close();
    }
}