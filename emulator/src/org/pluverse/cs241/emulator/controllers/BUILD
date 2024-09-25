load("@io_bazel_rules_kotlin//kotlin:jvm.bzl", "kt_jvm_binary", "kt_jvm_library")

java_binary(
    name = "mips.twoints",
    main_class = "org.pluverse.cs241.emulator.controllers.TwoIntsController",
    runtime_deps = [":controllers_lib"],
)

java_binary(
    name = "mips.array",
    main_class = "org.pluverse.cs241.emulator.controllers.ArrayController",
    runtime_deps = [":controllers_lib"],
)

java_binary(
    name = "mips.stdin",
    main_class = "org.pluverse.cs241.emulator.controllers.StdinController",
    runtime_deps = [":controllers_lib"],
)

java_binary(
    name = "steppers.test",
    main_class = "org.pluverse.cs241.emulator.controllers.StepperControllerTest",
    runtime_deps = [":controllers_lib"],
)

java_binary(
    name = "steppers",
    main_class = "org.pluverse.cs241.emulator.controllers.StepperController",
    runtime_deps = [":controllers_lib"],
)

kt_jvm_library(
    name = "controllers_lib",
    srcs = [
        "ArrayController.kt",
        "StdinController.kt",
        "StepperController.kt",
        "TwoIntsController.kt",
    ],
    visibility = ["//visibility:public"],
    deps = [
        "//emulator/src/org/pluverse/cs241/emulator/cpumodel",
        "//emulator/src/org/pluverse/cs241/emulator/views:views_lib",
    ],
)
