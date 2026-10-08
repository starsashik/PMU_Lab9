plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.app"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.example.app"
        minSdk = 26
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

// The API runs in Docker on the development computer. ADB reverse avoids
// emulator/VPN routing issues and also works with a USB-connected phone.
val reverseApiPort = tasks.register<Exec>("reverseApiPort") {
    group = "subtrack"
    description = "Forwards device port 8080 to the local Docker API"
    commandLine(
        androidComponents.sdkComponents.adb.get().asFile.absolutePath,
        "reverse",
        "tcp:8080",
        "tcp:8080"
    )
    isIgnoreExitValue = true
}

tasks.matching { it.name == "preDebugBuild" }.configureEach {
    dependsOn(reverseApiPort)
}

dependencies {
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.10.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.10.0")
    implementation("androidx.fragment:fragment-ktx:1.8.9")
    implementation("androidx.recyclerview:recyclerview:1.4.0")
    implementation("androidx.work:work-runtime-ktx:2.10.5")
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
}
