load("@io_bazel_rules_kotlin//kotlin:jvm.bzl", "kt_jvm_library")

kt_jvm_library(
    name = "components",
    srcs = [
        "CommandLineTextBox.kt",
        "DataActionListBox.kt",
        "InstructionsListItemRenderer.kt",
    ],
    visibility = ["//visibility:public"],
    deps = [
        "//:lanterna",
        "//emulator/src/org/pluverse/cs241/emulator/cpumodel",
    ],
)
