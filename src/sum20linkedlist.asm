; create a linked list of 20 values then sum them
; emulate struct list { int value; struct list * next; }
math add 40 R31 ; list head in memory
math add 1 R2 ; constant incremental value for R3
math add 1 R3 ; start of the sum
math add 60 R10 ; last value in the list
math add R31 R28 ; pointer to the list head
math add 2 R30 ; size of the linked list (list->next), and value
math add 8 R27 ; number to jump to on the loop
math add 39 R5 ; next pointer memory position
math add R31 R6

store R3 R28 ; store R3 (value) in the head of the list
math add R2 R3 ; increment R3 by one (make the sum somewhat variable)
math add R30 R6 ; pointer to the next node
math add R30 R5 ; next value of the linked list being the next pointer one above
store R6 R5 ; store the pointer to the next node in the position after the value in the linked list
math add R30 R28 ; increment the pointer by the size of the linked list
call gt R10 R28 R27

math add 40 R19 ; pointer to the head to read and sum the values
math add 18 R20 ; GOTO line
math add 10 R4 ; size of the list

load R19 R18 ; R18 has the stored current value
math add R18 R17 ; R17 contains the sum of the linked list
math add R30 R19 ; find the next address by adding 1
load R19 R15 ; next address in memory
math add R2 R24 ; increment value
call gt R4 R24 R20
