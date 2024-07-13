load("@io_bazel_rules_kotlin//kotlin:jvm.bzl", "kt_jvm_library", "kt_jvm_binary")

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

kt_jvm_binary(
    name = "testapp",
    main_class = "org.pluverse.cs241.emulator.views.Test",
    srcs = ["Test.kt"],
    deps = [
        "//:lanterna"
    ]
)