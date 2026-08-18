plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.google.ksp)
}

android {
    namespace = "com.example.safespace.core.data"
    compileSdk = 36

    defaultConfig {
        minSdk = 26
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

// Экспорт схем Room (schemas/<version>.json) — нужен MigrationTestHelper
// и стабильному диффу схем в git.
ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}

dependencies {
    implementation(projects.core.domain)
    implementation(libs.kotlinx.datetime)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    implementation(libs.health.connect)
    implementation(libs.koin.androidx.workmanager)
    implementation(libs.androidx.workmanager)
    ksp(libs.room.compiler)

    // MigrationTestHelper + JUnit4 runner для androidTest
    androidTestImplementation(libs.room.testing)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.junit)

    // JVM-юниты (парсеры, чистая Kotlin-логика — без эмулятора)
    testImplementation(libs.junit)
}
