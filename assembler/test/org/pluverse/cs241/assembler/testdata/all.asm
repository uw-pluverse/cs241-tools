Start:
EntryPoint:

Main: MainLoop:
  add x0, x1, x2         // add (Add3)

// arithmetic
  sub   x3, x4, x5       // sub (Sub3)
  mul   x6, x7, x8       // mul (Mul3)
  smulh x9, x10, x11     // smulh (Smulh3)
  umulh x12, x13, x14    // umulh (Umulh3)
  sdiv  x15, x16, x17    // sdiv (Sdiv3)
  udiv  x18, x19, x20    // udiv (Udiv3)

// cmp
CompareBlock:
  cmp x21, x22           // cmp (CmpInstr)

MemBlock1:
  ldur x0,  [x1, -8]
  stur x2,  [x3, 0]
  ldur x4,  [x5, 16]
  stur x6,  [x7, 0x20]

PCRelBlock:
  ldr x8,  32            // LdrPc + AddrImm
  ldr x9,  JumpTable     // LdrPc + AddrLabel
  b 16                   // BImm + AddrImm
  b Loop1                // BImm + AddrLabel

  br  x30                // BrReg
  blr x29                // BlrReg

Loop1:
  cmp x0, x1
  b.eq EndBlock          // BCondPlain (b cond addr)
  b . ne Loop1           // BCondDot   (b . cond addr)

CondTest:
  cmp x2, x3
  b.hs GreaterOrEqual    // hs
  b.lo LessThan         // lo
  b.hi StrictGreater    // hi
  b.ls LessOrEqual      // ls
  b.ge GreaterOrEqual   // ge
  b.lt LessThan         // lt
  b.gt StrictGreater    // gt
  b.le LessOrEqual      // le

GreaterOrEqual:
  add x0, x0, x1
  b Done

LessThan:
  sub x0, x0, x1
  b Done

StrictGreater:
  mul x0, x0, x1
  b Done

// .8byte
DataSection1:
  .8byte 0
  .8byte -1
  .8byte 0x1234ABCD
  .8byte Start
  .8byte JumpTable

JumpTable:
  .8byte Case0
  .8byte Case1
  .8byte Case2

Case0:
  add x1, x1, x1
  b Done

Case1:
  sub x1, x1, x2
  b Done

Case2:
  mul x1, x1, x2
  b Done

Loop2:
LOOP3:
Data2Section42:
  cmp xzr, x0
  b.ne MainLoop


// comment only line


EndBlock:
Done:
  br x30
