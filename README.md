# CS241-Tools - MIPS Assembly Emulator
This emulates binary instructions for a MIPS assembly program. It will execute and update the registers, pc, and memory as the program runs.

## Build
Packaged all controllers into a single jar file.

Execute the following to obtain the single jar 
```bash
bazel build emulator/src/org/pluverse/cs241/emulator:emulator_deploy.jar
```

or 
```bash
cd scripts
./build_self_executable.sh
```
Then you will see a JAR file named `pluverse_emulator`. This file is a JAR file and an executable file on Linux.

## Usage

Note that you need to have Java 11 on your PATH to run the emulator. You can enable the debugging mode of the emulator
with the option `--debug true`. 

```bash
java -jar pluverse_emulator --program <input_file> \
    [--debug true/false]? [--stdin-file <file>]? \
    twoints --register1 <int> --register2 <int>
```


```bash
java -jar pluverse_emulator --program <input_file> \
    [--debug true/false]? [--stdin-file <file>]? \
    array --elements 1,2,3,4,5
```

## Debugger
Allows for breakpoints and dynamic stepping (forward or backwards).

![Execute Command](/assets/Execute.png)

![Default Debugger](/assets/Debugger.png)

![Breakpoint Command](/assets/Breakpoints.png)


