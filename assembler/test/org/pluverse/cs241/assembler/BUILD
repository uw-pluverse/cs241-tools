load("@io_bazel_rules_kotlin//kotlin:jvm.bzl", "kt_jvm_library", "kt_jvm_test")
load("//test:test.bzl", "golden_test")

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

ASSEMBLER_BINARY = "//assembler/src/org/pluverse/cs241/assembler:assembler"

genrule(
    name = "generate_pretty_visitor_output",
    srcs = [
        "testdata/all.asm",
    ],
    outs = [
        "pretty_visitor_output.txt",
    ],
    cmd = "$(location %s) " % ASSEMBLER_BINARY +
          " $(location testdata/all.asm) " +
          " /dev/null " +
          " 2>&1 | sed '$$d' > $@",
    tools = [
        ASSEMBLER_BINARY,
    ],
)

golden_test(
    name = "golden_test_pretty_visitor",
    golden_file = "pretty_visitor_output.golden.txt",
    test_file = "pretty_visitor_output.txt",
)
