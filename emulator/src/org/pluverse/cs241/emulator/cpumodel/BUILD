load("@io_bazel_rules_kotlin//kotlin:jvm.bzl", "kt_jvm_library")

kt_jvm_library(
    name = "cpumodel",
    srcs = [
        "CpuEmulator.kt",
    ],
    visibility = ["//visibility:public"],
    deps = [
        ":cpumodel_lib",
        "//emulator/src/org/pluverse/cs241/emulator/views:views_lib",
    ],
)

kt_jvm_library(
    name = "cpumodel_lib",
    srcs = [
        "Address.kt",
        "EmulatorExceptions.kt",
        "ExecutionStack.kt",
        "Memory.kt",
        "MemoryData.kt",
        "MipsInstruction.kt",
    ],
    visibility = ["//visibility:public"],
)
