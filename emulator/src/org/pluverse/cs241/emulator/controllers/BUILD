load("@io_bazel_rules_kotlin//kotlin:jvm.bzl", "kt_jvm_binary", "kt_jvm_library")

kt_jvm_library(
    name = "controllers_lib",
    srcs = [
        "StdinController.kt",
        "TwoIntsController.kt",
    ],
    visibility = ["//visibility:public"],
    deps = [
        "//emulator/src/org/pluverse/cs241/emulator/cpumodel",
        "//emulator/src/org/pluverse/cs241/emulator/views:views_lib",
    ],
)
