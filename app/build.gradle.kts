plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.ajedrezprueba"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.ajedrezprueba"
        minSdk = 30
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
    kotlinOptions {
        jvmTarget = "11"
    }
    viewBinding {
        enable = true
    }
}

dependencies {

    //Frames
    implementation("androidx.fragment:fragment-ktx:1.8.5")

    //google-services
    implementation("com.google.android.gms:play-services-auth:21.3.0")

    //Para Imagenes
    implementation("com.google.android.material:material:1.11.0")

    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")

    //Picasso
    implementation("com.squareup.picasso:picasso:2.8")

    //LICHESS
    implementation("com.squareup.okhttp3:okhttp:4.12.0")

    //SWIPE
    implementation(libs.androidx.swiperefreshlayout)

    //CHESSLIB
    //implementation ("com.github.bhlangonijr:chesslib:1.3.0")
    //implementation ("com.github.bhlangonijr:chesslib:1.3.4")

    implementation (libs.picasso)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.firebase.auth)
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)
    implementation(libs.firebase.database)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}