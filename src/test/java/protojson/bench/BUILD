java_binary(
    name = "bench",
    srcs = ["ProtoJsonBenchmark.java"],
    main_class = "org.openjdk.jmh.Main",
    deps = [
        "//src/main/java/protojson",
        "//src/main/proto:test_java_proto",
        "//third_party:jmh",
        "@maven//:com_fasterxml_jackson_core_jackson_annotations",
        "@maven//:com_fasterxml_jackson_core_jackson_core",
        "@maven//:com_fasterxml_jackson_core_jackson_databind",
        "@maven//:com_google_guava_guava",
        "@maven//:com_google_protobuf_protobuf_java",
        "@maven//:com_google_protobuf_protobuf_java_util",
        "@maven//:com_hubspot_jackson_jackson_datatype_protobuf",
    ],
)
