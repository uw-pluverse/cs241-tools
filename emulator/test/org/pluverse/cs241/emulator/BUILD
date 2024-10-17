load("@io_bazel_rules_kotlin//kotlin:jvm.bzl", "kt_jvm_library", "kt_jvm_test")

# kt_jvm_test cannot have resources. Have to define this library with resources.
# {name} has to match the test class name OR define test_class parameter in kt_jvm_test.

kt_jvm_library(
    name = "TestResources",
    srcs = [
        "MipsInstructionTests.kt",
    ],
    deps = [
        "//:guava",
        "//:truth",
        "//emulator/src/org/pluverse/cs241/emulator/cpumodel:cpumodel_lib",
        "@maven//:junit_junit",
    ],
)

kt_jvm_test(
    name = "CpuEmulatorTest",
    srcs = [
        "CpuEmulatorTest.kt",
    ],
    data = [
        "testdata/lis.mips",
    ],
    deps = [
        "//:guava",
        "//:truth",
        "//emulator/src/org/pluverse/cs241/emulator/cpumodel",
        "//emulator/src/org/pluverse/cs241/emulator/views:views_lib",
    ],
)

kt_jvm_test(
    name = "MipsInstructionTests",
    srcs = [
        "MipsInstructionTests.kt",
    ],
    deps = [
        "//:guava",
        "//:truth",
        "//emulator/src/org/pluverse/cs241/emulator/cpumodel:cpumodel_lib",
        "@maven//:junit_junit",
    ],
)

kt_jvm_test(
    name = "MipsInstructionExecuteTests",
    srcs = [
        "MipsInstructionExecuteTests.kt",
    ],
    deps = [
        ":TestResources",
        "//:guava",
        "//:truth",
        "//emulator/src/org/pluverse/cs241/emulator/cpumodel:cpumodel_lib",
        "@maven//:junit_junit",
    ],
)

kt_jvm_test(
    name = "AddressTest",
    srcs = [
        "AddressTest.kt",
    ],
    deps = [
        "//:guava",
        "//:truth",
        "//emulator/src/org/pluverse/cs241/emulator/cpumodel:cpumodel_lib",
        "@maven//:junit_junit",
    ],
)
