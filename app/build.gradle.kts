plugins {
  id("com.android.application")
  id("org.jetbrains.kotlin.android")
  kotlin("plugin.serialization") version "1.8.10"
}

android {
  namespace = "com.aura"
  compileSdk = 34

  defaultConfig {
    applicationId = "com.aura"
    minSdk = 24
    targetSdk = 33
    versionCode = 1
    versionName = "1.0"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
  }
  kotlinOptions {
    jvmTarget = "1.8"
  }
  buildFeatures {
    viewBinding = true
  }
}

dependencies {

  // Kotlin standard library
  implementation ("org.jetbrains.kotlin:kotlin-stdlib-jdk7:2.0.0")


  implementation ("androidx.activity:activity-ktx:1.9.1")

  implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.4")
  implementation ("androidx.lifecycle:lifecycle-livedata-ktx:2.8.4")

  // Kotlin coroutines
  implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")
  implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.0")

  implementation("androidx.core:core-ktx:1.13.1")
  implementation("androidx.appcompat:appcompat:1.7.0")
  implementation("com.google.android.material:material:1.12.0")
  implementation("androidx.annotation:annotation:1.8.2")
  implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.room:room-ktx:2.6.1")
    testImplementation("junit:junit:4.13.2")
  androidTestImplementation("androidx.test.ext:junit:1.2.1")
  androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")

  // Retrofit for Network Requests
  implementation("com.squareup.retrofit2:retrofit:2.9.0")
  implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
  implementation("com.squareup.retrofit2:converter-moshi:2.9.0")
  implementation ("com.squareup.retrofit2:converter-gson:2.9.0") // If you want to use Gson for JSON conversion



  // Kotlin serialization
  implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
  implementation ("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:1.0.0")




}