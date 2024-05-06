
/*
300 cycles to access memory
10 cycles for multiplication
2 for the other math operations
register accesses are free
 */
class Processor {
    public static int currentClockCycle = 0;
    public final int MEMACCESS = 300, MULTIPLICATIONACCESS = 10, ALUACCESS = 2;
    private Word PC; // program counter
    private final Bit[] add = {new Bit(true), new Bit(true), new Bit(true), new Bit(false)};
    /* since the add bits are needed a frequent amount, it's better to store them inside the processor */
    final Word one = initOne();

    private Word initOne() {
        Word one = new Word(new Bit[32]);
        one.initBits();
        one.setBit(31, new Bit(true));
        return one;
    }

    private void initStackPointer() {
        SP = new Word(new Bit[32]);
        SP.set(1024);
    }

    private Word SP;
    private ALU alu;
    private static Word[] registers = new Word[32];

    public Word[] getRegisters() {
        return registers;
    }

    private final Bit halted = new Bit(false);

    public void setup(Word SP, Word PC) {//setup is similar to a constructor, per the rubric there is a setup method.
        this.SP = SP;//should be 1024
        initStackPointer();
        this.PC = new Word(new Bit[32]);
        PC.initBits();
        registers[0] = new Word(new Bit[32]);
        for (int i = 0; i < 32; i++) {
            registers[i] = new Word(null);
            registers[i].initBits();
            //initializing all registers to zero in the case of uninitialized registers being used
        }
    }

    /*
      Checks the halted bit, calls all 4 functions in the proper order (10)
     */
    public void run() {
        currentClockCycle = 0; // reset the static integer
        while (isNotHalted()) {
            fetch();
        }
    }

    /*
    gets the next instruction and increments on the PC
     */
    public void fetch() {
        if (checkNull(PC.getBits())) PC.initBits();
        System.out.println("running line: " + PC.getUnsigned());
        if (PC.getUnsigned() >= 1023) Halt();
        decode(memRead(PC));
        increment(PC);
    }

    /* return true if there exists a null bit inside the bit array */
    private boolean checkNull(Bit[] bits) {
        for (Bit bit : bits)
            if (bit == null)
                return true;
        return false;
    }

    /*
    increments a word by 1 integer amount, particularly used for the program counter
     */
    public void increment(Word word) {
        Bit cin = new Bit(false);
        Bit cout = new Bit(false);
        Bit s = new Bit(false);
        for (int i = 31; i >= 0; i--) {
            s.set(word.getBit(i).xor(one.getBit(i)).xor(cin).getValue());
            cout.set(word.getBit(i).and(one.getBit(i)).or(word.getBit(i).xor(one.getBit(i)).and(cin)).getValue());
            word.setBit(i, s);
            cin = cout;
        }
    }

    /*
    similar to increment except using twos compliment on one negating the value
     */
    public void decrement(Word word) {
        Word resultValue = new Word(new Bit[32]);
        Word one = new Word(new Bit[32]);
        one.initBits();
        one.setBit(31, new Bit(true));
        one = one.TwosCompliment(one);
        Bit cin = new Bit(false);
        Bit cout;
        Bit s;
        for (int i = 31; i >= 0; i--) {
            s = new Bit(word.getBit(i).xor(one.getBit(i)).xor(cin).getValue());
            cout = new Bit(word.getBit(i).and(one.getBit(i)).or(word.getBit(i).xor(one.getBit(i)).and(cin)).getValue());
            resultValue.setBit(i, s);
            cin = cout;
        }
        word.setBits(resultValue.getBits());
    }

    /*
    parse binary to specific instructions, which get executed in the execute() function
     */
    public void decode(Word address) {
        //look at the last 2 bits, which tells what format the assembly is in.
        Word format = new Word(new Bit[32]);
        format.initBits();
        Word op = new Word(new Bit[32]);
        op.initBits();
        format.setBit(30, address.getBit(30));
        format.setBit(31, address.getBit(31));
        address = address.rightShift(2);
        for (int i = 29; i < 32; i++) op.setBit(i, address.getBit(i));
        address = address.rightShift(3);
        if (zeroR(format.getBits()).getValue()) {
            Word immediate = new Word(null);
            immediate.initBits();
            for (int i = 32 - 27; i < 32; i++) {
                immediate.setBit(i, address.getBit(i));
            }
            execute(immediate, null, null, null, null, op, format);
        }
        Word rd = new Word(new Bit[32]);
        rd.initBits();
        for (int i = 32 - 5; i < 32; i++) rd.setBit(i, address.getBit(i));
        address = address.rightShift(5);
        Word function = new Word(new Bit[32]);
        function.initBits();
        for (int i = 32 - 4; i < 32; i++) function.setBit(i, address.getBit(i));
        address = address.rightShift(4);
        if (threeR(format.getBits()).getValue()) {
            //3R Get the Rs and Rd values from the registers and the immediate value from the instruction.
            Word rs2 = new Word(new Bit[32]);
            rs2.initBits();
            Word rs1 = new Word(new Bit[32]);
            rs1.initBits();
            Word immediate = new Word(new Bit[32]);
            immediate.initBits();
            for (int i = 32 - 5; i < 32; i++) rs2.setBit(i, address.getBit(i));
            address = address.rightShift(5);
            for (int i = 32 - 5; i < 32; i++) rs1.setBit(i, address.getBit(i));
            address = address.rightShift(5);
            for (int i = 32 - 8; i < 32; i++) immediate.setBit(i, address.getBit(i));
            execute(immediate, retrieve(rs1), retrieve(rs2), function, rd, op, format);
        } else if (twoR(format.getBits()).getValue()) {
            //2R  Get the Rd value from the registers and the immediate value from the instruction.
            Word rs1 = new Word(new Bit[32]);
            rs1.initBits();
            Word immediate = new Word(new Bit[32]); // 13
            immediate.initBits();
            for (int i = 32 - 5; i < 32; i++) rs1.setBit(i, address.getBit(i));
            address = address.rightShift(5);
            for (int i = 32 - 13; i < 32; i++) immediate.setBit(i, address.getBit(i));
            System.out.println(immediate.getUnsigned() + " Immediate");
            execute(immediate, retrieve(rs1), null, function, rd, op, format);
        } else if (oneR(format.getBits()).getValue()) {
            //1R
            Word immediate = new Word(new Bit[32]);
            immediate.initBits();
            for (int i = 32 - 18; i < 32; i++) immediate.setBit(i, address.getBit(i));
            System.out.println(immediate.getUnsigned() + " is the immediate");
            execute(immediate, null, null, function, rd, op, format);
        }
    }

    public void execute(Word immediate, Word rs1, Word rs2, Word function, Word rd, Word opcode, Word format) {
        Bit[] functionOp = new Bit[4];
        if (function != null) {
            for (int i = 32 - 4; i < 32; i++) functionOp[i - 28] = function.getBit(i);
            // initializing the function bits, as long as it isn't 0R, which the null check is for
        }

        /* finding the operation via the opcode, then calling the appropriate function */
        if (mathop(opcode).getValue()) {
            /* math operation */
            mathOperation(immediate, rs1, rs2, functionOp, rd, opcode, format);
        } else if (branchop(opcode).getValue()) {
            /* branch operation */
            branchOperation(immediate, rs1, rs2, functionOp, rd, opcode, format);
        } else if (callop(opcode).getValue()) {
            /* call operation */
            callOperation(immediate, rs1, rs2, functionOp, rd, opcode, format);
        } else if (pushop(opcode).getValue()) {
            /* push operation */
            pushOperation(immediate, rs1, rs2, functionOp, rd, opcode, format);
        } else if (loadop(opcode).getValue()) {
            /* load operation */
            loadOperation(immediate, rs1, rs2, functionOp, rd, opcode, format);
        } else if (storeop(opcode).getValue()) {
            /* store operation */
            storeOperation(immediate, rs1, rs2, functionOp, rd, opcode, format);
        } else if (popop(opcode).getValue()) {
            /* pop operation */
            popOperation(immediate, rs1, rs2, functionOp, rd, opcode, format);
        }
    }

    private Bit multiplicationOp(Bit[] functionOp) {
        if (checkNull(functionOp)) return new Bit(false);
        return functionOp[0].not().and(functionOp[1]).and(functionOp[2]).and(functionOp[3]);
    }

    private void mathOperation(Word immediate, Word rs1, Word rs2, Bit[] functionOp, Word rd, Word opcode, Word format) {
        System.out.println("MATH OPERATION");
        if (multiplicationOp(functionOp).getValue())
            currentClockCycle += MULTIPLICATIONACCESS;
        else
            currentClockCycle += ALUACCESS;

        if (zeroR(format.getBits()).getValue()) {
            Halt(); // 0R halts on a math operation instruction
        } else if (threeR(format.getBits()).getValue()) {
            //3R case.
            alu = new ALU(rs1, rs2);
            alu.doOperation(functionOp);
            store(rd, alu.result);
        } else if (twoR(format.getBits()).getValue()) {
            // 2R RD <- RD MOP RS
            alu = new ALU(retrieve(rd), rs1);
            alu.doOperation(functionOp);
            System.out.println("now doing " + retrieve(rd).getSigned() + " mop " + rs1.getSigned());
            store(rd, alu.result);
        } else if (oneR(format.getBits()).getValue()) {
            // 1R COPY RD <- imm
            store(rd, immediate);
        }
    }

    private void branchOperation(Word immediate, Word rs1, Word rs2, Bit[] functionOp, Word rd, Word opcode, Word format) {
        if (zeroR(format.getBits()).getValue()) {
            PC = immediate; // jump to the immediate value provided
        } else if (threeR(format.getBits()).getValue()) {
            Word result = branchFunction(rs1, rs2, functionOp);
            if (result.getBit(31).getValue()) {
                alu = new ALU(PC, immediate);
                alu.doOperation(add);
                PC = alu.result;
            }
        } else if (twoR(format.getBits()).getValue()) {
            Word result = branchFunction(rs1, rd, functionOp);
            if (result.getBit(31).getValue()) {
                alu = new ALU(PC, immediate);
                alu.doOperation(add);
                PC = alu.result;
            }
        } else if (oneR(format.getBits()).getValue()) {
            alu = new ALU(PC, immediate);
            alu.doOperation(add);
            PC = alu.result;
        }
    }

    private void callOperation(Word immediate, Word rs1, Word rs2, Bit[] functionOp, Word rd, Word opcode, Word format) {
        if (zeroR(format.getBits()).getValue()) {
            push(PC);
            PC = immediate;
        } else if (oneR(format.getBits()).getValue()) {
            push(PC);
            alu = new ALU(rd, immediate);
            alu.doOperation(add);
            PC = alu.result;
        } else if (twoR(format.getBits()).getValue()) {
            Word operation = branchFunction(rs1, rd, functionOp);
            if (operation.getBit(31).getValue()) {
                // if the bool operation is successful, push PC, set PC to imm+pc
                push(PC);
                alu = new ALU(PC, immediate);
                alu.doOperation(add);
                PC = alu.result;
                System.out.println("program counter should be equal to imm+PC");
            }
        } else if (threeR(format.getBits()).getValue()) {
            Word operation = branchFunction(rs1, rs2, functionOp);
            if (operation.getBit(31).getValue()) {
                push(PC);
                alu = new ALU(retrieve(rd), immediate);
                alu.doOperation(add);
                PC = alu.result;
                System.out.println("PC is now equal to: " + alu.result.getUnsigned());
            }
        }
    }

    private void pushOperation(Word immediate, Word rs1, Word rs2, Bit[] functionOp, Word rd, Word opcode, Word format) {
        if (zeroR(format.getBits()).getValue()) {
        } else if (oneR(format.getBits()).getValue()) {
            ALU operation = new ALU(rd, immediate);
            operation.doOperation(functionOp);
            push(operation.result);
            System.out.println("1R operation pushed onto the stack");
        } else if (twoR(format.getBits()).getValue()) {
            ALU operation = new ALU(rd, rs1);
            operation.doOperation(functionOp);
            push(operation.result);
            System.out.println("2R operation pushed onto the stack");
        } else if (threeR(format.getBits()).getValue()) {
            ALU operation = new ALU(rs1, rs2);
            operation.doOperation(functionOp);
            push(operation.result);
            System.out.println("3R operation pushed onto the stack");
        }
    }

    private void loadOperation(Word immediate, Word rs1, Word rs2, Bit[] functionOp, Word rd, Word opcode, Word format) {
        if (zeroR(format.getBits()).getValue()) {
            // pop function on the PC, setting PC equal to the stack pointer then incrementing the stack pointer
            PC = SP;
            increment(SP);
        } else if (oneR(format.getBits()).getValue()) {
            ALU operation = new ALU(rd, immediate);
            operation.doOperation(add);
            store(rd, memRead(operation.result));
            System.out.println("loading 1R of rd + immediate into rd");
        } else if (twoR(format.getBits()).getValue()) {
            ALU operation = new ALU(rs1, immediate);
            operation.doOperation(add);
            store(rd, memRead(operation.result));
            System.out.println("loading 2R of rs1 + immediate into rd");
        } else if (threeR(format.getBits()).getValue()) {
            ALU operation = new ALU(rs1, rs2);
            operation.doOperation(add);
            store(rd, memRead(operation.result));
            System.out.println("loading 3R of rs1 + rs2 into rd");
        }
    }

    private void storeOperation(Word immediate, Word rs1, Word rs2, Bit[] functionOp, Word rd, Word opcode, Word format) {
        if (zeroR(format.getBits()).getValue()) {
        } else if (oneR(format.getBits()).getValue()) {
            memWrite(retrieve(rd), immediate);
            System.out.println("1R storing immediate into rd");
        } else if (twoR(format.getBits()).getValue()) {
            ALU mop = new ALU(retrieve(rd), immediate);
            mop.doOperation(add);
            memWrite(mop.result, rs1);
            System.out.println("2R storing rs1 into rd mop imm");
        } else if (threeR(format.getBits()).getValue()) {
            ALU mop = new ALU(retrieve(rd), rs1);
            mop.doOperation(add);
            memWrite(mop.result, rs2);
            System.out.println("3R storing rs2 into rd mop rs1");
        }
    }


    private void popOperation(Word immediate, Word rs1, Word rs2, Bit[] functionOp, Word rd, Word opcode, Word format) {
        if (zeroR(format.getBits()).getValue()) {
        } else if (oneR(format.getBits()).getValue()) {
            increment(SP);
            store(rd, memRead(SP));
            System.out.println("1R pop, incrementing stack pointer and storing mem[sp] into rd");
        } else if (twoR(format.getBits()).getValue()) {
            alu = new ALU(rs1, immediate);
            alu.doOperation(add);
            store(rd, peek(alu.result));
            System.out.println("2R pop, storing peek(rs1 mop imm) into rd");
        } else if (threeR(format.getBits()).getValue()) {
            alu = new ALU(rs1, rs2);
            alu.doOperation(add);
            store(rd, peek(alu.result));
            System.out.println("3R pop, storing peek(rs1 mop rs2) into rd");
        }
    }

    /*
    push decrements the stack pointer and writes the value to main memory
     */
    private void push(Word value) {
        decrement(SP);
        memWrite(SP, value);
    }

    /*
    peek function which subtracts the stack pointer from the given address
     */
    private Word peek(Word rs) {
        alu = new ALU(SP, rs);
        Bit[] subtraction = {new Bit(true), new Bit(true), new Bit(true), new Bit(true)};
        alu.doOperation(subtraction);
        return memRead(alu.result);
    }

    /*
    FOR THE FORMATTING FUNCTIONS BELOW
    0R -> 00 or, (NOT arr[0]) AND (NOT arr[0])
    1R -> 01 or, (NOT arr[0]) AND (arr[0])
    2R -> 10 or, (arr[0]) AND (NOT arr[1])
    3R -> 11 or, (arr[0]) AND (arr[1])
     */

    private Bit zeroR(Bit[] format) {
        return format[30].not().and(format[31].not());
    }

    private Bit oneR(Bit[] format) {
        return format[30].not().and(format[31]);
    }

    private Bit twoR(Bit[] format) {
        return format[30].and(format[31].not());
    }

    private Bit threeR(Bit[] format) {
        return format[30].and(format[31]);
    }


    /* opcode functions based on the given opcode */

    private Bit mathop(Word opcode) {
        return opcode.getBit(29).not().and(opcode.getBit(30).not()).and(opcode.getBit(31).not());
    }

    private Bit branchop(Word opcode) {
        return opcode.getBit(29).not().and(opcode.getBit(30).not()).and(opcode.getBit(31));
    }

    private Bit callop(Word opcode) {
        return opcode.getBit(29).not().and(opcode.getBit(30)).and(opcode.getBit(31).not());
    }

    private Bit pushop(Word opcode) {
        return opcode.getBit(29).not().and(opcode.getBit(30)).and(opcode.getBit(31));
    }

    private Bit loadop(Word opcode) {
        return opcode.getBit(29).and(opcode.getBit(30).not()).and(opcode.getBit(31).not());
    }

    private Bit storeop(Word opcode) {
        return opcode.getBit(29).and(opcode.getBit(30).not()).and(opcode.getBit(31));
    }

    private Bit popop(Word opcode) {
        return opcode.getBit(29).and(opcode.getBit(30)).and(opcode.getBit(31).not());
    }

    /*
    store a word inside a register
     */
    public void store(Word rd, Word value) {
        System.out.println("Storing value: " + value.getUnsigned() + ", inside of rd: " + rd.getUnsigned());
        if (isZero(rd).getValue()) return; // cannot write to R0
        registers[indexOf(rd)] = value;
    }

    /*
    find the register value of the rd by incrementing from zero until the bits are equal, then return the value
     */
    private Word retrieve(Word rd) {
        return registers[indexOf(rd)];
    }

    /*
    method to index the memory and return the proper address
    O(n) time complexity
     */
    public int indexOf(Word rd) { // making this public only for testing purposes
        Bit[] rdValues = {rd.getBit(31 - 4), rd.getBit(31 - 3), rd.getBit(31 - 2), rd.getBit(31 - 1), rd.getBit(31)};
        if (rdValues[0].not().and(rdValues[1].not()).and(rdValues[2].not()).and(rdValues[3].not()).and(rdValues[4].not()).getValue()) {
            return 0;
        } else if (rdValues[0].not().and(rdValues[1].not()).and(rdValues[2].not()).and(rdValues[3].not()).and(rdValues[4]).getValue()) {
            return 1;
        } else if (rdValues[0].not().and(rdValues[1].not()).and(rdValues[2].not()).and(rdValues[3]).and(rdValues[4].not()).getValue()) {
            return 2;
        } else if (rdValues[0].not().and(rdValues[1].not()).and(rdValues[2].not()).and(rdValues[3]).and(rdValues[4]).getValue()) {
            return 3;
        } else if (rdValues[0].not().and(rdValues[1].not()).and(rdValues[2]).and(rdValues[3].not()).and(rdValues[4].not()).getValue()) {
            return 4;
        } else if (rdValues[0].not().and(rdValues[1].not()).and(rdValues[2]).and(rdValues[3].not()).and(rdValues[4]).getValue()) {
            return 5;
        } else if (rdValues[0].not().and(rdValues[1].not()).and(rdValues[2]).and(rdValues[3]).and(rdValues[4].not()).getValue()) {
            return 6;
        } else if (rdValues[0].not().and(rdValues[1].not()).and(rdValues[2]).and(rdValues[3]).and(rdValues[4]).getValue()) {
            return 7;
        } else if (rdValues[0].not().and(rdValues[1]).and(rdValues[2].not()).and(rdValues[3].not()).and(rdValues[4].not()).getValue()) {
            return 8;
        } else if (rdValues[0].not().and(rdValues[1]).and(rdValues[2].not()).and(rdValues[3].not()).and(rdValues[4]).getValue()) {
            return 9;
        } else if (rdValues[0].not().and(rdValues[1]).and(rdValues[2].not()).and(rdValues[3]).and(rdValues[4].not()).getValue()) {
            return 10;
        } else if (rdValues[0].not().and(rdValues[1]).and(rdValues[2].not()).and(rdValues[3]).and(rdValues[4]).getValue()) {
            return 11;
        } else if (rdValues[0].not().and(rdValues[1]).and(rdValues[2]).and(rdValues[3].not()).and(rdValues[4].not()).getValue()) {
            return 12;
        } else if (rdValues[0].not().and(rdValues[1]).and(rdValues[2]).and(rdValues[3].not()).and(rdValues[4]).getValue()) {
            return 13;
        } else if (rdValues[0].not().and(rdValues[1]).and(rdValues[2]).and(rdValues[3]).and(rdValues[4].not()).getValue()) {
            return 14;
        } else if (rdValues[0].not().and(rdValues[1]).and(rdValues[2]).and(rdValues[3]).and(rdValues[4]).getValue()) {
            return 15;
        } else if (rdValues[0].and(rdValues[1].not()).and(rdValues[2].not()).and(rdValues[3].not()).and(rdValues[4].not()).getValue()) {
            return 16;
        } else if (rdValues[0].and(rdValues[1].not()).and(rdValues[2].not()).and(rdValues[3].not()).and(rdValues[4]).getValue()) {
            return 17;
        } else if (rdValues[0].and(rdValues[1].not()).and(rdValues[2].not()).and(rdValues[3]).and(rdValues[4].not()).getValue()) {
            return 18;
        } else if (rdValues[0].and(rdValues[1].not()).and(rdValues[2].not()).and(rdValues[3]).and(rdValues[4]).getValue()) {
            return 19;
        } else if (rdValues[0].and(rdValues[1].not()).and(rdValues[2]).and(rdValues[3].not()).and(rdValues[4].not()).getValue()) {
            return 20;
        } else if (rdValues[0].and(rdValues[1].not()).and(rdValues[2]).and(rdValues[3].not()).and(rdValues[4]).getValue()) {
            return 21;
        } else if (rdValues[0].and(rdValues[1].not()).and(rdValues[2]).and(rdValues[3]).and(rdValues[4].not()).getValue()) {
            return 22;
        } else if (rdValues[0].and(rdValues[1].not()).and(rdValues[2]).and(rdValues[3]).and(rdValues[4]).getValue()) {
            return 23;
        } else if (rdValues[0].and(rdValues[1]).and(rdValues[2].not()).and(rdValues[3].not()).and(rdValues[4].not()).getValue()) {
            return 24;
        } else if (rdValues[0].and(rdValues[1]).and(rdValues[2].not()).and(rdValues[3].not()).and(rdValues[4]).getValue()) {
            return 25;
        } else if (rdValues[0].and(rdValues[1]).and(rdValues[2].not()).and(rdValues[3]).and(rdValues[4].not()).getValue()) {
            return 26;
        } else if (rdValues[0].and(rdValues[1]).and(rdValues[2].not()).and(rdValues[3]).and(rdValues[4]).getValue()) {
            return 27;
        } else if (rdValues[0].and(rdValues[1]).and(rdValues[2]).and(rdValues[3].not()).and(rdValues[4].not()).getValue()) {
            return 28;
        } else if (rdValues[0].and(rdValues[1]).and(rdValues[2]).and(rdValues[3].not()).and(rdValues[4]).getValue()) {
            return 29;
        } else if (rdValues[0].and(rdValues[1]).and(rdValues[2]).and(rdValues[3]).and(rdValues[4].not()).getValue()) {
            return 30;
        } else if (rdValues[0].and(rdValues[1]).and(rdValues[2]).and(rdValues[3]).and(rdValues[4]).getValue()) {
            return 31;
        }
        return 1; // if the indexed is greater than 32, return a non-breaking value
    }

    /*
    return the opposite of the halted bit
     */
    private boolean isNotHalted() {
        return halted.not().getValue();
    }

    /*
    set the halted bit to true, which will end the fetch loop
     */
    private void Halt() {
        halted.set(true);
        System.out.println("Program has halted, total clock cycle: " + currentClockCycle);
        currentClockCycle = 0;
    }

    /*
    performing branch operations, returns a word with value 1 if condition is fulfilled
     */
    private Word branchFunction(Word op1, Word op2, Bit[] functionOp) {
        Bit resultBit = new Bit(false);
        if (functionOp[0].not().and(functionOp[1].not().and(functionOp[2].not()).and(functionOp[3].not())).getValue()) {
            //equals case
            Bit equals = equals(op1, op2);
            resultBit.set(equals.getValue());
        } else if (functionOp[0].not().and(functionOp[1].not().and(functionOp[2].not()).and(functionOp[3])).getValue()) {
            //not equals case
            Bit notEqual = equals(op1, op2).not();
            resultBit.set(notEqual.getValue());
        } else if (functionOp[0].not().and(functionOp[1].not().and(functionOp[2]).and(functionOp[3].not())).getValue()) {
            //less than case
            Bit lessThan = lessThan(op1, op2);
            resultBit.set(lessThan.getValue());
        } else if (functionOp[0].not().and(functionOp[1].not().and(functionOp[2]).and(functionOp[3])).getValue()) {
            //geq case
            Bit greaterOrEqual = greaterOrEqual(op1, op2);
            resultBit.set(greaterOrEqual.getValue());
        } else if (functionOp[0].not().and(functionOp[1].and(functionOp[2].not()).and(functionOp[3].not())).getValue()) {
            //greater than case
            Bit greaterThan = greaterThan(op1, op2);
            resultBit.set(greaterThan.getValue());
        } else if (functionOp[0].not().and(functionOp[1].and(functionOp[2].not()).and(functionOp[3])).getValue()) {
            //leq case
            Bit lessOrEqual = lessOrEqual(op1, op2);
            resultBit.set(lessOrEqual.getValue());
        }
        Word result = new Word(null);
        result.initBits();
        result.setBit(31, resultBit);
        System.out.println("the condition inside branch is " + (resultBit.getValue() ? "true" : "false"));
        return result;
    }

    /*
    Subtracting then seeing if zero
     */
    private Bit equals(Word op1, Word op2) {
        alu = new ALU(op1, op2);
        Bit[] subtraction = {new Bit(true), new Bit(true), new Bit(true), new Bit(true)};
        alu.doOperation(subtraction);
        return isZero(alu.result);
    }

    private Bit isZero(Word word) {
        for (Bit bit : word.getBits()) {
            if (bit.getValue())
                return new Bit(false);
        }
        return new Bit(true);
    }

    /*
    return our greater than function, then the equals function
     */
    private Bit greaterOrEqual(Word op1, Word op2) {
        return greaterThan(op1, op2).or(equals(op1, op2));
    }

    /*
    iterate backwards, if op1 has a bit set to one, and op2 doesn't, then it's certain that op1 is greater than op2
     */
    private Bit greaterThan(Word op1, Word op2) {
        for (int i = 0; i < 32; i++) {
            if (op1.getBit(i).and(op2.getBit(i).not()).getValue()) {
                return new Bit(true);
            } else if (op2.getBit(i).and(op1.getBit(i).not()).getValue()) {
                return new Bit(false);
            }
        }
        return new Bit(false);
    }

    /*
    leveraging the greaterThan function, we can return it, then get the opposite value
     */
    private Bit lessThan(Word op1, Word op2) {
        return greaterThan(op1, op2).not();
    }

    /*
    leveraging the greaterThan function and the equals function, we can return
    (NOT greaterThan) OR equals
     */
    private Bit lessOrEqual(Word op1, Word op2) {
        return greaterThan(op1, op2).not().or(equals(op1, op2));
    }

    private Word memRead(Word address) {
        /* increment clock cycle and return the value */
//        return InstructionCache.InstructionToMainRead(address);
        return InstructionCache.InstructionToL2ToMainRead(address);
//        return MainMemory.read(address);
    }

    private void memWrite(Word address, Word value) {
        // instruction cache instructiontomainwrite(address)
        // instruction cache instructiontomaintol2write(address)
//        MainMemory.write(address, value);
        InstructionCache.WriteThrough(address, value);
    }
}