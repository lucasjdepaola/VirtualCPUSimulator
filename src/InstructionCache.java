
class InstructionCache {
    public static Word[] instructionCache = new Word[8];
    public static Word instructionAddr; // the address represented by the first of the 8 words in instructioncache
    public static Word[] L2one = new Word[8];
    public static Word L2oneAddr;
    public static Word[] L2two = new Word[8];
    public static Word L2twoAddr;
    public static Word[] L2three = new Word[8];
    public static Word L2threeAddr;
    public static Word[] L2four = new Word[8];
    public static Word L2fourAddr;

    public static void initCache() {
        instructionAddr = new Word(null);
        instructionAddr.initBits(); // starting at zero
        Word instructionAddrptr = instructionAddr.clone();
        for(int i =0; i < 8; i++) {
            instructionCache[i] = new Word(null);
            instructionCache[i].initBits();
            instructionCache[i] = MainMemory.read(instructionAddrptr);
            System.out.println(instructionCache[i].getUnsigned());
            increment(instructionAddrptr);
        }

        // initialize L2 cache
        L2oneAddr = instructionAddrptr.clone();
        for(int i = 0; i < 8; i++) {
            L2one[i] = new Word(null);
            L2one[i].initBits();
            L2one[i] = MainMemory.read(instructionAddrptr);
            increment(instructionAddrptr);
        }
        L2twoAddr = instructionAddrptr.clone();
        for(int i = 0; i < 8; i++) {
            L2two[i] = new Word(null);
            L2two[i].initBits();
            L2two[i] = MainMemory.read(instructionAddrptr);
            increment(instructionAddrptr);
        }
        L2threeAddr = instructionAddrptr.clone();
        for(int i = 0; i < 8; i++) {
            L2three[i] = new Word(null);
            L2three[i].initBits();
            L2three[i] = MainMemory.read(instructionAddrptr);
            increment(instructionAddrptr);
        }
        L2fourAddr = instructionAddrptr.clone();
        for(int i = 0; i < 8; i++) {
            L2four[i] = new Word(null);
            L2four[i].initBits();
            L2four[i] = MainMemory.read(instructionAddrptr);
            increment(instructionAddrptr);
        }
    }

    /*
    first part of the caching, simply using an instruction cache, when a miss happens, read from main memory
     */
    public static Word InstructionToMainRead(Word address) {
        Word instructionAddrClone = instructionAddr.clone(); // start from the lowest index in the instruction cache
        for(int i = 0; i < 8; i++) {
            if(instructionAddrClone.equals(address).getValue()) {
                System.out.println("Instruction cache hit.");
                Processor.currentClockCycle += 10; // cache hit
                return instructionCache[i];
            }
            increment(instructionAddrClone);
        }
        Processor.currentClockCycle += 350; // cache miss
        instructionAddr = address.clone();
        instructionAddrClone = address.clone(); // setting lowest to param address
        for(int i = 0; i < 8; i++) {
            instructionCache[i] = MainMemory.read(instructionAddrClone);
            increment(instructionAddrClone);
        }
        return instructionCache[0]; // new lowest value is equal to the target value
    }

    public static Word InstructionToL2ToMainRead(Word address) {
        // go from the instruction cache to the L2 cache to the main memory
        Word instructionAddrClone = instructionAddr.clone();
        for(Word instruction : instructionCache) {
            if(instructionAddrClone.equals(address).getValue()) {
                Processor.currentClockCycle += 10; // 10 if it hits the instruction cache
                return instruction;
            }
            increment(instructionAddrClone);
        }
        // moving onto the L2 cache
        instructionAddrClone = L2oneAddr.clone();
        for(Word L2 : L2one) {
            if(instructionAddrClone.equals(address).getValue()) {
                Processor.currentClockCycle += 20;
                instructionCache = L2one.clone();
                Processor.currentClockCycle += 50;
                return L2;
            }
        }
        // L2 one miss
        instructionAddrClone = L2twoAddr.clone();
        for(Word L2 : L2two) {
            if(instructionAddrClone.equals(address).getValue()) {
                Processor.currentClockCycle += 20;
                instructionCache = L2two.clone();
                Processor.currentClockCycle += 50;
                return L2;
            }
        }
        //L2 two miss
        instructionAddrClone = L2threeAddr.clone();
        for(Word L2 : L2three) {
            if(instructionAddrClone.equals(address).getValue()) {
                Processor.currentClockCycle += 20;
                instructionCache = L2three.clone();
                Processor.currentClockCycle += 50;
                return L2;
            }
        }
        // L2 three miss
        instructionAddrClone = L2fourAddr.clone();
        for(Word L2 : L2four) {
            if(instructionAddrClone.equals(address).getValue()) {
                Processor.currentClockCycle += 20;
                instructionCache = L2four.clone();
                Processor.currentClockCycle += 50;
                return L2;
            }
        }
        // L2 four miss
        UpdateL2(address);
        instructionCache = L2one.clone();
        instructionAddr = L2oneAddr.clone();
        return MainMemory.read(address);
    }

    public static void WriteThrough(Word address, Word value) {
        // write through with the values
        Processor.currentClockCycle += 50; // write through mechanism is 50 clock cycles
        Word addrptr = L2oneAddr.clone();
        for(int i = 0; i < 8; i++) {
            if(addrptr.equals(address).getValue()) {
                L2one[i] = value;
                MainMemory.write(addrptr, value);
                return;
            }
            increment(addrptr);
        }
        addrptr = L2twoAddr.clone();
        for(int i = 0; i < 8; i++) {
            if(addrptr.equals(address).getValue()) {
                L2two[i] = value;
                MainMemory.write(addrptr, value);
                return;
            }
            increment(addrptr);
        }
        addrptr = L2threeAddr.clone();
        for(int i = 0; i < 8; i++) {
            if(addrptr.equals(address).getValue()) {
                L2three[i] = value;
                MainMemory.write(addrptr, value);
                return;
            }
            increment(addrptr);
        }
        addrptr = L2fourAddr.clone();
        for(int i = 0; i < 8; i++) {
            if(addrptr.equals(address).getValue()) {
                L2four[i] = value;
                MainMemory.write(addrptr, value); // write through
                return;
            }
            increment(addrptr);
        }
        MainMemory.write(address, value); // write to main memory, pay 50 cycles still
    }

    /*
    cycle through all the caches and update with value relative to the address provided
     */
    private static void UpdateL2(Word address) {
        Processor.currentClockCycle += 350; // pay 350 for updating instruction/L2 and reading from main memory
        Word addrClone = address.clone();
        L2oneAddr = address.clone();
        for(int i = 0; i < 8; i++) {
            L2one[i] = MainMemory.read(addrClone);
            increment(addrClone);
        }
    }

    /*
    increments a word by 1 integer amount, particularly used for the program counter
     */
    private static void increment(Word word) {
        Word one = new Word(new Bit[32]);
        one.initBits();
        one.setBit(31, new Bit(true));
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
}