import java.util.ArrayList;

class Parser {
    private ArrayList<Token> list;
    private String[] assembledCode;

    public String[] getAssembledCode() {
        return assembledCode;
    }

    public Parser(ArrayList<Token> list) {
        this.list = list;
    }

    public void Parse() {
        String[] statements = statements();
        for (int i = 0; i < statements.length; i++)
            if (statements[i].length() < 32)
                System.out.println("statement: " + i + ", is not equal to 32, which is " + statements[i]);
        assembledCode = statements;
    }

    private String[] statements() {
        var statements = new ArrayList<String>();
        do {
            while (matchAndRemove(Token.tokenType.NEWLINE) != null)
                ; // this handles the case for lines with sole comments
            String statement = statement();
            if (statement != null) statements.add(statement);
        } while (matchAndRemove(Token.tokenType.NEWLINE) != null);
        for (String statement : statements) System.out.println(statement);
        return statements.toArray(new String[statements.size()]);
    }

    private String statement() {
        String statement = "";
        String immediate = "";
        String function = "";
        String rd = "";
        String opcode = "";
        if (matchAndRemove(Token.tokenType.MATH) != null) {
            opcode += "000";
            function = math();
        } else if (matchAndRemove(Token.tokenType.BRANCH) != null) {
            opcode += "001";
            function = branch();
        } else if (matchAndRemove(Token.tokenType.HALT) != null) {
            return halt();
        } else if (matchAndRemove(Token.tokenType.CALL) != null) {
            opcode += "010";
            function += branch();
        } else if (matchAndRemove(Token.tokenType.PUSH) != null) {
            opcode += "011";
            function += math();
        } else if (matchAndRemove(Token.tokenType.LOAD) != null) {
            opcode += "100";
            function += "1110";// should not be math , should just be add
        } else if (matchAndRemove(Token.tokenType.STORE) != null) {
            opcode += "101";
            function += "1110";
        } else if (matchAndRemove(Token.tokenType.RETURN) != null) {
            // there is no case for this token.
        } else if (matchAndRemove(Token.tokenType.PEEK) != null) {
            // there is no case for this token.
        } else if (matchAndRemove(Token.tokenType.POP) != null) {
            opcode += "110";
            function += "0000";
        } else if (matchAndRemove(Token.tokenType.INTERRUPT) != null) {
            // TODO remove interrupt since there is no interrupt keyword
            opcode += "110";
        }

        String regString = "";
        Token register;
        Token num = null;
        int format = 0;
        int numCount = 0; // our register format depends on format, the number offsets the format
        Token[] tarr = new Token[3];
        boolean tflag = true;
        //it could be a number, or it could be a register variable
        while (true) {
            register = matchAndRemove(Token.tokenType.REGISTER);
            if (register == null) {
                num = matchAndRemove(Token.tokenType.NUMBER);
                if (num == null) break;
                numCount++;
            }
            tarr[format++] = register == null ? num : register;
        }
        if (format - numCount == 0) {
            //0r case
            immediate = "000000000000000000000000000";
            opcode += "00";
            if (opcode.length() == 2) opcode += "000"; // if it's a blank statement to end the program, ensure len is 32
            return immediate + opcode;
        } else if (format - numCount == 1) {
            //1r case should at least accumulate some sort of immediate value
            String immediateNumber = longIntegerToBinary(tarr[0].getNumber(), 18);
            // interpret values for immediate
            for (int i = immediateNumber.length(); i < 18; i++) immediate += "0";
            System.out.println(immediateNumber + ", is the immediate number in the parser");
            immediate += immediateNumber;
            opcode += "01";
            final String intToBinary = tarr[1] == null ? "00000" : IntegerToBinary(tarr[1].getNumber());
            return immediate + function + intToBinary + opcode;
        } else if (format - numCount == 2) {
            //2r case
            if (tarr[0].getToken() == Token.tokenType.NUMBER) {
                String immediateNumber = longIntegerToBinary(tarr[0].getNumber(), 13);
                for (int i = 0; i < 13 - 5; i++) immediate += "0";
                immediate += immediateNumber;
            } else
                immediate += "0000000000000";

            opcode += "10";
            return immediate + IntegerToBinary(tarr[0].getNumber()) + function + IntegerToBinary(tarr[1].getNumber()) + opcode;
        } else if (format - numCount == 3) {
            immediate = "00000000";
            opcode += "11";
            //3r case
            return immediate + IntegerToBinary(tarr[0].getNumber()) + IntegerToBinary(tarr[1].getNumber()) + function + IntegerToBinary(tarr[2].getNumber()) + opcode;
        }
        return "err";
    }

    private String math() {
        if (matchAndRemove(Token.tokenType.AND) != null) {
            return "1000";
        } else if (matchAndRemove(Token.tokenType.OR) != null) {
            return "1001";
        } else if (matchAndRemove(Token.tokenType.XOR) != null) {
            return "1010";
        } else if (matchAndRemove(Token.tokenType.NOT) != null) {
            return "1011";
        } else if (matchAndRemove(Token.tokenType.LEFTSHIFT) != null) {
            return "1100";
        } else if (matchAndRemove(Token.tokenType.RIGHTSHIFT) != null) {
            return "1101";
        } else if (matchAndRemove(Token.tokenType.ADD) != null) {
            return "1110";
        } else if (matchAndRemove(Token.tokenType.SUBTRACT) != null) {
            return "1111";
        } else if (matchAndRemove(Token.tokenType.MULTIPLY) != null) {
            return "0111";
        }
        return "err";
    }

    private String branch() {
        if (matchAndRemove(Token.tokenType.EQUAL) != null) {
            return "0000";
        } else if (matchAndRemove(Token.tokenType.UNEQUAL) != null) {
            return "0001";
        } else if (matchAndRemove(Token.tokenType.LESS) != null) {
            return "0010";
        } else if (matchAndRemove(Token.tokenType.GREATEROREQUAL) != null) {
            return "0011";
        } else if (matchAndRemove(Token.tokenType.LESSOREQUAL) != null) {
            return "0101";
        } else if (matchAndRemove(Token.tokenType.GREATER) != null) {
            return "0100";
        }
        return "err";
    }

    public String IntegerToBinary(int number) {
        int ptr = number;
        char[] register = new char[5];
        for (int i = 0; i < 5; i++) register[i] = '0';
        int size = 4;
        while (ptr != 0 && size >= 0) {
            register[size--] = ptr % 2 == 1 ? '1' : '0';
            ptr /= 2;
        }
        return String.valueOf(register);
    }

    public String longIntegerToBinary(int number, int length) {
        int ptr = number;
        char[] register = new char[length];
        for (int i = 0; i < length; i++) register[i] = '0';
        int size = length-1;
        while (ptr != 0) {
            register[size--] = ptr % 2 == 1 ? '1' : '0';
            ptr /= 2;
        }
        return String.valueOf(register);
    }

    private String halt() {
        // halt opcode which is all zeros
        return "00000000000000000000000000000000";
    }

    /* If the head of the list matches the parameter token, remove and return it, else return null */
    private Token matchAndRemove(Token.tokenType token) {
        return list.isEmpty() || token != list.getFirst().getToken() ? null : list.removeFirst();
    }

}