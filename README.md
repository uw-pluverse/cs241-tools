# CS241-Tools - MIPS Assembly Emulator
This emulates binary instructions for a MIPS assembly program. It will execute and update the registers, pc, and memory as the program runs.

## Build
Packaged all controllers into a single jar file.

Execute the following to obtain the single jar 
```bash
bazel build emulator/src/org/pluverse/cs241/emulator/emulator_deploy.jar
```

## Usage

```bash
java -jar emulator_deploy.jar <input_file> [OPTIONAL] -debug -array -stdin

# Note it defaults to twoints controller
# array sets it to use an array for $1 and $2
# stdin reads from stdin instead of a file
# debug uses the ncurses-like view - a paneled and interactive debugger
```

## Debugger
Allows for breakpoints and dynamic stepping (forward or backwards).

![Execute Command](/assets/Execute.png)

![Default Debugger](/assets/Debugger.png)

![Breakpoint Command](/assets/Breakpoints.png)


