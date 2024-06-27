import java.io.IOException;

public class VMTranslator{
    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            System.out.println("there is not input files");
            return;
        }
        String file = args[0].substring(0, args[0].length() - 3);
        file = file + ".asm";
        String inputFileName = args[0];
        CodeWriter codeWriter = new CodeWriter(file);
        Parser parser = new Parser(inputFileName);
        while (parser.hasMoreLines()) {
            if (parser.commandType().equals("C_ARITHMETICS")) {
                codeWriter.writeArithmetic(parser.arg1());
            } else if (parser.commandType().equals("C_PUSH") || parser.commandType().equals("C_POP")) {
                codeWriter.writePushPop(parser.commandType(), parser.arg1(), parser.arg2());
            }
            parser.advance();
        }
        if (parser.commandType().equals("C_ARITHMETIC")) {
            codeWriter.writeArithmetic(parser.arg1());
        } else if (parser.commandType().equals("C_PUSH") || parser.commandType().equals("C_POP")) {
            codeWriter.writePushPop(parser.commandType(), parser.arg1(), parser.arg2());
        }
        codeWriter.close();
    }
}

