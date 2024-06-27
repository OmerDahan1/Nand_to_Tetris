import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The JackTokenizer is responsible for removing comments and white space from the input stream,
 * and breaking it into Jack-language tokens as specified by the Jack grammar.
 */
public class JackTokenizer {

    //constant for type
    public static int keyWord = 1;
    public static int symbol = 2;
    public static int identifier = 3;
    public static int intConst = 4;
    public static int stringConst = 5;

    //constant for keyword
    public static int CLASS = 10;
    public static int METHOD = 11;
    public static int FUNCTION = 12;
    public static int CONSTRUCTOR = 13;
    public static int INT = 14;
    public static int BOOLEAN = 15;
    public static int CHAR = 16;
    public static int VOID = 17;
    public static int VAR = 18;
    public static int STATIC = 19;
    public static int FIELD = 20;
    public static int LET = 21;
    public static int DO = 22;
    public static int IF = 23;
    public static int ELSE = 24;
    public static int WHILE = 25;
    public static int RETURN = 26;
    public static int TRUE = 27;
    public static int FALSE = 28;
    public static int NULL = 29;
    public static int THIS = 30;

    private String currentToken;
    private int currentTokenType;
    private int cursor = 0;
    private ArrayList<String> tokensList = new ArrayList<String>();

    private static Pattern tokenExpressions;
    private static String keyWordRegEx;
    private static String symbolRegEx;
    private static String intRegEx;
    private static String strRegEx;
    private static String idRegEx;

    private static HashMap<String, Integer> keyWords = new HashMap<>();
    private static HashSet<Character> operations = new HashSet<>();


    /**
     * Prepares the input file/stream for tokenization by opening it and getting ready to process it.
     */
    public JackTokenizer(File input) throws FileNotFoundException {
        Scanner scanner = new Scanner(input);
        StringBuilder filtered = new StringBuilder();
        String line;
        keyWords.put("class", CLASS);
        keyWords.put("constructor", CONSTRUCTOR);
        keyWords.put("function", FUNCTION);
        keyWords.put("method", METHOD);
        keyWords.put("field", FIELD);
        keyWords.put("static", STATIC);
        keyWords.put("var", VAR);
        keyWords.put("int", INT);
        keyWords.put("char", CHAR);
        keyWords.put("boolean", BOOLEAN);
        keyWords.put("void", VOID);
        keyWords.put("true", TRUE);
        keyWords.put("false", FALSE);
        keyWords.put("null", NULL);
        keyWords.put("this", THIS);
        keyWords.put("let", LET);
        keyWords.put("do", DO);
        keyWords.put("if", IF);
        keyWords.put("else", ELSE);
        keyWords.put("while", WHILE);
        keyWords.put("return", RETURN);

        operations.add('+');
        operations.add('-');
        operations.add('*');
        operations.add('/');
        operations.add('&');
        operations.add('|');
        operations.add('<');
        operations.add('>');
        operations.add('=');
        while (scanner.hasNext()) {
            line = noComments(scanner.nextLine()).trim();
            if (line.length() > 0) {
                filtered.append(line).append("\n");
            }
        }
        filtered = new StringBuilder(noBlockComments(filtered.toString()).trim());
        initRegs();
        Matcher match = tokenExpressions.matcher(filtered.toString());
        while (match.find()) {
            tokensList.add(match.group());
        }
        currentToken = "";
        currentTokenType = -1;
    }

    /**
     * Initializes the necessary regular expressions for tokenization.
     */
    private void initRegs() {
        keyWordRegEx = "";
        for (String s : keyWords.keySet()) {
            keyWordRegEx = new StringBuilder().append(keyWordRegEx).append(s + "|").toString();
        }
        symbolRegEx = "[\\&\\*\\+\\(\\)\\.\\/\\,\\-\\]\\;\\~\\}\\|\\{\\>\\=\\[\\<]";
        intRegEx = "[0-9]+";
        strRegEx = "\"[^\"\n]*\"";
        idRegEx = "[\\w_]+";
        tokenExpressions = Pattern.compile( strRegEx + "|" + intRegEx + "|" + idRegEx + "|" + keyWordRegEx + symbolRegEx);
    }


    /**
     * Checks if there are any remaining tokens to be read in the input.
     */
    public boolean hasMoreTokens() {
        return cursor < tokensList.size();
    }

    /**
     * Retrieves the next token from the input and sets it as the current token.
     * This method should only be invoked if there are more tokens to be read in the input.
     * Initially, there will be no current token.
     */
    public void advance() {
        currentToken = tokensList.get(cursor);
        cursor++;
        if (currentToken.matches(keyWordRegEx)) {
            currentTokenType = keyWord;
        } else if (currentToken.matches(symbolRegEx)) {
            currentTokenType = symbol;
        } else if (currentToken.matches(intRegEx)) {
            currentTokenType = intConst;
        } else if (currentToken.matches(strRegEx)) {
            currentTokenType = stringConst;
        } else if (currentToken.matches(idRegEx)) {
            currentTokenType = identifier;
        } else {
            throw new IllegalArgumentException("The current token, " + currentToken + ", is not recognized.\n" +
                    "\n" +
                    "\n" +
                    "\n");
        }
    }

    public String getCurrentToken() {
        return currentToken;
    }

    /**
     * Retrieves the current token's type.
     */
    public int tokenType() {
        return currentTokenType;
    }

    /**
     * Returns the keyword which is the current token
     * Should be called only when tokenType() is keyWord
     */
    public int keyWord() {
        if (currentTokenType == keyWord) {
            return keyWords.get(currentToken);
        } else {
            throw new IllegalStateException("The current token " + currentToken +" is not a keyWord");
        }
    }

    /**
     * Retrieves the keyword of the current token, if the current token is of type keyword.
     */
    public char symbol() {
        if (currentTokenType == symbol) {
            return currentToken.charAt(0);
        } else {
            throw new IllegalStateException("The current token " + currentToken +" is not a symbol");
        }
    }

    /**
     * Retrieves the identifier of the current token, if the current token is of type identifier.
     */
    public String identifier() {
        if (currentTokenType == identifier) {
            return currentToken;
        } else {
            throw new IllegalStateException("The current token " + currentToken +" is not an identifier");
        }
    }

    /**
     * Retrieves the intConst of the current token, if the current token is of type constant Integer
     */
    public int intVal() {
        if (currentTokenType == intConst) {
            return Integer.parseInt(currentToken);
        } else {
            throw new IllegalStateException("The current token " + currentToken +" is not an Integer");
        }
    }

    /**
     * Retrieves the intConst of the current token without the double quotes,
     * if the current token is of type String
     */
    public String stringVal() {
        if (currentTokenType == stringConst) {
            return currentToken.substring(1, currentToken.length() - 1);
        } else {
            throw new IllegalStateException("The current token " + currentToken +" is not a String");
        }
    }

    /**
     * decrease pointer by 1
     */
    public void decreasePointer() {
        if (cursor > 0) {
            cursor--;
        }
    }

    /**
     * Determines if the current symbol token is an operator.
     */
    public boolean isOp() {
        return operations.contains(symbol());
    }


    public static String noComments(String strIn) {
        int position = strIn.indexOf("//");
        if (position != -1) {
            strIn = strIn.substring(0, position);
        }
        return strIn;
    }

    /**
     * Removes block comments from the input.
     */
    public static String noBlockComments(String input) {
        int initialPosition = input.indexOf("/*");
        if (initialPosition == -1) return input;
        String output = input;
        int finalPosition = input.indexOf("*/");
        while (initialPosition != -1) {
            if (finalPosition == -1) {
                return input.substring(0, initialPosition - 1);
            }
            output = output.substring(0, initialPosition) + output.substring(finalPosition + 2);
            initialPosition = output.indexOf("/*");
            finalPosition = output.indexOf("*/");
        }
        return output;
    }
}