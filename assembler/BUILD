package(default_visibility = ["//visibility:public"])

load("@rules_java//java:defs.bzl", "java_binary", "java_library")
load("@io_bazel_rules_kotlin//kotlin:jvm.bzl", "kt_jvm_binary", "kt_jvm_library")

java_binary(
    name = "antlr_tool_binary",
    main_class = "org.antlr.v4.Tool",
    visibility = ["//visibility:private"],
    runtime_deps = ["//:antlr_tool"],
)

genrule(
    name = "generate_antlr",
    srcs = ["grammar/Arm64Asm.g4"],
    outs = [
        "grammar_gen/Arm64AsmLexer.java",
        "grammar_gen/Arm64AsmParser.java",
        "grammar_gen/Arm64AsmListener.java",
        "grammar_gen/Arm64AsmBaseListener.java",
        "grammar_gen/Arm64AsmVisitor.java",
        "grammar_gen/Arm64AsmBaseVisitor.java",
    ],
    cmd = """
      set -euo pipefail
      mkdir -p $(@D)/grammar_gen

      ANTLR_TOOL=$(location :antlr_tool_binary)
      GRAMMAR=$(location grammar/Arm64Asm.g4)
      OUTDIR="$(@D)/grammar_gen"

      # -Xexact-output-dir: make sure all generated files go to OUTDIR
      # -Werror: treat warnings as errors (to avoid "exit 0 but no files generated" trap)
      if ! "$$ANTLR_TOOL" \
           -Dlanguage=Java -visitor \
           -Xexact-output-dir -Werror \
           -o "$$OUTDIR" \
           "$$GRAMMAR" 2>&1; then
        echo "ANTLR generation failed" 1>&2
        exit 1
      fi
    """,
    tools = [":antlr_tool_binary"],
)

kt_jvm_library(
    name = "assembler_lib",
    srcs = [
        "src/org/pluverse/cs241/assembler/Main.kt",
        "src/org/pluverse/cs241/assembler/CodeGenVisitor.kt",
        ":generate_antlr",
    ],
    deps = [
        "//:antlr_runtime",
    ],
)

java_binary(
    name = "assembler",
    main_class = "org.pluverse.cs241.assembler.Main",
    runtime_deps = [":assembler_lib"],
)
