sub x1, x2, x3

smulh x4, x5, x6
umulh x7, x8, x9
sdiv x0, x1, x2
udiv x3, x4, x5
cmp x6, xzr


ldur x20, [x21, -8]
ldur x22, [xzr, 0x40]
stur x23, [sp, 16]
ldr x24, 4096


b gt 8 // plain form without dot
b.lo -12 // negative decimal