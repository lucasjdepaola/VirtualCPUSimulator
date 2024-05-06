import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

class Lexer {
    private String code;
    private HashMap<String, Token.tokenType> tokenMap = new HashMap<>();
    private ArrayList<Token> lexTokens = new ArrayList<>();
    private final Token token = new Token(Token.tokenType._START);

    public Lexer(String code) {
        this.code = code;
        InitializeMap();
    }

    public void lex() {
        String tokenString = "";
        for (int i = 0; i < code.length(); i++) {
            if (!isLetter(code.charAt(i))) ;
            if (code.charAt(i) == '\n') {
                lexTokens.add(new Token(Token.tokenType.NEWLINE));
            }
            if (code.charAt(i) != ' ' && code.charAt(i) != '\n')
                tokenString += code.charAt(i);
            if (tokenMap.containsKey(tokenString)) {
                lexTokens.add(new Token(tokenMap.get(tokenString)));
                tokenString = "";
            } else if (tokenString.equals("R")) {
                //register case
                String regNumber = "";
                while (Character.isDigit(peek(i++))) {
                    regNumber += code.charAt(i);
                }
                Token registerToken = new Token(Token.tokenType.REGISTER);
                registerToken.setNumber(StringtoInt(regNumber));
                lexTokens.add(registerToken);
                tokenString = "";
            }

            if (code.charAt(i) == ' ') {
                if (isNumber(tokenString)) {
                    Token number = new Token(Token.tokenType.NUMBER);
                    number.setNumber(Integer.parseInt(tokenString));
                    lexTokens.add(number);
                    tokenString = "";
                }
            } else if (code.charAt(i) == '\n') {
                lexTokens.add(new Token(Token.tokenType.NEWLINE));
            }
            else if(code.charAt(i) == ';') {
                // handle the comment case
                while(peek(i++) != '\n') {}// ignore all comment characters
                tokenString = "";
                lexTokens.add(new Token(Token.tokenType.NEWLINE));
            }
        }
    }

    //to avoid copying the entire code every function call, using the length value instead.
    private char peek(int index) {
        return index > code.length() - 2 ? code.charAt(index) : code.charAt(index + 1);
    }

    private boolean isLetter(char c) {
        return Character.isLetter(c);
    }

    private boolean isNumber(String numberString) {
        return Pattern.matches("-?[0-9]+", numberString);
        // zero or more dashes, one or more numbers
    }

    private void InitializeMap() {
        tokenMap.put("test", Token.tokenType.ADD);
        tokenMap.put("math", Token.tokenType.MATH);
        tokenMap.put("add", Token.tokenType.ADD);
        tokenMap.put("subtract", Token.tokenType.SUBTRACT);
        tokenMap.put("multiply", Token.tokenType.MULTIPLY);
        tokenMap.put("branch", Token.tokenType.BRANCH);
        tokenMap.put("halt", Token.tokenType.HALT);
        tokenMap.put("copy", Token.tokenType.COPY);
        tokenMap.put("jump", Token.tokenType.JUMP);
        tokenMap.put("call", Token.tokenType.CALL);
        tokenMap.put("push", Token.tokenType.PUSH);
        tokenMap.put("pop", Token.tokenType.POP);
        tokenMap.put("load", Token.tokenType.LOAD);
        tokenMap.put("store", Token.tokenType.STORE);
        tokenMap.put("return", Token.tokenType.RETURN);
        tokenMap.put("peek", Token.tokenType.PEEK);
        tokenMap.put("interrupt", Token.tokenType.INTERRUPT);
        tokenMap.put("or", Token.tokenType.OR);
        tokenMap.put("not", Token.tokenType.NOT);
        tokenMap.put("xor", Token.tokenType.XOR);
        tokenMap.put("eq", Token.tokenType.EQUAL);
        tokenMap.put("neq", Token.tokenType.UNEQUAL);
        tokenMap.put("gt", Token.tokenType.GREATER);
        tokenMap.put("lt", Token.tokenType.LESS);
        tokenMap.put("ge", Token.tokenType.GREATEROREQUAL);
        tokenMap.put("le", Token.tokenType.LESSOREQUAL);
        tokenMap.put("shift", Token.tokenType.SHIFT);
        tokenMap.put("ls", Token.tokenType.LEFTSHIFT);
        tokenMap.put("rs", Token.tokenType.RIGHTSHIFT);
        tokenMap.put("and", Token.tokenType.AND);
    }

    public ArrayList<Token> getList() {
        return lexTokens;
    }

    private int StringtoInt(String registerNumber) {
        return Integer.parseInt(registerNumber);
    }

    public String toString() {
        String tokenString = "";
        for (Token element : lexTokens) {
            tokenString += element + " ";
            if (element.getToken() == Token.tokenType.NEWLINE) {
                tokenString += "\n";
            }
        }
        return tokenString;
    }
}