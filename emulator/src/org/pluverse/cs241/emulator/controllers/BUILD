load("@io_bazel_rules_kotlin//kotlin:jvm.bzl", "kt_jvm_binary", "kt_jvm_library")

kt_jvm_binary(
    name = "mips.twoints",
    main_class = "org.pluverse.cs241.emulator.controllers.TwoIntsController",
    runtime_deps = [":controllers_lib"],
)

kt_jvm_library(
    name = "controllers_lib",
    srcs = [
        "TwoIntsController.kt",
        "ArrayController.kt",
        "StdinController.kt",
    ],
    visibility = ["//visibility:public"],
    deps = [
        "//emulator/src/org/pluverse/cs241/emulator/cpumodel:cpumodel",
        "//emulator/src/org/pluverse/cs241/emulator/views:views_lib"
    ],
)