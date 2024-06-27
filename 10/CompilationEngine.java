import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

/**
 *The CompilationEngine is a module that:
 * Gets input from a JackTokenizer
 * Outputs parsed structure into an output file/stream
 * Uses series of compilexxx() routines for every syntactic element of Jack grammar
 * Each compilexxx() routine reads syntactic construct, advances tokenizer, and outputs parsing
 * In first version of compiler, emits structured printout of code wrapped in XML tags
 * In final version of compiler, generates executable VM code
 * Parsing logic and module API are the same in both versions.
 */
public class CompilationEngine {

    private PrintWriter writer;
    private PrintWriter tokenWriter;
    private JackTokenizer tokenizer;

    /**
     * Construct a new compilationEngine with the given inputFile, outputFile and outputTokenFile.
     */
    public CompilationEngine(File inputFile, File outputFile, File outputTokenFile) throws FileNotFoundException {
            tokenizer = new JackTokenizer(inputFile);
            writer = new PrintWriter(outputFile);
            tokenWriter = new PrintWriter(outputTokenFile);

        }

    /**
     * Compiles a complete class
     */
    public void compileClass() {
        tokenizer.advance();
        writer.print("<class>\n");
        tokenWriter.print("<tokens>\n");
        writer.print("<keyword> class </keyword>\n");
        tokenWriter.print("<keyword> class </keyword>\n");
        tokenizer.advance();
        writer.print("<identifier> " + tokenizer.identifier() + " </identifier>\n");
        tokenWriter.print("<identifier> " + tokenizer.identifier() + " </identifier>\n");
        tokenizer.advance();
        if (tokenizer.tokenType() == JackTokenizer.symbol && tokenizer.symbol() == '{') {
            writer.print("<symbol> { </symbol>\n");
            tokenWriter.print("<symbol> { </symbol>\n");
        }
        compileClassVarDec();
        compileSubroutine();
        tokenizer.advance();
        if (tokenizer.tokenType() == JackTokenizer.symbol && tokenizer.symbol() == '}') {
            writer.print("<symbol> } </symbol>\n");
            tokenWriter.print("<symbol> } </symbol>\n");
        }
        tokenWriter.print("</tokens>\n");
        writer.print("</class>\n");
        writer.close();
        tokenWriter.close();
    }
    /**
     * Compiles a class variable (static | field) declaration
     */
    private void compileClassVarDec() {
        //Check if the next token is either '}', indicating the end of a class variable declaration,
        //or the beginning of a subroutine declaration, or the start of a class variable declaration.
        tokenizer.advance();
        if (tokenizer.tokenType() == JackTokenizer.symbol && tokenizer.symbol() == '}') {
            tokenizer.decreasePointer();
            return;
        }
        if (tokenizer.keyWord() == JackTokenizer.CONSTRUCTOR || tokenizer.keyWord() == JackTokenizer.FUNCTION || tokenizer.keyWord() == JackTokenizer.METHOD) {
            tokenizer.decreasePointer();
            return;
        }
        writer.print("<classVarDec>\n");
        writer.print("<keyword> " + tokenizer.getCurrentToken() + " </keyword>\n");
        tokenWriter.print("<keyword> " + tokenizer.getCurrentToken() + " </keyword>\n");
        tokenizer.advance();
        // Determine if the next token is a keyword of a known type.
        if (tokenizer.tokenType() == JackTokenizer.keyWord && (tokenizer.keyWord() == JackTokenizer.INT
                || tokenizer.keyWord() == JackTokenizer.CHAR || tokenizer.keyWord() == JackTokenizer.BOOLEAN)) {
            writer.print("<keyword> " + tokenizer.getCurrentToken() + " </keyword>\n");
            tokenWriter.print("<keyword> " + tokenizer.getCurrentToken() + " </keyword>\n");
        }
        if (tokenizer.tokenType() == JackTokenizer.identifier) {
            writer.print("<identifier> " + tokenizer.identifier() + " </identifier>\n");
            tokenWriter.print("<identifier> " + tokenizer.identifier() + " </identifier>\n");
        }
        while (tokenizer.hasMoreTokens()) {
            tokenizer.advance();
            writer.print("<identifier> " + tokenizer.identifier() + " </identifier>\n");
            tokenWriter.print("<identifier> " + tokenizer.identifier() + " </identifier>\n");
            tokenizer.advance();
            if (tokenizer.symbol() == ',') {
                writer.print("<symbol> , </symbol>\n");
                tokenWriter.print("<symbol> , </symbol>\n");
            } else {
                writer.print("<symbol> ; </symbol>\n");
                tokenWriter.print("<symbol> ; </symbol>\n");
                break;
            }
        }
        writer.print("</classVarDec>\n");
        compileClassVarDec();
    }

    /**
     * Compiles a subroutine (method | function | constructor).
     */
    private void compileSubroutine() {
        //Determines if there is a subroutine by checking the next token, if it is '}' the pointer is moved back.
        tokenizer.advance();
        if (tokenizer.tokenType() == JackTokenizer.symbol && tokenizer.symbol() == '}') {
            tokenizer.decreasePointer();
            return;
        }
        writer.print("<subroutineDec>\n");
        writer.print("<keyword> " + tokenizer.getCurrentToken() + " </keyword>\n");
        tokenWriter.print("<keyword> " + tokenizer.getCurrentToken() + " </keyword>\n");
        tokenizer.advance();
        if (tokenizer.tokenType() == JackTokenizer.keyWord && tokenizer.keyWord() == JackTokenizer.VOID) {
            writer.print("<keyword> void </keyword>\n");
            tokenWriter.print("<keyword> void </keyword>\n");
        } else {
            tokenizer.decreasePointer();
            tokenizer.advance();
            // Determine if the next token is a keyword of a known type.
            if (tokenizer.tokenType() == JackTokenizer.keyWord && (tokenizer.keyWord() == JackTokenizer.INT
                    || tokenizer.keyWord() == JackTokenizer.CHAR || tokenizer.keyWord() == JackTokenizer.BOOLEAN)) {
                writer.print("<keyword> " + tokenizer.getCurrentToken() + " </keyword>\n");
                tokenWriter.print("<keyword> " + tokenizer.getCurrentToken() + " </keyword>\n");
            }
            if (tokenizer.tokenType() == JackTokenizer.identifier) {
                writer.print("<identifier> " + tokenizer.identifier() + " </identifier>\n");
                tokenWriter.print("<identifier> " + tokenizer.identifier() + " </identifier>\n");
            }
        }
        tokenizer.advance();
        writer.print("<identifier> " + tokenizer.identifier() + " </identifier>\n");
        tokenWriter.print("<identifier> " + tokenizer.identifier() + " </identifier>\n");
        tokenizer.advance();
        if (tokenizer.tokenType() == JackTokenizer.symbol && tokenizer.symbol() == '(') {
            writer.print("<symbol> ( </symbol>\n");
            tokenWriter.print("<symbol> ( </symbol>\n");
        }
        writer.print("<parameterList>\n");
        compileParameterList();
        writer.print("</parameterList>\n");
        tokenizer.advance();
        if (tokenizer.tokenType() == JackTokenizer.symbol && tokenizer.symbol() == ')') {
            writer.print("<symbol> ) </symbol>\n");
            tokenWriter.print("<symbol> ) </symbol>\n");
        }
        compileSubroutineBody();
        writer.print("</subroutineDec>\n");
        compileSubroutine();
    }

    /**
     * Compiles the body of a subroutine
     */
    private void compileSubroutineBody() {
        writer.print("<subroutineBody>\n");
        tokenizer.advance();
        if (tokenizer.tokenType() == JackTokenizer.symbol && tokenizer.symbol() == '{') {
            writer.print("<symbol> { </symbol>\n");
            tokenWriter.print("<symbol> { </symbol>\n");
        }
        compileVarDec();
        writer.print("<statements>\n");
        compileStatement();
        writer.print("</statements>\n");
        tokenizer.advance();
        if (tokenizer.tokenType() == JackTokenizer.symbol && tokenizer.symbol() == '}') {
            writer.print("<symbol> } </symbol>\n");
            tokenWriter.print("<symbol> } </symbol>\n");
        }
        writer.print("</subroutineBody>\n");
    }

    /**
     * Compiles statement
     */
    private void compileStatement() {
        //Determines if there is a subroutine by checking the next token, if it is '}' the pointer is moved back.
        tokenizer.advance();
        if (tokenizer.tokenType() == JackTokenizer.symbol && tokenizer.symbol() == '}') {
            tokenizer.decreasePointer();
            return;
        }
         else {
            if (tokenizer.keyWord() == JackTokenizer.LET) {
                compileLet();
            } else if (tokenizer.keyWord() == JackTokenizer.IF) {
                compileIf();
            } else if (tokenizer.keyWord() == JackTokenizer.WHILE) {
                compilesWhile();
            } else if (tokenizer.keyWord() == JackTokenizer.DO) {
                compileDo();
            } else if (tokenizer.keyWord() == JackTokenizer.RETURN) {
                compileReturn();
            }
        }

        compileStatement();
    }

    /**
     * Compiles a parameter list
     */
    private void compileParameterList() {
        //Determines if there is a parameters by checking the next token, if it is ')' the pointer is moved back.
        tokenizer.advance();
        if (tokenizer.tokenType() == JackTokenizer.symbol && tokenizer.symbol() == ')') {
            tokenizer.decreasePointer();
            return;
        }
        tokenizer.decreasePointer();
        while (tokenizer.hasMoreTokens()) {
            tokenizer.advance();
            // Determines if the next token is a keyword of a known type.
            if (tokenizer.tokenType() == JackTokenizer.keyWord && (tokenizer.keyWord() == JackTokenizer.INT
                    || tokenizer.keyWord() == JackTokenizer.CHAR || tokenizer.keyWord() == JackTokenizer.BOOLEAN)) {
                writer.print("<keyword> " + tokenizer.getCurrentToken() + " </keyword>\n");
                tokenWriter.print("<keyword> " + tokenizer.getCurrentToken() + " </keyword>\n");
            }
            if (tokenizer.tokenType() == JackTokenizer.identifier) {
                writer.print("<identifier> " + tokenizer.identifier() + " </identifier>\n");
                tokenWriter.print("<identifier> " + tokenizer.identifier() + " </identifier>\n");
            }
            tokenizer.advance();
            writer.print("<identifier> " + tokenizer.identifier() + " </identifier>\n");
            tokenWriter.print("<identifier> " + tokenizer.identifier() + " </identifier>\n");
            tokenizer.advance();
            if (tokenizer.symbol() == ',') {
                writer.print("<symbol> , </symbol>\n");
                tokenWriter.print("<symbol> , </symbol>\n");
            } else {
                tokenizer.decreasePointer();
                break;
            }
        }

    }

    /**
     * Compiles a var declaration
     */
    private void compileVarDec() {
    //Determines if there is a variable declaration to compile by checking the next token. If the next token is not a keyword or the keyword is not "var", the pointer is moved back.
        tokenizer.advance();
        if (tokenizer.tokenType() != JackTokenizer.keyWord || tokenizer.keyWord() != JackTokenizer.VAR) {
            tokenizer.decreasePointer();
            return;
        }
        writer.print("<varDec>\n");
        writer.print("<keyword> var </keyword>\n");
        tokenWriter.print("<keyword> var </keyword>\n");
        tokenizer.advance();
        // Determines if the next token is a keyword of a known type.
        if (tokenizer.tokenType() == JackTokenizer.keyWord && (tokenizer.keyWord() == JackTokenizer.INT
                || tokenizer.keyWord() == JackTokenizer.CHAR || tokenizer.keyWord() == JackTokenizer.BOOLEAN)) {
            writer.print("<keyword> " + tokenizer.getCurrentToken() + " </keyword>\n");
            tokenWriter.print("<keyword> " + tokenizer.getCurrentToken() + " </keyword>\n");
        }
        if (tokenizer.tokenType() == JackTokenizer.identifier) {
            writer.print("<identifier> " + tokenizer.identifier() + " </identifier>\n");
            tokenWriter.print("<identifier> " + tokenizer.identifier() + " </identifier>\n");
        }
        while (tokenizer.hasMoreTokens()) {
            tokenizer.advance();
            writer.print("<identifier> " + tokenizer.identifier() + " </identifier>\n");
            tokenWriter.print("<identifier> " + tokenizer.identifier() + " </identifier>\n");
            tokenizer.advance();
            if (tokenizer.symbol() == ',') {
                writer.print("<symbol> , </symbol>\n");
                tokenWriter.print("<symbol> , </symbol>\n");
            } else {
                writer.print("<symbol> ; </symbol>\n");
                tokenWriter.print("<symbol> ; </symbol>\n");
                break;
            }
        }
        writer.print("</varDec>\n");
        compileVarDec();
    }

    /**
     * Compiles a do statement
     */
    private void compileDo() {
        writer.print("<doStatement>\n");
        writer.print("<keyword> do </keyword>\n");
        tokenWriter.print("<keyword> do </keyword>\n");
        compileSubroutineCall();
        tokenizer.advance();
        if (tokenizer.tokenType() == JackTokenizer.symbol && tokenizer.symbol() == ';') {
            writer.print("<symbol> ; </symbol>\n");
            tokenWriter.print("<symbol> ; </symbol>\n");
        }
        writer.print("</doStatement>\n");
    }
    /**
     * Compiles a let statement
     */
    private void compileLet() {
        writer.print("<letStatement>\n");
        writer.print("<keyword> let </keyword>\n");
        tokenWriter.print("<keyword> let </keyword>\n");
        tokenizer.advance();
        writer.print("<identifier> " + tokenizer.identifier() + " </identifier>\n");
        tokenWriter.print("<identifier> " + tokenizer.identifier() + " </identifier>\n");
        tokenizer.advance();
        boolean expression = false;
        if (tokenizer.symbol() == '[') {
            expression = true;
            writer.print("<symbol> [ </symbol>\n");
            tokenWriter.print("<symbol> [ </symbol>\n");
            compileExpression();
            tokenizer.advance();
                writer.print("<symbol> ] </symbol>\n");
                tokenWriter.print("<symbol> ] </symbol>\n");
        }

        if (expression) tokenizer.advance();
        writer.print("<symbol> = </symbol>\n");
        tokenWriter.print("<symbol> = </symbol>\n");
        compileExpression();
        tokenizer.advance();
        if (tokenizer.tokenType() == JackTokenizer.symbol && tokenizer.symbol() == ';') {
            writer.print("<symbol> ; </symbol>\n");
            tokenWriter.print("<symbol> ; </symbol>\n");
        }
        writer.print("</letStatement>\n");
    }

    /**
     * Compiles a while statement
     */
    private void compilesWhile() {
        writer.print("<whileStatement>\n");
        writer.print("<keyword> while </keyword>\n");
        tokenWriter.print("<keyword> while </keyword>\n");
        tokenizer.advance();
        if (tokenizer.tokenType() == JackTokenizer.symbol && tokenizer.symbol() == '(') {
            writer.print("<symbol> ( </symbol>\n");
            tokenWriter.print("<symbol> ( </symbol>\n");
        }
        compileExpression();
        tokenizer.advance();
        if (tokenizer.tokenType() == JackTokenizer.symbol && tokenizer.symbol() == ')') {
            writer.print("<symbol> ) </symbol>\n");
            tokenWriter.print("<symbol> ) </symbol>\n");
        }
        tokenizer.advance();
        if (tokenizer.tokenType() == JackTokenizer.symbol && tokenizer.symbol() == '{') {
            writer.print("<symbol> { </symbol>\n");
            tokenWriter.print("<symbol> { </symbol>\n");
        }
        writer.print("<statements>\n");
        compileStatement();
        writer.print("</statements>\n");
        tokenizer.advance();
        if (tokenizer.tokenType() == JackTokenizer.symbol && tokenizer.symbol() == '}') {
            writer.print("<symbol> } </symbol>\n");
            tokenWriter.print("<symbol> } </symbol>\n");
        }
        writer.print("</whileStatement>\n");
    }

    /**
     * Compiles a return statement
     */
    private void compileReturn() {
        writer.print("<returnStatement>\n");
        writer.print("<keyword> return </keyword>\n");
        tokenWriter.print("<keyword> return </keyword>\n");
        tokenizer.advance();
        if (tokenizer.tokenType() == JackTokenizer.symbol && tokenizer.symbol() == ';') {
            writer.print("<symbol> ; </symbol>\n");
            tokenWriter.print("<symbol> ; </symbol>\n");
            writer.print("</returnStatement>\n");
            return;
        }
        tokenizer.decreasePointer();
        compileExpression();
        tokenizer.advance();
        if (tokenizer.tokenType() == JackTokenizer.symbol && tokenizer.symbol() == ';') {
            writer.print("<symbol> ; </symbol>\n");
            tokenWriter.print("<symbol> ; </symbol>\n");
        }
        writer.print("</returnStatement>\n");
    }

    /**
     * Compiles an if statement
     * may include an else statement if present
     */
    private void compileIf() {
        writer.print("<ifStatement>\n");
        writer.print("<keyword> if </keyword>\n");
        tokenWriter.print("<keyword>  if </keyword>\n");
        tokenizer.advance();
        if (tokenizer.tokenType() == JackTokenizer.symbol && tokenizer.symbol() == '(') {
            writer.print("<symbol> ( </symbol>\n");
            tokenWriter.print("<symbol> ( </symbol>\n");
        }
        compileExpression();
        tokenizer.advance();
        if (tokenizer.tokenType() == JackTokenizer.symbol && tokenizer.symbol() == ')') {
            writer.print("<symbol> ) </symbol>\n");
            tokenWriter.print("<symbol> ) </symbol>\n");
        }
        tokenizer.advance();
        if (tokenizer.tokenType() == JackTokenizer.symbol && tokenizer.symbol() == '{') {
            writer.print("<symbol> { </symbol>\n");
            tokenWriter.print("<symbol> { </symbol>\n");
        }
        writer.print("<statements>\n");
        compileStatement();
        writer.print("</statements>\n");
        tokenizer.advance();
        if (tokenizer.tokenType() == JackTokenizer.symbol && tokenizer.symbol() == '}') {
            writer.print("<symbol> } </symbol>\n");
            tokenWriter.print("<symbol> } </symbol>\n");
        }
        tokenizer.advance();
        if (tokenizer.tokenType() == JackTokenizer.keyWord && tokenizer.keyWord() == JackTokenizer.ELSE) {
            writer.print("<keyword> else </keyword>\n");
            tokenWriter.print("<keyword> else </keyword>\n");
            tokenizer.advance();
            if (tokenizer.tokenType() == JackTokenizer.symbol && tokenizer.symbol() == '{') {
                writer.print("<symbol> { </symbol>\n");
                tokenWriter.print("<symbol> { </symbol>\n");
            }
            writer.print("<statements>\n");
            compileStatement();
            writer.print("</statements>\n");
            tokenizer.advance();
            if (tokenizer.tokenType() == JackTokenizer.symbol && tokenizer.symbol() == '}') {
                writer.print("<symbol> } </symbol>\n");
                tokenWriter.print("<symbol> } </symbol>\n");
            }
        } else {
            tokenizer.decreasePointer();
        }
        writer.print("</ifStatement>\n");
    }

    /**
     * Compiles a term.
     * This routine must determine if the current token is an identifier representing a variable, an array entry or a subroutine call.
     * The look-ahead token, "[" "(" "." is used to distinguish between these possibilities.
     * Any other token not related to this term will not be advanced over.
     */
    private void compileTerm() {
        writer.print("<term>\n");
        tokenizer.advance();
        if (tokenizer.tokenType() == JackTokenizer.identifier) {
            String identifier = tokenizer.identifier();
            tokenizer.advance();
            if (tokenizer.tokenType() == JackTokenizer.symbol && tokenizer.symbol() == '[') {
                writer.print("<identifier >" + identifier + " </identifier>\n");
                tokenWriter.print("<identifier> " + identifier + " </identifier>\n");
                writer.print("<symbol> [ </symbol>\n");
                tokenWriter.print("<symbol> [ </symbol>\n");
                compileExpression();
                tokenizer.advance();
                if (tokenizer.tokenType() == JackTokenizer.symbol && tokenizer.symbol() == ']') {
                    writer.print("<symbol> ] </symbol>\n");
                    tokenWriter.print("<symbol> ] </symbol>\n");
                }
            } else if (tokenizer.tokenType() == JackTokenizer.symbol && (tokenizer.symbol() == '(' || tokenizer.symbol() == '.')) {
                tokenizer.decreasePointer();
                tokenizer.decreasePointer();
                compileSubroutineCall();
            } else {
                writer.print("<identifier> " + identifier + " </identifier>\n");
                tokenWriter.print("<identifier> " + identifier + " </identifier>\n");
                tokenizer.decreasePointer();
            }
        } else {
            if (tokenizer.tokenType() == JackTokenizer.intConst) {
                writer.print("<integerConstant> " + tokenizer.intVal() + " </integerConstant>\n");
                tokenWriter.print("<integerConstant> " + tokenizer.intVal() + " </integerConstant>\n");
            } else if (tokenizer.tokenType() == JackTokenizer.stringConst) {
                writer.print("<stringConstant> " + tokenizer.stringVal() + " </stringConstant>\n");
                tokenWriter.print("<stringConstant> " + tokenizer.stringVal() + " </stringConstant>\n");
            } else if (tokenizer.tokenType() == JackTokenizer.keyWord &&
                    (tokenizer.keyWord() == JackTokenizer.TRUE ||
                            tokenizer.keyWord() == JackTokenizer.FALSE ||
                            tokenizer.keyWord() == JackTokenizer.NULL ||
                            tokenizer.keyWord() == JackTokenizer.THIS)) {
                writer.print("<keyword> " + tokenizer.getCurrentToken() + " </keyword>\n");
                tokenWriter.print("<keyword> " + tokenizer.getCurrentToken() + " </keyword>\n");
            } else if (tokenizer.tokenType() == JackTokenizer.symbol && tokenizer.symbol() == '(') {
                writer.print("<symbol> ( </symbol>\n");
                tokenWriter.print("<symbol> ( </symbol>\n");
                compileExpression();
                tokenizer.advance();
                if (tokenizer.tokenType() == JackTokenizer.symbol && tokenizer.symbol() == ')') {
                    writer.print("<symbol> ) </symbol>\n");
                    tokenWriter.print("<symbol> ) </symbol>\n");
                }
            } else if (tokenizer.tokenType() == JackTokenizer.symbol && (tokenizer.symbol() == '-' || tokenizer.symbol() == '~')) {
                writer.print("<symbol> " + tokenizer.symbol() + " </symbol>\n");
                tokenWriter.print("<symbol> " + tokenizer.symbol() + " </symbol>\n");
                compileTerm();
            }
        }
        writer.print("</term>\n");
    }

    /**
     * Compiles a subroutine call
     */
    private void compileSubroutineCall() {
        tokenizer.advance();
        writer.print("<identifier> " + tokenizer.identifier() + " </identifier>\n");
        tokenWriter.print("<identifier> " + tokenizer.identifier() + " </identifier>\n");
        tokenizer.advance();
        if (tokenizer.tokenType() == JackTokenizer.symbol && tokenizer.symbol() == '(') {
            writer.print("<symbol> ( </symbol>\n");
            tokenWriter.print("<symbol> ( </symbol>\n");
            writer.print("<expressionList>\n");
            compileExpressionList();
            writer.print("</expressionList>\n");
            tokenizer.advance();
            if (tokenizer.tokenType() == JackTokenizer.symbol && tokenizer.symbol() == ')') {
                writer.print("<symbol> ) </symbol>\n");
                tokenWriter.print("<symbol> ) </symbol>\n");
            }
        } else if (tokenizer.tokenType() == JackTokenizer.symbol && tokenizer.symbol() == '.') {
            writer.print("<symbol> . </symbol>\n");
            tokenWriter.print("<symbol> . </symbol>\n");
            tokenizer.advance();
            writer.print("<identifier> " + tokenizer.identifier() + " </identifier>\n");
            tokenWriter.print("<identifier> " + tokenizer.identifier() + " </identifier>\n");
            tokenizer.advance();
            if (tokenizer.tokenType() == JackTokenizer.symbol && tokenizer.symbol() == '(') {
                writer.print("<symbol> ( </symbol>\n");
                tokenWriter.print("<symbol> ( </symbol>\n");
            }
            writer.print("<expressionList>\n");
            compileExpressionList();
            writer.print("</expressionList>\n");
            tokenizer.advance();
            if (tokenizer.tokenType() == JackTokenizer.symbol && tokenizer.symbol() == ')') {
                writer.print("<symbol> ) </symbol>\n");
                tokenWriter.print("<symbol> ) </symbol>\n");
            }
        }
    }

    /**
     * Compiles an expression
     */
    private void compileExpression() {
        writer.print("<expression>\n");
        compileTerm();
        while (tokenizer.hasMoreTokens()) {
            tokenizer.advance();
            if (tokenizer.tokenType() == JackTokenizer.symbol && tokenizer.isOp()) {
                if (tokenizer.symbol() == '>') {
                    writer.print("<symbol> &gt; </symbol>\n");
                    tokenWriter.print("<symbol> &gt; </symbol>\n");
                } else if (tokenizer.symbol() == '<') {
                    writer.print("<symbol> &lt; </symbol>\n");
                    tokenWriter.print("<symbol> &lt; </symbol>\n");
                } else if (tokenizer.symbol() == '&') {
                    writer.print("<symbol> &amp; </symbol>\n");
                    tokenWriter.print("<symbol> &amp; </symbol>\n");
                } else {
                    writer.print("<symbol> " + tokenizer.symbol() + " </symbol>\n");
                    tokenWriter.print("<symbol> " + tokenizer.symbol() + " </symbol>\n");
                }
                compileTerm();
            } else {
                tokenizer.decreasePointer();
                break;
            }
        }
        writer.print("</expression>\n");
    }

    /**
     * Compiles a list of expressions
     */
    private void compileExpressionList() {
        tokenizer.advance();
        //Checks if there are any expressions to compile, if the next token to be read is ')', the pointer is moved back.
        if (tokenizer.tokenType() == JackTokenizer.symbol && tokenizer.symbol() == ')') {
            tokenizer.decreasePointer();
        } else {
            tokenizer.decreasePointer();
            compileExpression();
            while(tokenizer.hasMoreTokens()) {
                tokenizer.advance();
                if (tokenizer.tokenType() == JackTokenizer.symbol && tokenizer.symbol() == ',') {
                    writer.print("<symbol> , </symbol>\n");
                    tokenWriter.print("<symbol> , </symbol>\n");
                    compileExpression();
                } else {
                    tokenizer.decreasePointer();
                    break;
                }
            }
        }
    }
}
