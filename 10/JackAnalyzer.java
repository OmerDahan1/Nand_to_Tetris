import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
/**
 *The JackAnalyzer program takes in a source file or directory and uses a JackTokenizer to tokenize the input file(s).
 * It then creates an output file with the same name but with a .xml extension and uses a CompilationEngine
 * to translate the tokenized input into XML code,which is written to the output file.
 *  The program can handle both individual files and directories containing multiple .jack files.
 */
public class JackAnalyzer {
    public static void main(String[] args) throws FileNotFoundException {
            String fileName = args[0];
            File inputFile = new File(fileName);
            String outputPathCompilation, outputPathTokenizer;
            File outputFile,outputToken;
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
                outputPathCompilation = file.getAbsolutePath().substring(0, file.getAbsolutePath().lastIndexOf(".")) + ".xml";
                outputPathTokenizer = file.getAbsolutePath().substring(0, file.getAbsolutePath().lastIndexOf(".")) + "T.xml";
                outputFile = new File(outputPathCompilation);
                outputToken = new File(outputPathTokenizer);
                CompilationEngine compilationEngine = new CompilationEngine(file,outputFile,outputToken);
                compilationEngine.compileClass();
            }
    }
}