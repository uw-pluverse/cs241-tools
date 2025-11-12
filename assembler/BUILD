load("@rules_java//java:defs.bzl", "java_binary", "java_library")

# Create a java_binary to run ANTLR tool with all dependencies
java_binary(
    name = "antlr_tool_binary",
    main_class = "org.antlr.v4.Tool",
    runtime_deps = ["//:antlr_tool"],
    visibility = ["//visibility:private"],
)

genrule(
    name = "generate_antlr",
    srcs = ["grammar/Arm64Asm.g4"],
    tools = [":antlr_tool_binary"],
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
)

java_library(
    name = "assembler_lib",
    srcs = [
        "src/org/pluverse/cs241/assembler/Main.java",
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

