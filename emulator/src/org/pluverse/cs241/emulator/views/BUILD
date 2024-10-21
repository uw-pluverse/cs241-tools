load("@io_bazel_rules_kotlin//kotlin:jvm.bzl", "kt_jvm_binary", "kt_jvm_library")

kt_jvm_library(
    name = "views_lib",
    srcs = [
        "BasicEmulatorView.kt",
        "CliView.kt",
        "GuiView.kt",
    ],
    visibility = ["//visibility:public"],
    deps = [
        "//:lanterna",
        "//emulator/src/org/pluverse/cs241/emulator/cpumodel",
        "//emulator/src/org/pluverse/cs241/emulator/views/lanterna:components",
    ],
)

kt_jvm_binary(
    name = "testapp",
    srcs = ["Test.kt"],
    main_class = "org.pluverse.cs241.emulator.views.Test",
    deps = [
        "//:lanterna",
    ],
)
