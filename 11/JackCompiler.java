import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
/**
 * The compiler takes in a file or directory as input and processes each .jack file found.
 * It creates a tokenizer and corresponding output .vm file for each input file.
 * The CompilationEngine, SymbolTable, and VMWriter modules are used to generate the final output file.
 */
public class JackCompiler {
    public static void main(String[] args) throws FileNotFoundException {
        String fileName = args[0];
        File inputFile = new File(fileName);
        String outputPathCompilation;
        File outputFile;
        ArrayList<File> jackFiles = new ArrayList<>();
        if (inputFile.isFile()) {
            //if it is a single file, see whether it is a jack file
            jackFiles.add(inputFile);
        } else if (inputFile.isDirectory()) {
            //if it is a directory get all jack files under this directory
            File[] allFiles = inputFile.listFiles();
            for (File file:allFiles) {
                if (file.getName().endsWith(".jack")) {
                    jackFiles.add(file);
                }
            }
        }
        for (File file: jackFiles) {
            outputPathCompilation = file.getAbsolutePath().substring(0, file.getAbsolutePath().lastIndexOf(".")) + ".vm";
            outputFile = new File(outputPathCompilation);
            CompilationEngine compilationEngine = new CompilationEngine(file,outputFile);
            compilationEngine.compileClass();
        }
    }
}