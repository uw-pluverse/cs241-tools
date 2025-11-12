// prologue
add x0, x0, x1
cmp xzr, x5


ldur x2, [x3, 16]
stur x5, [sp, -32]
ldr x4, 0x100


b.ne -4 // also accepted as: b . ne -4
br x30
blr x19


.8byte 0x0