; arbitrary code to test every assembly format
math add 3 R1 ; 3 inside R1
math add 2 R2 ; 2 inside R2
math multiply R1 R2 ; 6 inside R2
math subtract R2 R1 R3 ; 6-3 = 3
branch le 5 R1 ; false or 0
branch ge R1 R2 ; false, 6 > 3
branch eq R1 R2 R20 ; not really sure what purpose R20 serves here, supposed to be pc -> imm+pc if condition is true
call eq 1 R3 ; R3 isn't equal to 1, so nothing should happen
call ge R1 R2 ; 3 is not ge 6
call neq R3 R2 R1 ; true, R3 isn't equal to R2
push add 3 R1 ; in --sp there should be 3 + R1
push multiply R1 R2 ; in R2 there should be R1 * R2
push subtract R3 R2 R5 ; in R5 there should be R3 - R2
load 2 R1 ; in R1 it should return the address R1 + 2
load R1 R2 ; in R2 it should load the address of R1
load R2 R3 R6 ; in R6 it should load the address of R2 + R3
store 12 R7 ; in dram[R7] it should store 12
store R3 R1 ; in dram[R1] it should store R3
store R6 R2 R4 ; in dram[R6+R2] it should store R4
pop R8 ; R8 should store dram[sp++]
pop R4 R2 ; R2 should store mem[sp-R4]
pop R8 R1 R9 ; R9 should store mem[sp - (R8+R1)]