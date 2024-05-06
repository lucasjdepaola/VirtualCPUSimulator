
class MainMemory {
    public static Word[] DRAM = new Word[1024];

    /*
    Initialize memory, calls alloc
     */
    public static void init() {
        Alloc();
    }

    /*
    allocates memory for the DRAM, making sure that everything is initialized
     */
    private static void Alloc() {
        for (int i = 0; i < DRAM.length; i++) {
            DRAM[i] = new Word(new Bit[32]);
            DRAM[i].initBits();
        }
    }

    /*
    Read memory address, returns a new word
     */
    public static Word read(Word address) {
        return DRAM[indexOf(address)].clone();// give the value of the address
    }

    /*
    method to index the memory and return the proper address
     */
    private static int indexOf(Word address) {
        int addr = (int) address.getUnsigned();
        return addr > 1023 || addr < 0 ? 1023 : addr; // in case the number is above 1023
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

    /*
    writes the value to the address in memory
     */
    public static void write(Word address, Word value) {
        System.out.println("writing " + value.getUnsigned() + " to address" + address.getUnsigned());
        DRAM[indexOf(address)].setBits(value.getBits());
    }

    /*
    loops over string array and writes out value to memory starting at zero
     */
    public static void load(String[] data) {
        //load will be a process of simulated strings inside the dram starting with 0.
        //the strings should be 32 0's or ones, meaning that they are a string of "bits"
        int incrementor = 0;
        for (String dataString : data) {
            Bit[] dataBits = new Bit[32];
            for (int i = 0; i < 32; i++) {
                if(dataString.length() < 32) break;
                dataBits[i] = new Bit(dataString.charAt(i) == '1');
                //if the data string bit is 1, it will be true (or 1), otherwise false
            }
            DRAM[incrementor++].setBits(dataBits); // since memory should be initialized, we don't need to reinitialize
        }
    }
}
