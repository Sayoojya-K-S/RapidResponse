plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.rapidresponse"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.rapidresponse"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.firestore)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.storage)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.database)

    implementation(libs.glide)
    annotationProcessor(libs.glide.compiler)
    implementation(libs.gson)
    implementation(libs.recyclerview)
    implementation(libs.cardview)
    implementation(libs.retrofit)         // Retrofit Core
    implementation(libs.retrofit.gson)    // Gson Converter for JSON Parsing
    implementation(libs.okhttp.logging)   // Logging Interceptor for Debugging
    implementation(libs.play.services.location)


    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

}