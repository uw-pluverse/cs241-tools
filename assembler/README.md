# Assembler

## Build

Execute the following to obtain the single jar

```
bazel build //assembler/src/org/pluverse/cs241/assembler:assembler_deploy.jar
```

## Usage

```
java -jar bazel-bin/assembler/src/org/pluverse/cs241/assembler/assembler_deploy.jar <input.asm>
```

## Test

Execute the following to run the unit tests:

```
bazel test //assembler/...
```