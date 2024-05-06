math add 10 R8 ; change this number to the fibonacci number wanting to be calculated
math add 1 R10 ; count register
math add 4 R9 ; go to this line number on repeat
math add 1 R2 ; start the fibonacci function, first starting off by hardcoding 1 into R1
math add 1 R30 ; increment register

; here is where the loop begins.
math add R2 R1 R3 ; R3 is going to be where the answer is
math add 0 R1 ; putting 0 inside R1 to reset its value
math add R2 R1 ; putting R2 inside the R1 "cache" similar to the java function
math add 0 R2 ; putting zero into R2 to reset its value
math add R3 R2 ; storing the previous fib value inside of R2
math add R30 R10 ; increment the count register
; here is where the loop ends.

call gt R8 R10 R9 ; ensure that count is less than 20 (the sum)
; when R8 > R10, we go to R9 -> 4, which loops until R10 = 10
; TODO figure out why the L2 cache cannot obtain the correct value for the auto fib sequences.