// This file is part of www.nand2tetris.org
// and the book "The Elements of Computing Systems"
// by Nisan and Schocken, MIT Press.
// File name: projects/04/Fill.asm

// Runs an infinite loop that listens to the keyboard input.
// When a key is pressed (any key), the program blackens the screen,
// i.e. writes "black" in every pixel;
// the screen should remain fully black as long as the key is pressed. 
// When no key is pressed, the program clears the screen, i.e. writes
// "white" in every pixel;
// the screen should remain fully clear as long as no key is pressed.
//insert to i the value of the start of the pixel and iterate on all pixels and 
//give them the value -1 if the value of the keyboard register is not 0.
(START)
//initialize iterator to 16384
@16384
D=A
@iterator
M=D
//check Keyboard and if we dont press the keyboard than go to WHITE
@24576
D=M
@WHITE
D;JEQ
//if we press the keyboard than go to BLACK
@24576
D=M
@BLACK
D;JNE

(BLACK)
//LOOP all over the screen and do M=-1
@24575
D=A
@iterator
D=D-M
@END
D;JLT
//SCREEN = -1
@iterator
A=M
M=-1
//iterator++
@iterator
M=M+1
//goto BLACK
@BLACK
0;JMP

(WHITE)
//LOOP all over the screen and do M=0
@24575
D=A
@iterator
D=D-M
@END
D;JLT
//SCREEN = 0
@iterator
A=M
M=0
//iterator++
@iterator
M=M+1
//goto WHITE
@WHITE
0;JMP
//infinite LOOP
(END)
@START
0;JMP