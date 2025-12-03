load("@io_bazel_rules_kotlin//kotlin:jvm.bzl", "kt_jvm_library", "kt_jvm_test")

kt_jvm_test(
    name = "CodeGenVisitorTest",
    srcs = ["CodeGenVisitorTest.kt"],
    associates = [
        "//assembler/src/org/pluverse/cs241/assembler:assembler_lib",
    ],
    deps = [
        "//:antlr_runtime",
        "//:truth",
    ],
)
