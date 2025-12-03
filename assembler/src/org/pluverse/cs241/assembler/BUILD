load("@io_bazel_rules_kotlin//kotlin:jvm.bzl", "kt_jvm_binary", "kt_jvm_library")
load("@rules_java//java:defs.bzl", "java_binary", "java_library")

package(default_visibility = ["//visibility:public"])

kt_jvm_library(
    name = "assembler_lib",
    srcs = [
        "CodeGenVisitor.kt",
        "Main.kt",
    ],
    deps = [
        "//:antlr_runtime",
        "//assembler/src/org/pluverse/cs241/assembler/grammar",
    ],
)

java_binary(
    name = "assembler",
    main_class = "org.pluverse.cs241.assembler.Main",
    runtime_deps = [":assembler_lib"],
)
