class Word {
    private Bit[] bits;

    public Word(Bit[] bits) {
        this.bits = bits;
    }

    void initBits() {
        // in case the number is void or not fulfilled, we fill the numbers with false
        Bit[] b = new Bit[32];
        for (int i = 0; i < 32; i++) {
            b[i] = new Bit(false);
        }
        bits = b;
    }

    Bit getBit(int i) {
        return new Bit(bits[i].getValue()); // making sure not to send a direct reference to the object.
    }

    Bit[] getBits() {
        return bits;
    }

    //if we ever want to set our word with a new array of bits
    void setBits(Bit[] bits) {
        this.bits = bits;
    }

    Word and(Word other) {
        Bit[] returnBits = new Bit[32];
        for (int i = 0; i < 32; i++) {
            // iterating through 32, while accumulating the AND value of the two bits
            returnBits[i] = getBit(i).and(other.getBit(i));
        }
        return new Word(returnBits);
    }

    Word or(Word other) {
        Bit[] returnBits = new Bit[32];
        for (int i = 0; i < 32; i++) {
            // iterating through 32, while accumulating the OR value of the two bits
            returnBits[i] = getBit(i).or(other.getBit(i));
        }
        return new Word(returnBits);
    }

    Word xor(Word other) {
        Bit[] returnBits = new Bit[32];
        for (int i = 0; i < 32; i++) {
            // iterating through 32, while accumulating the XOR value of the two bits
            returnBits[i] = getBit(i).xor(other.getBit(i));
        }
        return new Word(returnBits);
    }

    Word toggle() {
        //setting all bits to their opposite values
        Word returnValue = new Word(bits);
        for (int i = 0; i < 32; i++) {
            returnValue.setBit(i, new Bit(!returnValue.getBit(i).getValue()));
        }
        return returnValue;
    }

    /*
    To convert the number negative, vice versa, we use twos compliment.
    This simply flips all bits, then adds one.
     */
    Word TwosCompliment(Word word) {
        Word returnValue = new Word(new Bit[32]);
        for (int i = 0; i < 32; i++) returnValue.setBit(i, word.getBit(i));
        returnValue.toggle();
        returnValue.addOne();
        return returnValue;
    }

    Word not() {
        Bit[] returnBits = new Bit[32];
        for (int i = 0; i < 32; i++) {
            // iterating through 32, while accumulating the NOT value of the two bits
            returnBits[i] = getBit(i).not();
        }
        return new Word(returnBits);
    }

    void setBit(int i, Bit value) {
        //setting an individual bit by its given index
        bits[i] = new Bit(value.getValue());
    }

    Word rightShift(int amount) {
        //shifting the bit array right by {amount} times
        Bit[] returnValue = new Bit[32];
        for (int j = 0; j < Math.min(32, amount); j++) {
            returnValue[j] = new Bit(false);
        }
        int offSet = 0;
        for (int i = Math.max(amount, 0); i < 32; i++) {
            //right shift means we can offset i by the int amount
            returnValue[i] = new Bit(bits[offSet++].getValue());
        }
        return new Word(returnValue);
    }

    Word leftShift(int amount) {
        //shifting the bit array left by {amount} times
        if (amount > 31) amount = 31; // max shift amount
        Bit[] returnValue = new Bit[32];
        int count = 0;
        for (int i = 31; i > 31 - amount; i--) {
            returnValue[i] = new Bit(false);
        }
        int offSet = amount;
        for (int x = 0; x <= 31 - amount; x++) {
            offSet = amount + x;
            returnValue[x] = new Bit(bits[offSet].getValue());
        }
        return new Word(returnValue);
    }

    long getUnsigned() {
        long returnValue = 0;
        Word bitsAsWord = new Word(bits);
        int pow = 0;
        for (int i = 31; i >= 0; i--) {
            // accumulate either 2^i if the current bit value is true, else accumulate zero.
            returnValue += bitsAsWord.getBit(i).getValue() ? (long) Math.pow(2, pow) : 0;
            pow++;
        }
        return returnValue;
    }

    int getSigned() {
        //means that our bits can contain a negative value, so we need to check for this before calculating
        Word word = new Word(bits);
        boolean signValue = isSigned(); //is signed also means "Does it have a sign?"
        if (signValue) {
            word = word.toggle();
            word.addOne(); // toggling (flipping all) then adding one.
        }
        int returnValue = 0;

        int pow = 0;
        for (int i = 31; i >= 0; i--) {
            returnValue += word.getBit(i).getValue() ? (int) Math.pow(2, pow) : 0;
            pow++;
        }
        // ternary to either return the negative or positive value of the binary
        return signValue ? returnValue * -1 : returnValue;
    }

    boolean isSigned() {
        return bits[0].getValue();//if the first bit is 1, then it has to be signed.
    }

    private void addOne() {
        //adding one primarily for the twos compliment signed algorithm
        for (int i = 31; i >= 0; i--) {
            if (bits[i].getValue()) {
                bits[i].clear();
            } else {
                bits[i].set();
                return;
            }
        }
    }

    void copy(Word other) {
        //copy a new word to our bit[32] member
        for (int i = 0; i < 32; i++) {
            setBit(i, other.getBit(i));
        }
    }

    void set(int value) {
        //this should be converting the Word to the value above.
        int valRef = value;
        Word setVoid = new Word(new Bit[32]);
        setVoid.initBits();
        int index = 31;
        while (valRef != 0) {
            //during the while loop, we divide our valref by 2, and create a new bit (true if mod%2 is 1, else false)
            setVoid.setBit(index--, (valRef % 2 == 0 ? new Bit(false) : new Bit(true)));
            valRef /= 2;
        }
        if (value < 0) {
            //if our number is negative, toggle and add one per twos complement.
            setVoid.toggle();
            setVoid.addOne();
        }
        bits = setVoid.getBits();// set bits to calculated value.
    }

    public String toString() {
        StringBuilder returnString = new StringBuilder().append("[");
        for (int i = 0; i < bits.length; i++)
            returnString.append(bits[i].toString()).append(i == bits.length - 1 ? "]" : ", ");
        return returnString.toString();
    }

    public Word clone() {
        Word newWord = new Word(new Bit[32]);
        for (int i = 0; i < 32; i++) {
            newWord.setBit(i, new Bit(bits[i].getValue()));
        }
        return newWord;
    }

    public Bit equals(Word word) {
        for (int i = 0; i < 32; i++) {
            if (bits[i].xor(word.getBit(i)).getValue()) {
                return new Bit(false);
            }
        }
        return new Bit(true);
    }
}