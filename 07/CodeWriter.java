import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;

public class CodeWriter {
    BufferedWriter writer;
    File output;
    int labelC = 0;
    /**
     * Opens an output file / stream and gets ready to write into it
     */
    public CodeWriter(String fileName) throws IOException {
        output = new File(fileName);
        FileWriter t = new FileWriter(output);
        writer = new BufferedWriter(t);
    }

    /**
     * Writes into the output file the assembly code that implements the given arithmetic-logical command
     */
    public void writeArithmetic(String command) throws IOException {
        if (Objects.equals(command, "add")) {
                writer.write("@SP\n" +
                             "AM=M-1\n" +
                             "D=M\n" +
                             "A=A-1\n" +
                             "M=M+D\n");
        }else if (Objects.equals(command, "sub")) {
                writer.write("@SP\n" +
                             "AM=M-1\n" +
                             "D=M\n" +
                             "A=A-1\n" +
                             "M=M-D\n");
        }else if (Objects.equals(command, "neg")) {
                writer.write("@SP\n" +
                             "A=M-1\n" +
                             "M=-M\n");
        } else if (Objects.equals(command, "eq")) {
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
        } else if (Objects.equals(command, "gt")) {
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
    } else if (Objects.equals(command, "lt")) {
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
    }else if (Objects.equals(command, "and")) {
                writer.write("@SP\n" +
                             "AM=M-1\n" +
                             "D=M\n" +
                             "A=A-1\n" +
                             "M=M&D\n");
        } else if (Objects.equals(command, "or")) {
                writer.write("@SP\n" +
                             "AM=M-1\n" +
                             "D=M\n" +
                             "A=A-1\n" +
                             "M=M|D\n");
        } else if (Objects.equals(command, "not")) {
                writer.write("@SP\n" +
                             "A=M-1\n" +
                             "M=!M\n");
            }
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
                writer.write("@" + index + "\n" +
                             "D=A\n" + "@" + 16 + "\n" +
                             "A=D+A\n" +
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
                writer.write("@" + index + "\n" +
                             "D=A\n" + "@" + 16 + "\n" +
                             "D=D+A\n" +
                             "@SP\n" +
                             "M=M-1\n" +
                             "@R13\n" +
                             "M=D\n" +
                             "@SP\n" +
                             "A=M\n" +
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
