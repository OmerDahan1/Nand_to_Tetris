import java.util.*;
public class Code {
    public static String dest(String dest){
        if(dest.equals("M")) return "001";
        else if(dest.equals("D")) return "010";
        else if(dest.equals("MD")) return "011";
        else if(dest.equals("A")) return "100";
        else if(dest.equals("AM")) return "101";
        else if(dest.equals("AD")) return "110";
        else if(dest.equals("AMD")) return "111";
        else return "000";

    }
    public static String comp(String comp){
        if(comp.equals("0")) return "0101010";
        else if(comp.equals("1")) return "0111111";
        else if(comp.equals("-1")) return "0111010";
        else if(comp.equals("D")) return "0001100";
        else if(comp.equals("A")) return "0110000";
        else if(comp.equals("M")) return "1110000";
        else if(comp.equals("!D")) return "0001101";
        else if(comp.equals("!A")) return "0110001";
        else if(comp.equals("!M")) return "1110001";
        else if(comp.equals("-D")) return "0001111";
        else if(comp.equals("-A")) return "0110011";
        else if(comp.equals("-M")) return "1110011";
        else if(comp.equals("D+1")) return "0011111";
        else if(comp.equals("A+1")) return "0110111";
        else if(comp.equals("M+1")) return "1110111";
        else if(comp.equals("D-1")) return "0001110";
        else if(comp.equals("A-1")) return "0110010";
        else if(comp.equals("M-1")) return "1110010";
        else if(comp.equals("D+A")) return "0000010";
        else if(comp.equals("D+M")) return "1000010";
        else if(comp.equals("D-A")) return "0010011";
        else if(comp.equals("D-M")) return "1010011";
        else if(comp.equals("A-D")) return "0000111";
        else if(comp.equals("M-D")) return "1000111";
        else if(comp.equals("D&A")) return "0000000";
        else if(comp.equals("D&M")) return "1000000";
        else if(comp.equals("D|A")) return "0010101";
        else if(comp.equals("D|M")) return "1010101";
        else{
            throw new Error(String.format("Invalid comp value \"%s\" found", comp));
        }
    }


    public static String jump(String jump){
        if (jump.equals("JGT")) return "001";
        else if (jump.equals("JEQ")) return "010";
        else if (jump.equals("JGE")) return "011";
        else if (jump.equals("JLT")) return "100";
        else if (jump.equals("JNE")) return "101";
        else if (jump.equals("JLE")) return "110";
        else if (jump.equals("JMP")) return "111";
        else return "000";
    }
}
