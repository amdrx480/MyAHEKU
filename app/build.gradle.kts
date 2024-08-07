plugins {
    id("com.android.application")
    id("kotlin-android")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("kotlin-parcelize")
//    id("dagger.hilt.android.plugin")


}

android {
    namespace = "com.dicoding.picodiploma.loginwithanimation"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.dicoding.picodiploma.loginwithanimation"
//        minSdk = 21
        // menggunakan minSdk 26 agar Apache POI dapat digunakan
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        named("release") {
            isMinifyEnabled = false
            setProguardFiles(listOf(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"))
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_9
        targetCompatibility = JavaVersion.VERSION_1_9
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    implementation("androidx.coordinatorlayout:coordinatorlayout:1.2.0")

    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")
    implementation("androidx.navigation:navigation-fragment:2.7.7")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    // si bangsat ini penyebab room dao gagal mulu
    debugImplementation("com.github.chuckerteam.chucker:library:4.0.0")
    releaseImplementation("com.github.chuckerteam.chucker:library-no-op:4.0.0")
    implementation("com.github.chuckerteam.chucker:library:4.0.0")

    implementation("androidx.test.espresso:espresso-idling-resource:3.4.0")
    androidTestImplementation("com.android.support.test.espresso:espresso-core:3.0.2")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
    androidTestImplementation("androidx.test.espresso:espresso-contrib:3.4.0")
    androidTestImplementation("com.android.support.test.espresso:espresso-contrib:3.0.2")
    androidTestImplementation("androidx.test.espresso:espresso-intents:3.4.0")


    implementation("androidx.datastore:datastore-preferences:1.0.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.1")
    implementation("androidx.activity:activity-ktx:1.8.2")

    //Internet
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.squareup.okhttp3:logging-interceptor:4.11.0")

    //room & paging
    implementation ("androidx.room:room-ktx:2.5.0-alpha01")
    //sama si bangsat init penyebab room dao bermasalah
    kapt ("androidx.room:room-compiler:2.6.1")
    implementation ("androidx.room:room-paging:2.5.0-alpha01")
    implementation ("androidx.paging:paging-runtime-ktx:3.1.1")

    implementation ("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

//    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2-native-mt")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.5.2")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.4.1")

    implementation ("com.github.bumptech.glide:glide:4.13.1")

    // Data untuk Ekspor dan Buat File Excel Menggunakan Apache POI
    implementation ("org.apache.poi:poi:5.2.2")
//    implementation ("org.apache.poi:poi:5.3.2")
    implementation ("org.apache.poi:poi-ooxml:5.2.2")

//    // Tambahkan ini untuk Log4j2 API
//    implementation("org.apache.logging.log4j:log4j-api:2.17.2")
//
//    // Tambahkan ini untuk Log4j2 Core
//    annotationProcessor("org.apache.logging.log4j:log4j-core:2.17.0")


    // DI Hilt
//    implementation ("com.google.dagger:hilt-android:2.41")
//    kapt ("com.google.dagger:hilt-android-compiler:2.41")

}