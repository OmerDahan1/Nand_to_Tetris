import java.io.File;
import java.io.FileNotFoundException;

/**
 * This class performs the compilation of input from a JackTokenizer and writes the output to a VMWriter. 
 * It consists of a series of compilexxx() routines for each syntactic element of the Jack language. 
 * Each routine reads the next element, advances the tokenizer and emits the corresponding VM code. 
 * If the element is part of an expression, the emitted code calculates its value and leaves it at the top of the VM stack.
 */
public class CompilationEngine {

    private VMWriter vmWriter;
    private JackTokenizer tokenizer;
    private SymbolTable symbolTable;
    private String currentClass;
    private String currentSubroutine;
    private int labelIndex;

    /**
     * Construct a new compiler with the provided inputFile and outputFile.
     */
    public CompilationEngine(File inputFile, File outputFile) throws FileNotFoundException {
        tokenizer = new JackTokenizer(inputFile);
        vmWriter = new VMWriter(outputFile);
        symbolTable = new SymbolTable();
        labelIndex = 0;
    }

    /**
     * Compiles a complete class
     */
    public void compileClass(){
        tokenizer.advance();
        tokenizer.advance();
        currentClass = tokenizer.identifier();
        //'{'
        tokenizer.advance();
       compileClassVarDec();
        compileSubroutine();
        //'}'
        tokenizer.advance();
        vmWriter.close();
    }

    /**
     * Compiles a class variable (static | field) declaration
     */
    private void compileClassVarDec(){
        //Check if the next token is either '}', indicating the end of a class variable declaration,
        //or the beginning of a subroutine declaration, or the start of a class variable declaration.
        tokenizer.advance();
        if (tokenizer.tokenType() == JackTokenizer.symbol && tokenizer.symbol() == '}'){
            tokenizer.decreasePointer();
            return;
        }
        if (tokenizer.keyWord() == JackTokenizer.CONSTRUCTOR || tokenizer.keyWord() == JackTokenizer.FUNCTION || tokenizer.keyWord() == JackTokenizer.METHOD){
            tokenizer.decreasePointer();
            return;
        }
        Symbol.KIND kind = null;
        String type = "";
        String name = "";
        int i = tokenizer.keyWord();
        if (i == JackTokenizer.STATIC) {
            kind = Symbol.KIND.STATIC;
        } else if (i == JackTokenizer.FIELD) {
            kind = Symbol.KIND.FIELD;
        }
        tokenizer.advance();
        if (tokenizer.tokenType() == JackTokenizer.keyWord && (tokenizer.keyWord() == JackTokenizer.INT || tokenizer.keyWord() == JackTokenizer.CHAR || tokenizer.keyWord() == JackTokenizer.BOOLEAN)) {
            type = tokenizer.getCurrentToken();
        }else {
            type = tokenizer.identifier();
        }
        while (tokenizer.hasMoreTokens()) {
            tokenizer.advance();
            name = tokenizer.identifier();
            symbolTable.define(name, type, kind);
            //if tokenizer.symbol() is ';' we don't have more variables from this type
            tokenizer.advance();
            if (tokenizer.symbol() == ';') {
                break;
            }
        }
        compileClassVarDec();
    }

    /**
     * Compiles a subroutine (method | function | constructor).
     */
    private void compileSubroutine(){
        //Determines if there is a subroutine by checking the next token, if it is '}' the pointer is moved back.
        tokenizer.advance();
        if (tokenizer.tokenType() == JackTokenizer.symbol && tokenizer.symbol() == '}'){
            tokenizer.decreasePointer();
            return;
        }
        int keyword = tokenizer.keyWord();
        symbolTable.startSubroutine();
        if (tokenizer.keyWord() == JackTokenizer.METHOD){
            symbolTable.define("this",currentClass, Symbol.KIND.ARG);
        }
        String type = "";
        tokenizer.advance();
        if (tokenizer.tokenType() == JackTokenizer.keyWord && tokenizer.keyWord() == JackTokenizer.VOID){
            type = "void";
        }else {
            tokenizer.decreasePointer();
            tokenizer.advance();
            if (tokenizer.tokenType() == JackTokenizer.keyWord && (tokenizer.keyWord() == JackTokenizer.INT || tokenizer.keyWord() == JackTokenizer.CHAR || tokenizer.keyWord() == JackTokenizer.BOOLEAN)) {
                type = tokenizer.getCurrentToken();
            }else {
                type = tokenizer.identifier();
            }
        }
        tokenizer.advance();
        currentSubroutine = tokenizer.identifier();
        //'('
        tokenizer.advance();
        compileParameterList();
        //')'
        tokenizer.advance();
        compileSubroutineBody(keyword);
        compileSubroutine();
    }

    /**
     * Compiles the body of a subroutine
     */
    private void compileSubroutineBody(int keyword){
        //'{'
        tokenizer.advance();
        compileVarDec();
        String functionDec = currentClass + "." + currentSubroutine;
        vmWriter.writeFunction(functionDec,symbolTable.varCount(Symbol.KIND.VAR));
        if (keyword == JackTokenizer.METHOD){
            vmWriter.writePush(VMWriter.SEGMENT.ARG, 0);
            vmWriter.writePop(VMWriter.SEGMENT.POINTER,0);

        }else if (keyword == JackTokenizer.CONSTRUCTOR){
            vmWriter.writePush(VMWriter.SEGMENT.CONST,symbolTable.varCount(Symbol.KIND.FIELD));
            vmWriter.writeCall("Memory.alloc", 1);
            vmWriter.writePop(VMWriter.SEGMENT.POINTER,0);
        }
        compileStatement();
        //'}'
        tokenizer.advance();
    }

    /**
     * Compiles statement
     */
    private void compileStatement(){
        //Determines if there is a subroutine by checking the next token, if it is '}' the pointer is moved back.
        tokenizer.advance();
        if (tokenizer.tokenType() == JackTokenizer.symbol && tokenizer.symbol() == '}'){
            tokenizer.decreasePointer();
            return;
        }
            int keyWord = tokenizer.keyWord();
            if (keyWord == JackTokenizer.LET) {
                compileLet();
            } else if (keyWord == JackTokenizer.IF) {
                compileIf();
            } else if (keyWord == JackTokenizer.WHILE) {
                compilesWhile();
            } else if (keyWord == JackTokenizer.DO) {
                compileDo();
            } else {
                compileReturn();
            }
        compileStatement();
    }
    /**
     * Compiles a parameter list
     */
    private void compileParameterList(){
        //Determines if there is a parameters by checking the next token, if it is ')' the pointer is moved back.
        tokenizer.advance();
        if (tokenizer.tokenType() == JackTokenizer.symbol && tokenizer.symbol() == ')'){
            tokenizer.decreasePointer();
            return;
        }
        String type = "";
        //there is at least one parameter
        tokenizer.decreasePointer();
        while (tokenizer.hasMoreTokens()) {
            tokenizer.advance();
            // Determines if the next token is a keyword of a known type.
            if (tokenizer.tokenType() == JackTokenizer.keyWord && (tokenizer.keyWord() == JackTokenizer.INT || tokenizer.keyWord() == JackTokenizer.CHAR || tokenizer.keyWord() == JackTokenizer.BOOLEAN)) {
                type = tokenizer.getCurrentToken();
            }else {
                type = tokenizer.identifier();
            }
            tokenizer.advance();
            symbolTable.define(tokenizer.identifier(), type, Symbol.KIND.ARG);
            //if tokenizer.symbol() is ')' we don't have more parameters
            tokenizer.advance();
            if (tokenizer.symbol() == ')') {
                tokenizer.decreasePointer();
                break;
            }
        }
    }

    /**
     * Compiles a var declaration
     */
    private void compileVarDec(){
        //Determines if there is a variable declaration to compile by checking the next token. If the next token is not a keyword or the keyword is not "var", the pointer is moved back.
        tokenizer.advance();
        if (tokenizer.tokenType() != JackTokenizer.keyWord || tokenizer.keyWord() != JackTokenizer.VAR){
            tokenizer.decreasePointer();
            return;
        }
        String type = "";
        tokenizer.advance();
        if (tokenizer.tokenType() == JackTokenizer.keyWord && (tokenizer.keyWord() == JackTokenizer.INT || tokenizer.keyWord() == JackTokenizer.CHAR || tokenizer.keyWord() == JackTokenizer.BOOLEAN)) {
            type = tokenizer.getCurrentToken();
        }else {
            type = tokenizer.identifier();
        }
        while (tokenizer.hasMoreTokens()) {
            tokenizer.advance();
            symbolTable.define(tokenizer.identifier(), type, Symbol.KIND.VAR);
            //if tokenizer.symbol() is ';' we don't have more variables from this type
            tokenizer.advance();
            if (tokenizer.symbol() == ';') {
                break;
            }
        }
        compileVarDec();
    }

    /**
     * Compiles a do statement
     */
    private void compileDo(){
        compileSubroutineCall();
        tokenizer.advance();
        vmWriter.writePop(VMWriter.SEGMENT.TEMP,0);
    }

    /**
     * Compiles a let statement
     */
    private void compileLet(){
        tokenizer.advance();
        String variable = tokenizer.identifier();
        //'[' or '='
        tokenizer.advance();
        boolean expression = false;
        if (tokenizer.symbol() == '['){
            expression = true;
            vmWriter.writePush(vmWriter.getSegment(symbolTable.kindOf(variable)),symbolTable.indexOf(variable));
            compileExpression();
            //']'
            tokenizer.advance();
            vmWriter.writeArithmetic(VMWriter.OPERATION.ADD);
        }
        if (expression) tokenizer.advance();
        compileExpression();
        tokenizer.advance();
        if (expression){
            vmWriter.writePop(VMWriter.SEGMENT.TEMP,0);
            vmWriter.writePop(VMWriter.SEGMENT.POINTER,1);
            vmWriter.writePush(VMWriter.SEGMENT.TEMP,0);
            vmWriter.writePop(VMWriter.SEGMENT.THAT,0);
        }else {
            vmWriter.writePop(vmWriter.getSegment(symbolTable.kindOf(variable)), symbolTable.indexOf(variable));
        }
    }


    /**
     * Compiles a while statement
     */
    private void compilesWhile(){
        String loopLabel = "LABEL_" + (labelIndex++);
        String startLabel = "LABEL_" + (labelIndex++);
        vmWriter.writeLabel(startLabel);
        tokenizer.advance();
        compileExpression();
        tokenizer.advance();
        vmWriter.writeArithmetic(VMWriter.OPERATION.NOT);
        vmWriter.writeIf(loopLabel);
        //'{'
        tokenizer.advance();
        compileStatement();
        //'}'
        tokenizer.advance();
        vmWriter.writeGoto(startLabel);
        vmWriter.writeLabel(loopLabel);
    }

    /**
     * Compiles a return statement
     */
    private void compileReturn(){
        tokenizer.advance();
        if (tokenizer.tokenType() == JackTokenizer.symbol && tokenizer.symbol() == ';'){
            vmWriter.writePush(VMWriter.SEGMENT.CONST,0);
        }else {
            tokenizer.decreasePointer();
            compileExpression();
            //';'
            tokenizer.advance();
        }
        vmWriter.writeReturn();
    }

    /**
     * Compiles an if statement
     * may include an else statement if present
     */
    private void compileIf(){
        String elseLabel = "LABEL_" + (labelIndex++);
        String endLabel = "LABEL_" + (labelIndex++);
        //'('
        tokenizer.advance();
        compileExpression();
        //')'
        tokenizer.advance();
        vmWriter.writeArithmetic(VMWriter.OPERATION.NOT);
        vmWriter.writeIf(elseLabel);
        //'{'
        tokenizer.advance();
        compileStatement();
        //'}'
        tokenizer.advance();
        vmWriter.writeGoto(endLabel);
        vmWriter.writeLabel(elseLabel);
        tokenizer.advance();
        if (tokenizer.tokenType() == JackTokenizer.keyWord && tokenizer.keyWord() == JackTokenizer.ELSE){
            //'{'
            tokenizer.advance();
            compileStatement();
            //'}'
            tokenizer.advance();
        }else {
            tokenizer.decreasePointer();
        }
        vmWriter.writeLabel(endLabel);
    }

    /**
     * Compiles a term.
     * This routine must determine if the current token is an identifier representing a variable, an array entry or a subroutine call.
     * The look-ahead token, "[" "(" "." is used to distinguish between these possibilities.
     * Any other token not related to this term will not be advanced over.
     */
    private void compileTerm(){
        tokenizer.advance();
        if (tokenizer.tokenType() == JackTokenizer.identifier){
            String identifier = tokenizer.identifier();
            tokenizer.advance();
            if (tokenizer.tokenType() == JackTokenizer.symbol && tokenizer.symbol() == '['){
                vmWriter.writePush(vmWriter.getSegment(symbolTable.kindOf(identifier)),symbolTable.indexOf(identifier));
                compileExpression();
                //']'
                tokenizer.advance();
                vmWriter.writeArithmetic(VMWriter.OPERATION.ADD);
                vmWriter.writePop(VMWriter.SEGMENT.POINTER,1);
                vmWriter.writePush(VMWriter.SEGMENT.THAT,0);
            }else if (tokenizer.tokenType() == JackTokenizer.symbol && (tokenizer.symbol() == '(' || tokenizer.symbol() == '.')){
                tokenizer.decreasePointer();
                tokenizer.decreasePointer();
                compileSubroutineCall();
            }else {
                tokenizer.decreasePointer();
                vmWriter.writePush(vmWriter.getSegment(symbolTable.kindOf(identifier)), symbolTable.indexOf(identifier));
            }
        }else{
            if (tokenizer.tokenType() == JackTokenizer.intConst){
                vmWriter.writePush(VMWriter.SEGMENT.CONST,tokenizer.intVal());
            }else if (tokenizer.tokenType() == JackTokenizer.stringConst){
                String str = tokenizer.stringVal();
                vmWriter.writePush(VMWriter.SEGMENT.CONST,str.length());
                vmWriter.writeCall("String.new",1);
                for (int i = 0; i < str.length(); i++){
                    vmWriter.writePush(VMWriter.SEGMENT.CONST,(int)str.charAt(i));
                    vmWriter.writeCall("String.appendChar",2);
                }
            }else if(tokenizer.tokenType() == JackTokenizer.keyWord && tokenizer.keyWord() == JackTokenizer.TRUE){
                vmWriter.writePush(VMWriter.SEGMENT.CONST,0);
                vmWriter.writeArithmetic(VMWriter.OPERATION.NOT);
            }else if(tokenizer.tokenType() == JackTokenizer.keyWord && tokenizer.keyWord() == JackTokenizer.THIS){
                vmWriter.writePush(VMWriter.SEGMENT.POINTER,0);
            }else if(tokenizer.tokenType() == JackTokenizer.keyWord && (tokenizer.keyWord() == JackTokenizer.FALSE || tokenizer.keyWord() == JackTokenizer.NULL)){
                vmWriter.writePush(VMWriter.SEGMENT.CONST,0);
            }else if (tokenizer.tokenType() == JackTokenizer.symbol && tokenizer.symbol() == '('){
                compileExpression();
                //')'
                tokenizer.advance();
            }else{
                char symbol = tokenizer.symbol();
                compileTerm();
                if (symbol == '-'){
                    vmWriter.writeArithmetic(VMWriter.OPERATION.NEG);
                }else {
                    vmWriter.writeArithmetic(VMWriter.OPERATION.NOT);
                }
            }
        }
    }

    /**
     * Compiles a subroutine call
     */
    private void compileSubroutineCall(){
        tokenizer.advance();
        String subroutineName = tokenizer.identifier();
        int nArgs = 0;
        tokenizer.advance();
        if (tokenizer.tokenType() == JackTokenizer.symbol && tokenizer.symbol() == '('){
            vmWriter.writePush(VMWriter.SEGMENT.POINTER,0);
            nArgs = compileExpressionList() + 1;
            //')'
            tokenizer.advance();
            vmWriter.writeCall(currentClass + '.' + subroutineName, nArgs);
        }else {
            String className = subroutineName;
            tokenizer.advance();
            subroutineName = tokenizer.identifier();
            String type = symbolTable.typeOf(className);
            if (type.equals("")){
                subroutineName = className + "." + subroutineName;
            }else {
                nArgs = 1;
                vmWriter.writePush(vmWriter.getSegment(symbolTable.kindOf(className)), symbolTable.indexOf(className));
                subroutineName = symbolTable.typeOf(className) + "." + subroutineName;
            }
            //'('
            tokenizer.advance();
            nArgs += compileExpressionList();
            //')'
            tokenizer.advance();
            vmWriter.writeCall(subroutineName,nArgs);
        }
    }

    /**
     * Compiles an expression
     */
    private void compileExpression(){
        compileTerm();
        while (tokenizer.hasMoreTokens()) {
            tokenizer.advance();
            if (tokenizer.tokenType() == JackTokenizer.symbol && tokenizer.isOp()) {
                String vmOperation = "";
                char symbol = tokenizer.symbol();
                if (symbol == '+') {
                    vmOperation = "add";
                } else if (symbol == '-') {
                    vmOperation = "sub";
                } else if (symbol == '*') {
                    vmOperation = "call Math.multiply 2";
                } else if (symbol == '/') {
                    vmOperation = "call Math.divide 2";
                } else if (symbol == '<') {
                    vmOperation = "lt";
                } else if (symbol == '>') {
                    vmOperation = "gt";
                } else if (symbol == '=') {
                    vmOperation = "eq";
                } else if (symbol == '&') {
                    vmOperation = "and";
                } else if (symbol == '|') {
                    vmOperation = "or";
                }
                compileTerm();
                vmWriter.getPrintWriter().print(vmOperation + "\n");
            } else {
                tokenizer.decreasePointer();
                break;
            }
        }
    }

    /**
     * Compiles a list of expressions
     */
    private int compileExpressionList(){
        int nArgs = 0;
        tokenizer.advance();
        if (tokenizer.tokenType() == JackTokenizer.symbol && tokenizer.symbol() == ')'){
            tokenizer.decreasePointer();
        }else {
            nArgs = 1;
            tokenizer.decreasePointer();
            compileExpression();
            while (tokenizer.hasMoreTokens()) {
                tokenizer.advance();
                if (tokenizer.tokenType() == JackTokenizer.symbol && tokenizer.symbol() == ',') {
                    compileExpression();
                    nArgs++;
                } else {
                    tokenizer.decreasePointer();
                    break;
                }
            }
        }
        return nArgs;
    }
}
