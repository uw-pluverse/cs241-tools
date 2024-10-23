package(default_visibility = ["//visibility:public"])

load("@io_bazel_rules_kotlin//kotlin:jvm.bzl", "kt_jvm_binary", "kt_jvm_library")

java_binary(
    name = "emulator",
    main_class = "org.pluverse.cs241.emulator.MipsEmulatorMain",
    runtime_deps = [":emulator_lib"],
)

kt_jvm_library(
    name = "emulator_lib",
    srcs = [
        "CommandOptions.kt",
        "MipsEmulatorMain.kt",
    ],
    deps = [
        "//:jcommander",
        "//emulator/src/org/pluverse/cs241/emulator/controllers:controllers_lib",
        "@maven//:org_jetbrains_kotlinx_kotlinx_cli_jvm",
    ],
)
