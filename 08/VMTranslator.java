import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class VMTranslator{
    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            System.out.println("there is not input files");
            return;
        }
        File inputFile = new File(args[0]);
        String outputName = inputFile.getName();
        String outputPath = inputFile.getAbsolutePath();
        if (!inputFile.exists()) {
            throw new IOException("Input file or directory does not exist");
        }if (inputFile.isDirectory()) {
            outputPath = outputPath + "/" + outputName + ".asm";
        }else if (outputPath.lastIndexOf(".") == -1) {
            outputPath = outputPath + ".asm";
        }else {
            outputPath = outputPath.substring(0, outputPath.length() - 2);
            outputPath = outputPath + "asm";
        }
        CodeWriter codeWriter = new CodeWriter(outputPath);
        if (inputFile.isDirectory()) {
            File[] filesInDir = inputFile.listFiles();
            ArrayList<File> files = new ArrayList<File>();

            for (File file1 : filesInDir) {
                if (file1.getName().endsWith(".vm")) {
                    files.add(file1);
                }
            }
                if (files.size() == 1) {
                    for (File file2 : files) {
                        codeWriter.setFilename(file2.getName());
                        Parser parser = new Parser(file2.getAbsolutePath());
                        loopOnFile(codeWriter, parser);
                    }
                } else {
                    codeWriter.writeInit();

                    for (File file2 : files) {
                        codeWriter.setFilename(file2.getName());
                        Parser parser = new Parser(file2.getAbsolutePath());
                        loopOnFile(codeWriter, parser);
                    }
                }
        }else{
            codeWriter.setFilename(inputFile.getName());
            Parser parser = new Parser(inputFile.getAbsolutePath());
            loopOnFile(codeWriter, parser);
        }
        codeWriter.close();
    }
    private static void loopOnFile(CodeWriter writer, Parser parser) throws IOException {
        while (parser.hasMoreLines()) {
            if (parser.commandType().equals("C_PUSH") || parser.commandType().equals("C_POP")) {
                writer.writePushPop(parser.commandType(), parser.arg1(), parser.arg2());
            } else if (parser.commandType().equals("C_CALL")) {
                writer.writeCall(parser.arg1(), parser.arg2());
            } else if (parser.commandType().equals("C_FUNCTION")) {
                writer.writeFunction(parser.arg1(), parser.arg2());
            } else if (parser.commandType().equals("C_LABEL")) {
                writer.writeLabel(parser.arg1());
            } else if (parser.commandType().equals("C_ARITHMETICS")) {
                writer.writeArithmetic(parser.arg1());
            } else if (parser.commandType().equals("C_IF")) {
                writer.writeIf(parser.arg1());
            } else if (parser.commandType().equals("C_GOTO")) {
                writer.writeGoto(parser.arg1());
            } else if (parser.commandType().equals("C_RETURN")) {
                writer.writeReturn();
            }
            parser.advance();
        }
        /*if (parser.commandType().equals("C_PUSH") || parser.commandType().equals("C_POP")) {
            writer.writePushPop(parser.commandType(), parser.arg1(), parser.arg2());
        } else if (parser.commandType().equals("C_CALL")) {
            writer.writeCall(parser.arg1(), parser.arg2());
        } else if (parser.commandType().equals("C_FUNCTION")) {
            writer.writeFunction(parser.arg1(), parser.arg2());
        } else if (parser.commandType().equals("C_LABEL")) {
            writer.writeLabel(parser.arg1());
        } else if (parser.commandType().equals("C_ARITHMETICS")) {
            writer.writeArithmetic(parser.arg1());
        } else if (parser.commandType().equals("C_IF")) {
            writer.writeIf(parser.arg1());
        } else if (parser.commandType().equals("C_GOTO")) {
            writer.writeGoto(parser.arg1());
        } else if (parser.commandType().equals("C_RETURN")) {
            writer.writeReturn();
        }

         */
    }
}