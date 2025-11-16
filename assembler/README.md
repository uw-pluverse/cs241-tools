# Assembler

## Build

Execute the following to obtain the single jar

```
bazel build //assembler:assembler_deploy.jar
```

## Usage

```
java -jar bazel-bin/assembler/assembler_deploy.jar <input.asm>