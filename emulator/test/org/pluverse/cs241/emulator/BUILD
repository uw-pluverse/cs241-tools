load("@io_bazel_rules_kotlin//kotlin:jvm.bzl", "kt_jvm_library", "kt_jvm_test")

# kt_jvm_test cannot have resources. Have to define this library with resources.
# {name} has to match the test class name OR define test_class parameter in kt_jvm_test.

kt_jvm_library(
    name = "TestResources",
    srcs = [
        "MipsInstructionTests.kt"
    ],
    deps = [
        "//emulator/src/org/pluverse/cs241/emulator/cpumodel:cpumodel_lib",
        "//:truth",
        "//:guava",
        "@maven//:junit_junit",
    ]
)

kt_jvm_test(
    name = "MipsInstructionTests",
    srcs = [
        "MipsInstructionTests.kt"
    ],
    deps = [
        "//emulator/src/org/pluverse/cs241/emulator/cpumodel:cpumodel_lib",
        "//:truth",
        "//:guava",
        "@maven//:junit_junit",
    ]
)

kt_jvm_test(
    name = "MipsInstructionExecuteTests",
    srcs = [
        "MipsInstructionExecuteTests.kt"
    ],
    deps = [
        "//emulator/src/org/pluverse/cs241/emulator/cpumodel:cpumodel_lib",
        "//:truth",
        "//:guava",
        "@maven//:junit_junit",
        ":TestResources"
    ]
)

kt_jvm_test(
    name = "AddressTest",
    srcs = [
        "AddressTest.kt"
    ],
    deps = [
        "//emulator/src/org/pluverse/cs241/emulator/cpumodel:cpumodel_lib",
        "//:truth",
        "//:guava",
        "@maven//:junit_junit",
    ]
)