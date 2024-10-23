load("@io_bazel_rules_kotlin//kotlin:jvm.bzl", "kt_jvm_library")

kt_jvm_library(
    name = "cpumodel",
    srcs = [
        "AbstractMipsStdInput.kt",
        "Address.kt",
        "CpuEmulator.kt",
        "EmulatorExceptions.kt",
        "ExecutionStack.kt",
        "IEmulatorListener.kt",
        "Memory.kt",
        "MemoryData.kt",
        "MipsInstruction.kt",
        "MipsStdInput.kt",
    ],
    visibility = ["//visibility:public"],
    deps = [
        "//:guava",
    ],
)
