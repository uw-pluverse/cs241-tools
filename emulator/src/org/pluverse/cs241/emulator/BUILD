load("@io_bazel_rules_kotlin//kotlin:jvm.bzl", "kt_jvm_binary", "kt_jvm_library")

kt_jvm_binary(
    name = "emulator",
    main_class = "org.pluverse.cs241.emulator.MipsEmulatorMain",
    runtime_deps = [":emulator_lib"]
)

kt_jvm_library(
    name = "emulator_lib",
    srcs = [
        "MipsEmulatorMain.kt",
        "Memory.kt",
        "CpuEmulator.kt",
        "Memory.kt",
        "MemoryData.kt",
        "MipsInstruction.kt"
    ]
)
