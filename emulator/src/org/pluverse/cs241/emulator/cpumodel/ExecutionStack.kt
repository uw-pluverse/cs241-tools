package org.pluverse.cs241.emulator.cpumodel

/**
 * ExectionStack is used to track the execution of the program for step reversal.
 *
 * It will track the program counter and the original register/memory state in each step.
 *
 *      For instance, if instruction 0 changes register 1 value 0 -> 5, then the stack will
 *      record register 1 value of 0, so we can reverse backwards into it.
 *
 * Note: The inner list is per instruction. Contents inside are mutations performed by instruction.
 *
 */
interface ExecutionStack {
    fun getSize(): Int
    fun get(i: Int): List<Execution>?
    fun last(): List<Execution>?
}

/**
 * Executions will be appended whenever a change occurs in the memory, register, or PC to save
 * for rewind. This is a list of list of executions done in each execution in stack-like fashion.
 *
 * MutableList<...> below:
 *
 * MutableList<Triple<ExecutionType, MemoryCompanion.Address, Int>>
 *
 *     A list of all mutations during a single instruction. Last item in overall list
 *     is the last instruction's mutation.
 *
 * ... Triple<ExecutionType, Companion.Address, Int>
 *
 *     Execution Type: The type of execution done
 *
 *     Address: The address in which it was stored (changed) || Last PC address
 *
 *     Int: The value that was in their prior || null
 *
 */
class MutableExecutionStack : ExecutionStack {

    private val executionStack: MutableList<MutableList<Execution>> = mutableListOf()

    /**
     * Add a new instruction to the stack
     */
    fun newInstruction() {
        executionStack.add(mutableListOf())
    }

    /**
     * This function adds a new execution to the current instruction.
     * Perform some assertions to ensure value null iff type is PC.
     */
    fun recordExecution(type: Execution.ExecutionType, address: Address, value: Int = 0) {
        executionStack.last().add(Execution(type, address, value))
    }

    /**
     * Pop the last instruction's mutations
     */
    fun pop(): MutableList<Execution>? {
        return executionStack.removeLastOrNull()
    }

    override fun getSize(): Int = executionStack.size

    /**
     * Get instruction execution at index i. Make it immutable.
     */
    override fun get(i: Int): List<Execution>? {
        return executionStack.getOrNull(i)
    }

    override fun last(): List<Execution>? {
        return executionStack.lastOrNull()
    }

}

/**
 * A data type to represent an execution - that is a change in the program counter, register, or memory.
 *
 */
data class Execution(val type: ExecutionType, val address: Address, val value: Int) {

    enum class ExecutionType {REGISTER, MEMORY, PC}
}