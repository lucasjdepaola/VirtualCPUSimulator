class Token {
    private tokenType token;
    private int number;

    public Token(tokenType token) {
        this.token = token;
        this.number = -1;
    }

    public tokenType getToken() {
        return token;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

    public enum tokenType {
        MATH, ADD, SUBTRACT, MULTIPLY, AND, OR, NOT, XOR, COPY, HALT, BRANCH, JUMP, CALL, PUSH, LOAD, RETURN, STORE,
        PEEK, POP, INTERRUPT, EQUAL, UNEQUAL, GREATER, LESS, GREATEROREQUAL, LESSOREQUAL, SHIFT, LEFTSHIFT, RIGHTSHIFT, _START,
        NEWLINE, REGISTER, NUMBER
    }

    public String toString() {
        String string = "";
        string += token.toString();
        if (token == tokenType.NUMBER || token == tokenType.REGISTER) string += "(" + number + ")";
        return string;
    }
}