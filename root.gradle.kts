plugins {
    alias(libs.plugins.kotlin) apply false
    alias(libs.plugins.shadow) apply false
    alias(libs.plugins.blossom) apply false
    alias(egt.plugins.multiversionRoot)
}

preprocess {
    val fabric12001 = createNode("1.20.1-fabric", 12001, "yarn")
    val forge12001 = createNode("1.20.1-forge", 12001, "yarn")
    val forge11902 = createNode("1.19.2-forge", 11902, "yarn")
    val fabric11902 = createNode("1.19.2-fabric", 11902, "yarn")

    fabric12001.link(forge12001)
    forge12001.link(forge11902)
    forge11902.link(fabric11902)
}