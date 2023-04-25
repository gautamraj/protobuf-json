load("//bzl:test.bzl", "junit4_test_suite")

junit4_test_suite(
    name = "tests",
    size = "small",
    srcs = glob([
        "*Test.java",
        "*ITCase.java",
    ]),
    deps = [
        "//src/main/java/protojson",
        "//src/main/proto:test_java_proto",
        "@maven//:com_fasterxml_jackson_core_jackson_annotations",
        "@maven//:com_fasterxml_jackson_core_jackson_core",
        "@maven//:com_fasterxml_jackson_core_jackson_databind",
        "@maven//:com_google_protobuf_protobuf_java",
        "@maven//:com_google_protobuf_protobuf_java_util",
        "@maven//:org_assertj_assertj_core",
    ],
)
