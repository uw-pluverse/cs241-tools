load("@io_bazel_rules_kotlin//kotlin:jvm.bzl", "kt_jvm_library")

kt_jvm_library(
    name = "components",
    srcs = [
        "DataActionListBox.kt",
        "InstructionsListItemRenderer.kt",
        "CommandLine.kt"
    ],
    visibility = ["//visibility:public"],
    deps = [
        "//:lanterna",
        "//emulator/src/org/pluverse/cs241/emulator/cpumodel:cpumodel_lib",
    ],
)
