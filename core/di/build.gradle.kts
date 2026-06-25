plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.example.safespace.core.di"
    compileSdk = 36

    defaultConfig {
        minSdk = 26
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.koin.android)
    implementation(projects.core.data)
    implementation(libs.room.runtime)
}
