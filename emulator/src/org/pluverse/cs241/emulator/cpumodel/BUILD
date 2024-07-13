load("@io_bazel_rules_kotlin//kotlin:jvm.bzl", "kt_jvm_library")

kt_jvm_library(
    name = "cpumodel",
    srcs = [
        "CpuEmulator.kt"
    ],
    deps = [
        ":cpumodel_lib",
        "//emulator/src/org/pluverse/cs241/emulator/views:views_lib"
    ],
    visibility = ["//visibility:public"]
)

kt_jvm_library(
    name = "cpumodel_lib",
    srcs = [
        "Memory.kt",
        "MemoryData.kt",
        "MipsInstruction.kt",
        "EmulatorExceptions.kt",
        "Address.kt",
    ],
    visibility = ["//visibility:public"]
)