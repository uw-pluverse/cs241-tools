grammar Arm64Asm;

@header { package org.pluverse.cs241.assembler; }

// ---------- Parser ----------
//program
//  : line* EOF
//  ;

program
  : line* lastline? EOF
  ;

line
  : labels? statement? NEWLINE
  ;

lastline
  : labels statement?
  | statement
  ;

labels
  : labelDef+
  ;

labelDef
  : LABEL_ID COLON
  ;

statement
  : arith3
  | cmp2
  | mem
  | ldr_pc
  | br_reg
  | blr_reg
  | b_imm
  | b_cond
  | dir8
  ;

arith3
  : ADD reg COMMA reg COMMA reg   #Add3
  | SUB reg COMMA reg COMMA reg   #Sub3
  | MUL reg COMMA reg COMMA reg   #Mul3
  | SMULH reg COMMA reg COMMA reg #Smulh3
  | UMULH reg COMMA reg COMMA reg #Umulh3
  | SDIV reg COMMA reg COMMA reg  #Sdiv3
  | UDIV reg COMMA reg COMMA reg  #Udiv3
  ;

cmp2
  : CMP reg COMMA reg #CmpInstr
  ;

mem
  : LDUR reg COMMA LBRACK reg COMMA imm RBRACK #LdurMem
  | STUR reg COMMA LBRACK reg COMMA imm RBRACK #SturMem
  ;

ldr_pc
  : LDR reg COMMA addr #LdrPc
  ;

b_imm
  : B addr #BImm
  ;

br_reg
  : BR reg #BrReg
  ;

blr_reg
  : BLR reg #BlrReg
  ;

// support：b '.' cond imm  or  b cond imm
b_cond
  : B DOT cond addr #BCondDot
  | B cond addr #BCondPlain
  ;

dir8
  : DOT8BYTE addr #Dir8Byte
  ;

// addressing modes: immediate or label
addr
  : imm      #AddrImm
  | LABEL_ID #AddrLabel
  ;

// registers
reg
  : XZR #XzrReg
  | SP #SpReg
  | XREG #RegX
  ;

// immediate values
imm
  : DEC_INT #ImmDec
  | HEX_INT #ImmHex
  ;

// cond
cond
  : EQ | NE | HS | LO | HI | LS | GE | LT | GT | LE
  ;

// ---------- Lexer ----------
// keywords（lowercase）
ADD   : 'add';
SUB   : 'sub';
MUL   : 'mul';
SMULH : 'smulh';
UMULH : 'umulh';
SDIV  : 'sdiv';
UDIV  : 'udiv';
CMP   : 'cmp';

B     : 'b';
BR    : 'br';
BLR   : 'blr';

LDUR  : 'ldur';
STUR  : 'stur';
LDR   : 'ldr';

DOT8BYTE : '.8byte';
DOT      : '.';

// cond
EQ : 'eq'; NE : 'ne';
HS : 'hs'; LO : 'lo'; HI : 'hi'; LS : 'ls';
GE : 'ge'; LT : 'lt'; GT : 'gt'; LE : 'le';

// registers (leave range checking to later stages)
XZR  : 'xzr';
SP   : 'sp';
XREG : 'x' DIGIT+;

// label identifiers
//LABEL_ID : [a-zA-Z] [a-zA-Z0-9]* ; // this strictly follows course specs, which does not allow '_'
LABEL_ID : [a-zA-Z_] [a-zA-Z0-9_]*;

// immediates
DEC_INT : '-'? DIGIT+;
HEX_INT : '0x' HEXDIGIT+;

// symbols
COMMA   : ',';
LBRACK  : '[';
RBRACK  : ']';
COLON   : ':';

WS      : [ \t]+ -> skip;
NEWLINE : ('\r'? '\n')+;

LINE_COMMENT : '//' ~[\r\n]* -> skip;

// fragment
fragment DIGIT    : [0-9];
fragment HEXDIGIT : [0-9a-fA-F];
