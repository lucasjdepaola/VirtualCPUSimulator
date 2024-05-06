/*
(TODO) IMPORTANT: the processor is mainly tested using programs written using the assembler, which is assignment 6
the unit test for manual written opcode is testMemoryAndProcessor()
//the first assignment 4 I turned in was just that, but after realizing I couldn't use getUnsigned()
//for accessing registers, I had to come back to this assignment and fix it. The code I'm submitting
//is much more than assignment 4, which unfortunately there isn't an easy way for me to fix.
  */
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;

public class UnitTests {
    @Test
    public void testBit() {
        //using these two bits as default test bits, one true, one false.
        Bit falseBit = new Bit(false);
        Bit trueBit = new Bit(true);

        //subtest 1, true AND true, should yield true.
        Bit andTrueBit = new Bit(trueBit.and(trueBit).getValue());
        assertEquals(andTrueBit.getValue(), true);

        //subtest 2, false AND true, and true AND false should both yield false.
        Bit andFalseBit = new Bit(falseBit.and(trueBit).getValue());
        assertEquals(andFalseBit.getValue(), false);
        Bit andTrueFalse = new Bit(trueBit.and(falseBit).getValue());
        assertEquals(andTrueFalse.getValue(), false);

        //subtest 3, false AND false, should yield false.
        Bit falseFalseAndBit = new Bit(falseBit.and(falseBit).getValue());
        assertEquals(falseFalseAndBit.getValue(), false);

        //moving on to OR
        //subtest 4, true OR true should yield true.
        Bit orTrueTrueBit = new Bit(trueBit.or(trueBit).getValue());
        assertEquals(orTrueTrueBit.getValue(), true);

        //subtest 5 false OR true, and true OR false, should both yield true.
        Bit orTrueFalseBit = new Bit(trueBit.or(falseBit).getValue());
        assertEquals(orTrueFalseBit.getValue(), true);
        Bit orFalseTrueBit = new Bit(falseBit.or(trueBit).getValue());
        assertEquals(orFalseTrueBit.getValue(), true);

        //subtest 6 false OR false should yield false
        Bit orFalseFalseBit = new Bit(falseBit.or(falseBit).getValue());
        assertEquals(orFalseFalseBit.getValue(), false);

        //moving onto XOR
        //subtest 7 true XOR false, and false XOR true should both yield true
        Bit xorTrueFalseBit = new Bit(trueBit.xor(falseBit).getValue());
        assertEquals(xorTrueFalseBit.getValue(), true);
        Bit xorFalseTrueBit = new Bit(falseBit.xor(trueBit).getValue());
        assertEquals(xorFalseTrueBit.getValue(), true);

        //subtest 8 true XOR true should yield false
        Bit xorTrueTrueBit = new Bit(trueBit.xor(trueBit).getValue());
        assertEquals(xorTrueTrueBit.getValue(), false);

        //subtest 9 false XOR false should yield false
        Bit xorFalseFalseBit = new Bit(falseBit.xor(falseBit).getValue());
        assertEquals(xorFalseFalseBit.getValue(), false);

        //moving onto set
        //subtest 10, set() on a true bit should yield true, false should also yield true.
        Bit setBit = new Bit(false);
        setBit.set();
        assertEquals(setBit.getValue(), true);
        setBit.set();//if the above test passes, the bit changes to true, thus we can test set() again.
        assertEquals(setBit.getValue(), true);

        //moving onto not
        //subtest 11, not() on a false bit should yield true, not() again should yield false.
        Bit notBit = new Bit(false).not();
        assertEquals(notBit.getValue(), true);
        assertEquals(notBit.not().getValue(), false);

        //moving onto clear
        //subtest 12, clear() on a true bit should set value to false, clear() on a false bit should set the value false.
        Bit clearTrueBit = new Bit(true);
        clearTrueBit.clear();
        assertEquals(clearTrueBit.getValue(), false);
        clearTrueBit.clear();//seeing if clear() on false remains false.
        assertEquals(clearTrueBit.getValue(), false);

        //moving onto toggle
        //subtest 13, toggle() on a false bit should change the value to true, vice versa.
        Bit toggleFalseBit = new Bit(false);
        toggleFalseBit.toggle();
        assertEquals(toggleFalseBit.getValue(), true);
        toggleFalseBit.toggle();
        assertEquals(toggleFalseBit.getValue(), false);

        //subtest 14, basic toString test
        assertEquals(new Bit(false).toString(), "f");
        assertEquals(new Bit(true).toString(), "t");
    }

    //testing methods to fill a string with an n amount of zeroes, helps with long 32 length string redundancy
    String nOnes(int amount) {
        StringBuilder oneString = new StringBuilder();
        for (int i = 0; i < amount; i++) {
            oneString.append("1");
        }
        return oneString.toString();
    }

    String nZeros(int amount) {
        StringBuilder zeroString = new StringBuilder();
        for (int i = 0; i < amount; i++) {
            zeroString.append("0");
        }
        return zeroString.toString();
    }

    String WordToBinaryString(Word word) {
        Bit[] bits = word.getBits();
        StringBuilder returnString = new StringBuilder();
        for (int i = 0; i < 32; i++) {
            returnString.append(bits[i].getValue() ? "1" : "0");
        }
        return returnString.toString();
    }

    //for unit testing purposes, an abstract translation of a binary string to a Word object.
    Word binaryStringToWord(String binary) {
        Bit[] returnValue = new Bit[32];
        for (int i = 0; i < 32 - binary.length(); i++) {
            returnValue[i] = new Bit(false);
        }
        int strIndex = 0;
        for (int j = 32 - binary.length(); j < 32; j++) {
            returnValue[j] = new Bit(binary.charAt(strIndex++) == '1');
        }
        return new Word(returnValue);
    }

    Word getSetWord(int number) {
        Word word = new Word(null);
        word.set(number);
        return word;
    }

    @Test
    public void testWord() {
        final String binaryString = "10110"; // binary string to be converted to bits[]
        Word stringToWord = binaryStringToWord(binaryString);
        //binary string to word is a native method used for testing, it fills the previous bits with zeros, along with the string.


        //subtest 1, right shift 10110 should be ...00010
        assertEquals(nZeros(30) + "10", WordToBinaryString(stringToWord.rightShift(3)));
        Word shiftTest = new Word(new Bit[32]);
        for (int i = 2; i < 32; i *= 2) {
            shiftTest.set(i); // setting to a multiple of two
            assertEquals(i / 2, shiftTest.rightShift(1).getSigned());
            //right shift is equivalent to dividing by two
        }

        //subtest 2, left shift 10110 should be ...10110000
        assertEquals(nZeros(24) + "10110000", WordToBinaryString(stringToWord.leftShift(3)));
        String leftShiftBinary = "11111";
        int leftShiftAmount = 3; //..11111000
        assertEquals(nZeros(24) + "11111000", WordToBinaryString(binaryStringToWord(leftShiftBinary).leftShift(leftShiftAmount)));
        Word left = new Word(new Bit[32]);
        for (int i = 2; i < 32; i *= 2) {
            left.set(i); // setting to a multiple of two
            assertEquals(i * 2, left.leftShift(1).getSigned());
            //left shift is equivalent to multiplying by two
        }

        //subtest 3, going to test the toggle word function.
        String toggleString = "11011";
        Word toggleWord = binaryStringToWord(toggleString);
        //because of the twos compliment, the remaining bits should be flipped, or filled with ones.
        assertEquals(nOnes(32 - 5) + "00100", WordToBinaryString(toggleWord.toggle()));
        for (int i = -1000; i < 1000; i++) {
            toggleWord.set(i);
            toggleWord.toggle();
            assertEquals(i, toggleWord.not().getSigned()); // the word toggled should be the same as the not function
        }

        //subtest 4, unsigned conversion
        Word unsignedTest = binaryStringToWord("10110");
        assertEquals(22, unsignedTest.getUnsigned());
        for (int unsignedI = 0; unsignedI < 1000; unsignedI++) {
            //1000 numbers to ensure that unsigned works among a vast range of numbers.
            unsignedTest.set(unsignedI);
            assertEquals(unsignedI, (int) unsignedTest.getUnsigned());
        }

        //subtest 5, going to test the signed int function.
        final String signedString = nOnes(27) + "10110";
        Word str = binaryStringToWord(signedString);
        int test = str.getSigned();
        assertEquals(-10, test);
        str.set(127);
        assertEquals(127, str.getSigned());
        for (int signedI = -1000; signedI <= 1000; signedI++) {
            str.set(signedI);
            assertEquals(signedI, str.getSigned());
            //testing 2000 numbers just to be sure unsigned is working properly.
        }

        //subtest 6, set bits to a binary value.
        Bit[] b = new Bit[32];
        Word setTest = new Word(b);
        setTest.initBits();
        setTest.set(22);
        assertEquals(nZeros(27) + "10110", WordToBinaryString(setTest));
        //using both my binary string converter (testing) function and the unsigned() function
        assertEquals(22, setTest.getUnsigned());
        setTest.set(-22);
        assertEquals(-22, setTest.getSigned());

        //subtest 7, using copy()
        Word copyTest = binaryStringToWord("11111");
        assertEquals(nZeros(32 - 5) + "11111", WordToBinaryString(copyTest));
        Word newCopyWord = binaryStringToWord("101010");
        copyTest.copy(newCopyWord);
        assertEquals(nZeros(32 - 6) + "101010", WordToBinaryString(copyTest));

        //subtest 8, using not()
        Word notWord = binaryStringToWord("11000").not();
        assertEquals(nOnes(32 - 5) + "00111", WordToBinaryString(notWord));
        // NOT 11000 is 1{32-5}00111

        //subtest 9, using and()
        Word andTest = binaryStringToWord("11011");
        Word andWord = binaryStringToWord("11000").and(andTest);
        assertEquals(nZeros(32 - 5) + "11000", WordToBinaryString(andWord));
        andWord.set(45);
        andTest.set(45);
        assertEquals(45, andWord.and(andTest).getSigned());

        //subtest 10, using or()
        Word orTest = binaryStringToWord("11101");
        Word orWord = binaryStringToWord("00010").or(orTest);
        assertEquals(nZeros(32 - 5) + "11111", WordToBinaryString(orWord));

        //subtest 11, using xor()
        Word xorTest = binaryStringToWord("10101");
        Word xorWord = binaryStringToWord("01010").xor(xorTest);
        assertEquals(nZeros(32 - 5) + "11111", WordToBinaryString(xorWord));
        Word wordXorTest = new Word(new Bit[32]);
        wordXorTest.set(0);
        Word wordXorSecondParameter = new Word(new Bit[32]);
        wordXorSecondParameter.set(0);
        assertEquals(0, wordXorTest.xor(wordXorSecondParameter).getSigned());

        //subtest 12, basic toString test
        assertEquals(xorTest.toString(), "[f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, t, f, t, f, t]");
        HashMap<Integer, Integer> map = new HashMap<>();
    }

    @Test
    public void testALU() {
        var numbersToFiveHundred = new Word[500];
        for (int i = 0; i < numbersToFiveHundred.length; i++) {
            numbersToFiveHundred[i] = new Word(new Bit[32]);
            numbersToFiveHundred[i].set(i);
            assertEquals(i, numbersToFiveHundred[i].getUnsigned()); // ensuring the array is correct
        } //array of words ranging from zero to five hundred, good to have for tests incase you want a quick word with a number

        final Word firstALUWord = new Word(new Bit[32]);
        firstALUWord.set(32);
        final Word secondALUWord = new Word(new Bit[32]);
        secondALUWord.set(14);
        final Bit falseBit = new Bit(false);
        final Bit trueBit = new Bit(true);
        final Bit[] addBit = {trueBit, trueBit, trueBit, falseBit};
        final Bit[] subtractBit = {trueBit, trueBit, trueBit, trueBit};
        ALU operationObject = new ALU(firstALUWord, secondALUWord);
        operationObject.doOperation(addBit);
        assertEquals(46, operationObject.result.getUnsigned());
        secondALUWord.set(-12);
        operationObject.doOperation(addBit);
        firstALUWord.set(12);
        secondALUWord.set(12);
        operationObject.doOperation(addBit);
        assertEquals(24, operationObject.result.getUnsigned());

        //testing add4
        ALU addfour = new ALU(null, null);
        Word fourAdd = new Word(null);
        fourAdd.set(4);
        assertEquals(16, addfour.add4(fourAdd.getBits(), fourAdd.getBits(), fourAdd.getBits(), fourAdd.getBits()).getUnsigned());
        //should be 16
        assertEquals(28, addfour.add4(numbersToFiveHundred[12].getBits(), numbersToFiveHundred[3].getBits(), numbersToFiveHundred[6].getBits(), numbersToFiveHundred[7].getBits()).getUnsigned());
        assertEquals(11 + 5 + 18 + 11, addfour.add4(numbersToFiveHundred[11].getBits(), numbersToFiveHundred[5].getBits(), numbersToFiveHundred[18].getBits(), numbersToFiveHundred[11].getBits()).getUnsigned());
        ALU scaleTest;
        //If our manual tests pass, to be safe I am still iterating over a few hundred integers to be sure
        for (int i = 0; i < 400; i++) {
            scaleTest = new ALU(numbersToFiveHundred[12 + i], numbersToFiveHundred[56 + i]);
            scaleTest.doOperation(addBit);
            assertEquals(12 + i + 56 + i, scaleTest.result.getUnsigned());
        }
        Word negativeWord = new Word(null);
        negativeWord.set(-1246);
        scaleTest = new ALU(negativeWord, numbersToFiveHundred[321]);
        scaleTest.doOperation(addBit);
        assertEquals(-1246 + 321, scaleTest.result.getSigned());

        //testing multiplication
        ALU multiply = new ALU(numbersToFiveHundred[12], numbersToFiveHundred[5]);//12 * 5
        multiply.multiply2();

        firstALUWord.set(786);
        secondALUWord.set(13); // 786 - 13 = 773
        operationObject.doOperation(subtractBit);
        assertEquals(773, operationObject.result.getUnsigned());


        Word subtractionOp1 = new Word(new Bit[32]);
        subtractionOp1.set(100);
        Word subtractionOp2 = new Word(new Bit[32]);
        subtractionOp2.set(50);
        ALU subtractionALU = new ALU(subtractionOp1, subtractionOp2);
        subtractionALU.doOperation(subtractBit);
        assertEquals(50, subtractionALU.result.getSigned()); // 100 - 50 = 50
        subtractionALU.op1 = numbersToFiveHundred[54];
        subtractionALU.op2 = numbersToFiveHundred[32];
        subtractionALU.doOperation(subtractBit);
        assertEquals(54 - 32, subtractionALU.result.getUnsigned());
        subtractionALU.op1 = numbersToFiveHundred[32];
        subtractionALU.op2 = numbersToFiveHundred[54];
        subtractionALU.doOperation(subtractBit);
        assertEquals(32 - 54, subtractionALU.result.getSigned());


        //multiplying now
        final Bit[] MultiplicationBit = {falseBit, trueBit, trueBit, trueBit};
        Word multiplicationOp1 = new Word(new Bit[32]);
        multiplicationOp1.set(6);
        Word multiplicationOp2 = new Word(new Bit[32]);
        multiplicationOp2.set(6);
        ALU multiplicationALU = new ALU(multiplicationOp1, multiplicationOp2);
        multiplicationALU.doOperation(MultiplicationBit);
        for (int i = 0; i < 1000; i++) {
            Word firstOp = new Word(null);
            firstOp.set(i);
            Word secondOp = new Word(null);
            secondOp.set(i + 3);
            ALU loopMultiplication = new ALU(firstOp, secondOp);
            loopMultiplication.doOperation(MultiplicationBit);
            assertEquals(i * (i + 3), loopMultiplication.result.getUnsigned());
        }

        //right shift, left shift, not, xor, and, or
        final Bit[] rightShiftBits = {trueBit, trueBit, falseBit, trueBit};
        final Bit[] leftShiftBits = {trueBit, trueBit, falseBit, falseBit};
        final Bit[] notBits = {trueBit, falseBit, trueBit, trueBit};
        final Bit[] andBits = {trueBit, falseBit, falseBit, falseBit};
        final Bit[] orBits = {trueBit, falseBit, falseBit, trueBit};
        final Bit[] xorBits = {trueBit, falseBit, trueBit, falseBit};
        ALU finalTest = new ALU(null, null);
        finalTest.op1 = numbersToFiveHundred[23];
        finalTest.op2 = numbersToFiveHundred[1];
        finalTest.doOperation(leftShiftBits); //multiply by 2
        assertEquals(23 * 2, finalTest.result.getUnsigned());
        finalTest.doOperation(rightShiftBits); //divide by 2
        assertEquals(23 / 2, finalTest.result.getUnsigned());
        finalTest.doOperation(notBits);//nots 23
        ALU reNot = new ALU(finalTest.result, null);
        reNot.doOperation(notBits); // simply double not and assert back to original value
        assertEquals(23, reNot.result.getUnsigned());
        finalTest.doOperation(andBits); // 23 and 1
        assertEquals(1, finalTest.result.getUnsigned());
        finalTest.doOperation(orBits); //23 or 1
        assertEquals(23, finalTest.result.getUnsigned());
        finalTest.doOperation(xorBits);//1 since 23 is odd
        assertEquals(1, finalTest.result.getUnsigned());
    }

    @Test
    public void testALUSpeed() {
        /*
        this test is going to test every single operations speed (ideally a large quantity of operations)
        and compare it with javas native operations
        */
        long startn;
        long endn;
        long startb;
        long endb;
        Word zerotoamillion = new Word(null);
        zerotoamillion.initBits();
        Word one = new Word(null);
        one.set(1);

        Processor processor = new Processor();
        processor.setup(null, zerotoamillion);
        //we aren't actually testing the processor, just the increment function

        startn = System.currentTimeMillis();
        int foo = 0;
        for(int i = 0; i < 1_000_000; i++) {
            foo++;
        }
        endn = System.currentTimeMillis();
        startb = System.currentTimeMillis();
        for(int i = 0; i < 100_000; i++) {
            processor.increment(zerotoamillion);
        }
        endb = System.currentTimeMillis();
        System.out.println("native loop 1 million iteration time: " + (endn - startn)+ "ms");
        System.out.println("processor 100,000 iteration time: " + (endb - startb) + "ms");

        Word five = new Word(null);
        five.set(5);
        Word seven = new Word(null);
        seven.set(7);
        ALU multiplication = new ALU(five, seven);
        Bit[] multiply = {new Bit(false), new Bit(true), new Bit(true), new Bit(true)};

        startn = System.currentTimeMillis();
        int var = 0;
        for(int i = 0; i < 1_000; i++) {
            var = 5*7;
        }
        endn = System.currentTimeMillis();

        startb = System.currentTimeMillis();
        for(int i = 0; i < 1_000; i++) {
            multiplication.doOperation(multiply);
        }
        endb = System.currentTimeMillis();
        System.out.println("native multiplication time: " + (endn - startn) + "ms");
        System.out.println("bit multiplication time: " + (endb - startb) + "ms");

        startn = System.currentTimeMillis();
        var = 0;
        for(int i =0; i < 10_000; i++) {
            var = 567+675;
        }
        endn = System.currentTimeMillis();

        Word fivesixseven = new Word(null);
        fivesixseven.set(567);
        Word sixsevenfive = new Word(null);
        sixsevenfive.set(675);
        ALU plusALU = new ALU(fivesixseven, sixsevenfive);
        Bit[] add = {new Bit(true), new Bit(true), new Bit(true), new Bit(false)};
        startb = System.currentTimeMillis();
        for(int i = 0; i < 10_000; i++) {
            plusALU.doOperation(add);
        }
        endb = System.currentTimeMillis();
        System.out.println("native addition time: " + (endn - startn) + "ms");
        System.out.println("bit addition time: " + (endb - startb) + "ms");
    }

    private String[] getData() {
        String[] data = {
                "0000000000000 00101 1110 00001 00001", // math add 5 R1 value 5
                "00000000 00001 00001 1110 00010 00011", // math ADD R1 R1 R2 value 10
                "0000000000000 00010 1110 00010 00010", //math add R2 R2 value 20
                "00000000 00010 00001 1110 00011 00011", // math add R2 R1 R3 value 25
                "00000000 00011 00010 1111 00100 00011", // math subtract R3 R2 R4 value 5
                "00000000 00100 00011 0111 00101 00011", // math multiply R4 R3 R5 value 125
                "00000000 00001 00101 1000 00110 00011", // math and R1 R5 R6 value 5
                "00000000 00110 00101 1001 00111 00011", // math or R6 R5 R7 value 125
                "00000000 00111 00101 1010 01000 00011", // math xor R7 R5 R8 value 125
                "00000000 00111 00101 1011 01001 00011", // math not R7 R5 R9 value near the max
                "00000000 01001 00101 1011 01001 00011", // math not R9 R5 R9 value 125
                "00000000 01001 00001 1100 01010 00011", // math left shift R9 R1 R10 value 125<<5
                "00000000 01010 01001 1101 01011 00011", // math right shift R10 R2 R11 value R10>>R2 which should be zero
                "0000000000000 00101 1110 00000 00010", // math add R5 R0, this should not work, as R0 should remain zero
//                "00000000 00001 00010 0101 01100 00111", // branch le r1 r2 r12
//                "00000000 00001 00001 0000 01101 00111", // branch eq r1 r1 r13 this should be true
        };
        for (int i = 0; i < data.length; i++) {
            data[i] = data[i].replaceAll(" ", "");
            //un spacing the strings, easier to read if string instructions are first spaced out
        }
        return data;
    }

    @Test
    public void testMemory() {
        String[] data = getData();
        Processor processor = new Processor();
        Word testIncrement = new Word(new Bit[32]);
        testIncrement.set(0);
        MainMemory.load(data);

        for (int i = 0; i < data.length; i++) {
            Word address = new Word(null);
            address.set(i);
            assertEquals(WordToBinaryString(MainMemory.read(address)), data[i]);
            //testing every single loaded address to ensure that the memory has been loaded correctly
        }

        //increment test
        Word increment = new Word(null);
        increment.initBits();
        for (int i = 0; i < 1024; i++) {
            assertEquals(i, increment.getSigned());
            processor.increment(increment);
            //this test can be automated,
            // if it doesn't pass the 1024 increments
            // it doesn't work, otherwise it works.
        }

        for (int i = data.length; i < 1024; i++) {
            Word address = new Word(null);
            Word value = new Word(null);
            address.set(i);
            value.set(i * 5);
            MainMemory.write(address, value);
            assertEquals(i * 5, MainMemory.read(address).getUnsigned());
        }
        //iterating from the previous instructions to the end of the main memory
        //mapping every address to itself times 5, then reading from the address
        //asserting that it's equal to itself times five.
    }

    @Test
    public void testMemoryAndProcessor() {
        MainMemory.init(); // resetting memory from previous test
        String[] data = getData();
        Processor processor = new Processor();
        Word testIncrement = new Word(new Bit[32]);
        testIncrement.set(0);
        MainMemory.load(data);
        InstructionCache.initCache(); // initialize cache
        processor.setup(new Word(new Bit[32]), new Word(new Bit[32]));
        processor.run();
        Word[] registers = processor.getRegisters();

//                "0000000000000 00101 1110 00001 00001", // math add 5 R1 value 5
//                "00000000 00001 00001 1110 00010 00011", // math ADD R1 R1 R2 value 10
//                "0000000000000 00010 1110 00010 00010", //math add R2 R2 value 20
//                "00000000 00010 00001 1110 00011 00011", // math add R2 R1 R3 value 25
//                "00000000 00011 00010 1111 00100 00011", // math subtract R3 R2 R4 value 5
//                "00000000 00100 00011 0111 00101 00011", // math multiply R4 R3 R5 value 125
//                "00000000 00001 00101 1000 00110 00011", // math and R1 R5 R6 value 5
//                "00000000 00110 00101 1001 00111 00011", // math or R6 R5 R7 value 125
//                "00000000 00111 00101 1010 01000 00011", // math xor R7 R5 R8 value 125
//                "00000000 00111 00101 1011 01001 00011", // math not R7 R5 R9 value near the max
//                "00000000 01001 00101 1011 01001 00011", // math not R9 R5 R9 value 125
//                "00000000 01001 00001 1100 01010 00011", // math left shift R9 R1 R10 value 125<<5
//                "00000000 01010 01001 1101 01011 00011", // math right shift R10 R2 R11 value R10>>R2 which should be zero
//                "0000000000000 00101 1110 00000 00010", // math add R5 R0, this should not work, as R0 should remain zero
        /* manual opcode for this program is above, retrieved from the getData() method */
        for(Word register : registers) System.out.println(register.getUnsigned() + ", register");
        assertEquals(5, registers[1].getUnsigned());
        assertEquals(20, registers[2].getUnsigned());
        assertEquals(25, registers[3].getUnsigned());
        assertEquals(5, registers[4].getUnsigned());
        assertEquals(125, registers[5].getUnsigned());
        assertEquals(4000, registers[10].getUnsigned());
        assertEquals(0, registers[11].getUnsigned());
        assertEquals(0, registers[0].getUnsigned()); // ensuring that the register 0 remains 0 even after modification

        Word zeroRegister = new Word(null);
        zeroRegister.initBits();
        processor.store(zeroRegister, registers[3]);// testing 0r again, should not work
        assertEquals(0, registers[0].getUnsigned());
        //ensuring that all the registers have the proper values after running the program
    }

    /*
    turns a list of strings into a string
     */
    private String getCode(List<String> list) {
        StringBuilder program = new StringBuilder();
        for (String line : list) {
            program.append(line).append("\n");
        }
        return program.toString();
    }

    private Processor runProgram(String filename) throws IOException {
        Path path = Paths.get("./src/" + filename);
        List<String> list = Files.readAllLines(path);
        String code = getCode(list);
        Lexer lexer = new Lexer(code);
        lexer.lex();
        System.out.println(lexer);
        Parser parser = new Parser(lexer.getList());
        parser.Parse();
        String[] parsedCode = parser.getAssembledCode();

        MainMemory.init(); // reset memory to all zero's

        MainMemory.load(parsedCode);
        InstructionCache.initCache(); // load cache after memory has been loaded
        Processor processor = new Processor();
        Word pc = new Word(null);
        pc.initBits();
        processor.setup(null, pc);
        long start = System.currentTimeMillis();
        processor.run();
        long end = System.currentTimeMillis();
        System.out.println("Program " + filename + " took " + (end - start) + " milliseconds to run");
        return processor;
    }

    @Test
    public void fibonacciTest() throws IOException {
        System.out.println("Running the fibonacci program.");
        Processor processor = runProgram("fibonacci.asm"); // running the program fibonacci
        int[] fib = javaFib(20); // this is correct by human eye, doesn't need testing
        Word[] registers = processor.getRegisters();
        for(int i = 1; i < 20; i++) {
            assertEquals(fib[i], registers[i+1].getUnsigned());// since we don't use R0 as a variable, we need to skip over it.
            // this test passing will ensure that registers[1-20] contain the correct fibonacci sequence (listed below)
            // [1, 1, 2, 3, 5, 8, 13, 21, 34, 55, 89, 144, 233, 377, 610, 987, 1597, 2584, 4181, 6765]
        }
    }

    /* short test method using dynamic programming to ensure the SIA version works */
    private int[] javaFib(int len) {
        int[] cache = {0, 1}, returnValue = new int[len];
        returnValue[0] = 1;
        for (int i = 1; i < len; i++) {
            returnValue[i] = cache[0] + cache[1];
            cache[0] = cache[1];
            cache[1] = returnValue[i];
        }
        return returnValue;
    }

    @Test
    public void factorialTest() throws IOException {
        System.out.println("Running the factorial program.");
        Processor processor = runProgram("factorial.asm");
        int[] factorial = javaFactorial(10);
        Word[] registers = processor.getRegisters();
        for(int i : factorial) System.out.println(i + " factorial");
        for(int i = 0; i < 6; i++) {
            assertEquals(factorial[i], registers[i+1].getUnsigned());
            // factorials 1 through 6, zero register cannot be modified, so starting at register 1
        }
    }

    private int[] javaFactorial(int len) {
        int[] value = new int[len];
        int cache = 1;
        value[0] = 1;
        value[1] = 1;
        for(int i = 2; i < len; i++) {
            value[i] = cache * i;
            cache = value[i];
        }
        return value;
    }

    @Test
    public void AllCombinationsTest() throws IOException {
        System.out.println("running all combinations test");
        Processor processor = runProgram("allcombinations.asm");
        Word[] registers = processor.getRegisters();
        /* now going to test the registers with their appropriate value */

        assertEquals(0, registers[0].getUnsigned()); // starting off with R0 being unmodified
        assertEquals(3, registers[3].getSigned());
    }

    @Test
    public void fibAutoTest() throws IOException {
        System.out.println("Running the looping fibonacci sequence asm code.");
        Processor processor = runProgram("fibonacciauto.asm");
        Word[] registers = processor.getRegisters();
        System.out.println(registers[3].getUnsigned());
        assertEquals(55, registers[3].getUnsigned()); // since we're calculating the fib(10), R3 should be 55
        int[] javafib = javaFib(10);
        assertEquals(javafib[9], registers[3].getUnsigned()); // the 10th number of javafib (0 indexed) should be 55
        long start = System.currentTimeMillis();
        javaFib(500_000);
        long end = System.currentTimeMillis();
        System.out.println("java fibonacci took: " + (end - start) + "ms to run");
    }

    @Test
    public void testIndexOf() {
        Processor processor = new Processor();
        for(int i = 0; i < 32; i++) {
            Word word = new Word(null);
            word.set(i);
            assertEquals(i, processor.indexOf(word));
        }
    }

    @Test
    public void cacheTest() throws IOException {
        Processor countCycles = runProgram("countcycles.asm");
        assertEquals(16, MainMemory.read(getSetWord(2)).getUnsigned());
        /* check the stored value of 8*2 -> mem[2] */
    }

    @Test
    public void LinkedListTest() throws IOException {
        Processor linkedListTest = runProgram(("sum20linkedlist.asm"));
        int regcount = 0;
        for(Word register : linkedListTest.getRegisters()) {
            System.out.println("R" + regcount++ + ": " + register.getUnsigned());
        }
        for(int i =40; i <= 60; i++) { // iterate and print out the linked list in memory
            System.out.println("address " + i + " is " +MainMemory.read(getSetWord(i)).getUnsigned());
        }
        assertEquals(55, linkedListTest.getRegisters()[17].getUnsigned()); // sum of the linked list is 55
    }

    @Test
    public void ArrayTest() throws IOException {
        Processor arrayTest = runProgram(("sum20integers.asm"));
        int regcount = 0;
        for(Word register : arrayTest.getRegisters()) {
            System.out.println("R" + regcount++ + ": " + register.getUnsigned());
        }
        for(int i = 40; i <= 60; i++) {
            System.out.println(MainMemory.read(getSetWord(i)).getUnsigned() + ", is the value of address " + i);
        }
        assertEquals(210, arrayTest.getRegisters()[6].getUnsigned()); // R6 being 210 will mean that the sum of the arry 1-20 will equal 210, which it does
    }

    @Test
    public void IntegersBackward() throws IOException {
        Processor backwardTest = runProgram(("integersbackward.asm"));
        int regcount = 0;
        for(Word register : backwardTest.getRegisters()) {
            System.out.println("R" + regcount++ + ": " + register.getUnsigned());
        }
        for(int i = 40; i <= 60; i++) {
            System.out.println(MainMemory.read(getSetWord(i)).getUnsigned() + ", is the value of address " + i);
        }
    }
}
