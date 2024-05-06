; sum 20 integers in an array sia program
math add 40 R31 ; start of the array location
math add 60 R30 ; end location
math add 1 R1 ; store 1 inside of R1
math add 1 R2 ; store 1 as a constant in R2
math add 5 R9 ; store the number to goto during the call loop
store 20 R31 ; putting the array length in memory
; store 20 values inside the array
math add R2 R31
; start the loop process
store R1 R31 ; store 1-20 from addresses 40-60
math add R2 R1
call lt R31 R30 R9

math add 11 R7; line number
math add 41 R10 ; start at 40 again
load R10 R5 ; R5 stores the current loaded address
math add R5 R6 ; R6 is the sum
math add R2 R10 ; increment R10
call lt R10 R30 R7