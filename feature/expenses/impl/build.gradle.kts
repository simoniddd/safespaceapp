plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.example.safespace.feature.expenses"
    compileSdk = 36
    defaultConfig { minSdk = 26 }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures { compose = true }
}

dependencies {
    implementation(libs.androidx.compose.foundation.layout)
    implementation(projects.feature.expenses.api)
    implementation(projects.core.data)
    implementation(projects.core.ui)
    implementation(projects.core.presentation)
    implementation(projects.core.domain)

    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.navigation.compose)
    implementation(libs.kotlinx.serialization.json)

    implementation(libs.koin.android)
    implementation(libs.koin.compose)

    // Для Clock/TimeZone/todayIn во ViewModel — тот же стек, что в HubViewModel.
    implementation(libs.kotlinx.datetime)
    implementation(libs.kotlinx.coroutines.core)
}
