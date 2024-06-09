load("@io_bazel_rules_kotlin//kotlin:jvm.bzl", "kt_jvm_library")

kt_jvm_library(
    name = "views_lib",
    srcs = [
        "EmulatorView.kt",
        "CliView.kt"
    ],
    deps = [
        "//emulator/src/org/pluverse/cs241/emulator/cpumodel:cpumodel_lib"
    ],
    visibility = ["//visibility:public"]
)