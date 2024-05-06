
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Main {

//    private static String getCode(List<String> list) {
//        StringBuilder program = new StringBuilder();
//        for(String line : list) {
//            program.append(line).append("\n");
//        }
//        return program.toString();
//    }
//
//    private static String[] formatOpCode(String[] data) {
//        for(int i  =0; i < data.length; i++) {
//            data[i] = data[i].replaceAll(" ", "");
//        }
//        return data;
//    }

    public static void main(String[] args) throws IOException {
//        Path path = Paths.get(args[0]);
//        List<String> list = Files.readAllLines(path);
//        String code = getCode(list);
//        Lexer lexer = new Lexer(code);
//        lexer.lex();
//        System.out.println(lexer);
//        Parser parser = new Parser(lexer.getList());
//        parser.Parse();
//        String[] data = {
//                "000000000000000101 1110 00001 00001", // MATH destonly 5 R1 value 5
//                "00000000 00001 00001 1110 00010 00011", // MATH ADD R1 R1 R2 value 10
//                "0000000000000 00010 1110 00010 00010", //MATH ADD R2 R2 value 20
//                "00000000 00010 00001 1110 00011 00011", // MATH ADD R2 R1 R3 value 25
//                "00000000 00011 00010 1111 00100 00011", // MATH SUBTRACT R3 R2 R4 value 5
//                "0000000000000 00011 0101 00010 00110",// branch le R3 R2
//                "0000000000000 01010 0100 01011 00110",//branch gt R10 R11
//                "0000000000000 11110 0011 00001 00110",//branch ge R30 R1
//                "00000000 00001 00010 0010 00100 00111",//branch lt R1 R2 R4
//                "0000000000000 00101 0001 01000 00110",//branch neq R5 R8
//                "0000000000000 10001 0000 10010 00110",//branch eq R17 R18
//                "00000000 00001 00010 1111 00011 00011",//math subtract R1 R2 R3
//                "0000000000000 00111 0111 00101 00010",//math multiply R7 R5
//                "0000000000000 00100 1000 01001 00010",//math and R4 R9
//                "0000000000000 11111 1001 00001 00010",//math or R31 R1
//                "00000000 11000 01010 1010 01000 00011",//math xor R24 R10 R8
//                "0000000000000 00101 1011 00101 00010",//math not R5 R5
//                "0000000000000 00001 1100 00100 00010",//math ls R1 R4
//                "0000000000000 01000 1101 01001 00010",//math rs R8 R9
//                "00000000 00001 00010 0010 00101 01011",//call lt R1 R2 R5
//                "0000000000000 00010 0111 00111 01110",//push multiply R2 R7
//                "0000000000000 00001 1110 01000 10010",//load R1 R8
//                "0000000000000 00001 1110 00010 10110",//store R1 R2
//                "0000000000000 00101 0000 01000 11010",//pop R5 R8
//        };
//        String[] assembledCode = parser.getAssembledCode();
//        data = formatOpCode(data);
//        for (int i = 0; i < data.length; i++) {
//            //change this, use the asserteq
//
//            if (data[i].equals(assembledCode[i])) {
//                System.out.println("equal, passed test: " + i);
//            }
//            else {
//                System.out.println("neq" + i);
//                System.out.println("expected:\n" + data[i]);
//                System.out.println("\n actual\n" + assembledCode[i] + "\n\n");
//            }
//
//        }
//
//
    }
}