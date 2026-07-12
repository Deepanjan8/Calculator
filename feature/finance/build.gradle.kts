plugins {
    alias(libs.plugins.nexura.library.compose)
}

dependencies {
    implementation(project(":core:navigation"))

    implementation(libs.findLibrary("androidx-core-ktx").get())
    implementation(libs.findLibrary("androidx-navigation-compose").get())
    implementation(libs.findLibrary("androidx-compose-material-icons-extended").get())
}
