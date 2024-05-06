class ALU {

    public ALU(Word op1, Word op2) {
        this.op1 = op1;
        this.op2 = op2;
    }

    Word op1;
    Word op2;
    Word result = new Word(new Bit[32]);//initialize empty result

    /*
    takes a bit operator and determines which operation to perform. Uses built in functionality for logic
     */
    public void doOperation(Bit[] operation) {
        if (operation[0].and(operation[1].not()).and(operation[3].not()).getValue()) {
            and();
        } else if (operation[0].and(operation[1].not()).and(operation[2].not()).and(operation[3]).getValue()) {
            or();
        } else if (operation[0].and(operation[1].not()).and(operation[2]).and(operation[3].not()).getValue()) {
            xor();
        } else if (operation[0].and(operation[1].not()).and(operation[2]).and(operation[3]).getValue()) {
            not();
        } else if (operation[0].and(operation[1]).and(operation[2].not()).and(operation[3].not()).getValue()) {
            leftShift();
        } else if (operation[0].and(operation[1]).and(operation[2].not()).and(operation[3]).getValue()) {
            rightShift();
        } else if (operation[0].and(operation[1]).and(operation[2]).and(operation[3].not()).getValue()) {
            add2();
        } else if (operation[0].and(operation[1]).and(operation[2]).and(operation[3]).getValue()) {
            subtract2();
        } else if (operation[0].not().and(operation[1]).and(operation[2]).and(operation[3]).getValue()) {
            multiply2();
        }
    }

    private void and() {
        result.setBits(op1.and(op2).getBits());
    }

    private void or() {
        result.setBits(op1.or(op2).getBits());
    }

    private void xor() {
        result.setBits(op1.xor(op2).getBits());
    }

    private void not() {
        result.setBits(op1.not().getBits());
    }

    private void leftShift() {
        result.setBits(op1.leftShift(op2.getSigned()).getBits());
    }

    private void rightShift() {
        result.setBits(op1.rightShift(op2.getSigned()).getBits());
    }

    /*
    Since subtraction is addition of op2 negation, we can implement this easily with our adder done
     */
    private void subtract2() {
        Word op2neg = new Word(op2.getBits());
        result.setBits(add2(op1.getBits(), op2neg.TwosCompliment(op2neg).getBits()));
    }

    /*
    two operand bit addition. Returns a word instead of a bit array
     */
    public Word add2() {
        Word resultValue = new Word(new Bit[32]);
        Bit cin = new Bit(false);
        Bit cout;
        Bit s;
        for (int i = 31; i >= 0; i--) {
            s = new Bit(op1.getBit(i).xor(op2.getBit(i)).xor(cin).getValue());
            cout = new Bit(op1.getBit(i).and(op2.getBit(i)).or(op1.getBit(i).xor(op2.getBit(i)).and(cin)).getValue());
            resultValue.setBit(i, s);
            cin = cout;
        }
        result.setBits(resultValue.getBits());
        return resultValue;
    }

    /*
    for multiplication, we need a 4 operand adder. This adds 1 operand with the other twice, then combines the two
    with another add, all within the same loop to improve efficiency
     */
    public Word add4(Bit[] op1, Bit[] op2, Bit[] op3, Bit[] op4) {
        //need to somehow add twice, versus adding once, basically you add two times within the loop
        Word resultValue = new Word(new Bit[32]);
        Bit cin = new Bit(false);
        Bit cin2 = new Bit(false);
        Bit cin3 = new Bit(false);
        Bit cout, cout2, cout3;
        Bit s, s2, s3;
        for (int i = 31; i >= 0; i--) {
            s = op1[i].xor(op2[i]).xor(cin);
            cout = op1[i].and(op2[i]).or(op1[i].xor(op2[i]).and(cin));
            //first adder and carry
            s2 = op3[i].xor(op4[i]).xor(cin2);
            cout2 = op3[i].and(op4[i]).or(op3[i].xor(op4[i]).and(cin2));
            //second adder and carry
            s3 = s.xor(s2).xor(cin3);
            cout3 = s.and(s2).or(s.xor(s2).and(cin3));
            cin = cout;
            cin2 = cout2;
            cin3 = cout3;
            resultValue.setBit(i, s3);
        }
        return resultValue;
    }

    /*
    two operand bit addition. Uses a combination of xor, and, or bitwise to perform correctly
    returns a bit[] instead of a word
     */
    public Bit[] add2(Bit[] op1, Bit[] op2) {
        Word resultValue = new Word(new Bit[32]);
        Bit cin = new Bit(false);
        Bit cout;
        Bit s;
        for (int i = 31; i >= 0; i--) {
            s = new Bit(op1[i].xor(op2[i]).xor(cin).getValue());
            cout = new Bit(op1[i].and(op2[i]).or(op1[i].xor(op2[i]).and(cin)).getValue());
            resultValue.setBit(i, s);
            cin = cout;
        }
        return resultValue.getBits();
    }

    /*
    Bit multiplication, uses a mix of add4 and add2 to reduce the loop overhead
     */
    public void multiply2() {
        int shift = 0;
        Word[] tableBuffer = new Word[32];
        int bufferIndex = 0;
        for (int i = 31; i >= 0; i--) {
            if (op2.getBit(i).getValue()) {
                tableBuffer[bufferIndex++] = new Word(op1.getBits()).leftShift(shift);
            } else {
                Word zero = new Word(new Bit[32]);
                zero.initBits();
                tableBuffer[bufferIndex++] = zero;
            }
            //first multiplication
            shift++;
        }

        //round 1
        Word[] tableEight = new Word[8];
        bufferIndex = 0;
        for (int i = 0; i < 32; i += 4) {
            tableEight[bufferIndex++] = add4(tableBuffer[i].getBits(), tableBuffer[i + 1].getBits(), tableBuffer[i + 2].getBits(), tableBuffer[i + 3].getBits());
        }

        //round 2
        Word[] tableTwo = new Word[2];
        bufferIndex = 0;
        for (int i = 0; i < 8; i += 4) {
            tableTwo[bufferIndex++] = add4(tableEight[i].getBits(), tableEight[i + 1].getBits(), tableEight[i + 2].getBits(), tableEight[i + 3].getBits());
        }

        //round 3
        result.setBits(add2(tableTwo[0].getBits(), tableTwo[1].getBits()));
    }
}