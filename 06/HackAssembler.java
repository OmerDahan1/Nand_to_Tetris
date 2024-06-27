import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class HackAssembler {
    public static void main(String[] args) throws IOException {
        //Parser parser = new Parser(args[0]);
        SymbolTable symbolTable = new SymbolTable();
        firstPass(args[0], symbolTable);
        secondPass(args[0], symbolTable, 16);
    }


    public static void firstPass(String fileName, SymbolTable symbolOfTable) throws IOException{

        Parser par_file = new Parser(fileName);
        int numberOfLine = 0;
        while(par_file.hasMoreLines()){
                if(par_file.instructionType().equals("Label")){
                    String symbol = par_file.symbol();
                    if(!symbolOfTable.contains(symbol)) {
                        symbolOfTable.addEntry(symbol, numberOfLine);
                    }
                    numberOfLine--;
                }
                numberOfLine++;
                par_file.advance();

        }
    }

    public static void secondPass(String fileName, SymbolTable symbolOfTable, int counter) throws IOException {
        String file = fileName.substring(0, fileName.indexOf(".") + 1);
        file = file + "hack";
        File output = new File(file);

        FileWriter t = new FileWriter(output);
        BufferedWriter buffered = new BufferedWriter(t);
        

        String C_instruction;
        String destOrCompOrJump = "";
        Parser par_file = new Parser(fileName);
        while (par_file.hasMoreLines()) {
            if (par_file.instructionType().equals("A_Instruction")) {
               // boolean number = true;
                String symbol = par_file.symbol();
              /*  for (int i = symbol.length() - 1; 0 < i; i--) {
                    if ('0' <= symbol.charAt(i) && symbol.charAt(i) <= '9') {
                        num += symbol.charAt(i) - '0';
                        num *= 10;
                    } else {
                        number = false;
                    }

                }
                */
                if (Character.isDigit(par_file.symbol().charAt(0))) {
                    buffered.write(Integer.toBinaryString(0x10000 | Integer.parseInt(par_file.symbol())).substring(1));
                    buffered.newLine();
                } else {
                    if (symbolOfTable.contains(symbol)) {
                        buffered.write(Integer.toBinaryString(0x10000 | symbolOfTable.getAddress(symbol)).substring(1));
                        buffered.newLine();
                    } else {
                        symbolOfTable.addEntry(symbol, counter);
                        buffered.write(Integer.toBinaryString(0x10000 | symbolOfTable.getAddress(symbol)).substring(1));
                        buffered.newLine();
                        counter++;
                    }
                }
            } else if (par_file.instructionType().equals("C_Instruction")) {
                C_instruction = "111";
                /*char AorM = '0';
                if (par_file.dest().contains("M")) {
                    AorM = '1';
                }
                //C_instruction += AorM;*/

                if (par_file.comp() != "" && par_file.jump() != "")
                C_instruction += Code.comp(par_file.comp()) + Code.dest(par_file.dest()) + Code.jump(par_file.jump());
                else if (par_file.jump() != "")
                C_instruction += Code.comp(par_file.comp()) + "000" + Code.jump(par_file.jump());
                else
                C_instruction += Code.comp(par_file.comp()) + Code.dest(par_file.dest()) + "000";
                buffered.write(C_instruction);
                buffered.newLine();
                C_instruction = "";
                destOrCompOrJump = "";
            }
        par_file.advance();
        }
        buffered.close();
    }
}

