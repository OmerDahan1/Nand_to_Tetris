// This file is part of www.nand2tetris.org
// and the book "The Elements of Computing Systems"
// by Nisan and Schocken, MIT Press.
// File name: projects/04/Mult.asm

// Multiplies R0 and R1 and stores the result in R2.
// (R0, R1, R2 refer to RAM[0], RAM[1], and RAM[2], respectively.)
//
// This program only needs to handle arguments that satisfy
// R0 >= 0, R1 >= 0, and R0*R1 < 32768.

//initialize R2 to 0 and i to 1
@R2
M=0
@i
M=1
//if R0=0 goto END
@R0
D=M
@END
D;JEQ
//if R1=0 goto END
@R1
D=M
@END
D;JEQ
//LOOP
(LOOP)
//if(i-R0 > 0) goto END
@i
D=M
@R0
D=D-M
@END
D;JGT
// R2 = R2 + R1
@R2 
D=M
@R1
D=D+M
@R2
M=D
//i = i + 1
@i
M=M+1
//back to LOOP
@LOOP
0;JMP
//END
(END)
@END
0;JMP
