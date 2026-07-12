plugins {
    alias(libs.plugins.nexura.library.compose)
}

dependencies {
    implementation(libs.findLibrary("androidx-core-ktx").get())
}
