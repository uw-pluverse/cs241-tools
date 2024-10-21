lis $10 ; input address
.word 0xffff0004

lis $20 ; output address
.word 0xffff000c

lis $25
.word -1

head:
  lw $3, 0($10)
  beq $3, $25, end
  sw $3, 0($20)
  beq $0, $0, head

end:
jr $31

