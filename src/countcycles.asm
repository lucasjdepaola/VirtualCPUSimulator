math add 2 R1 ; 2 + 300 cycles
math add 8 R2 ; 2 + 300 cycles
math multiply R1 R2 R3 ; 10 + 300 cycles
store R3 R1 ; another memcall, store 16 inside address 2 (overwrites), 300+300 + 2 (since addition happens in store) cycles
; total number is 302 + 302 + 310 + 600 + 2 + 300 + 2 (since halt bit is technically addition of nothing) = 1214 + 600 + 2 = 1816
; without caching the value is 1816
; using just an instruction cache the value of the clock cycle is 366 which is substantially lower
; using an instruction cache and an L2 cache, the value of the clock cycle is still 366
; write through implemented, 116 clock cycles