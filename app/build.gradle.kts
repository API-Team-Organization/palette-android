plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.example.palette"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.palette"
        minSdk = 24
        targetSdk = 34
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
    // OkHttp
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.1")

    // 이미지 확대
    implementation("com.davemorrissey.labs:subsampling-scale-image-view:3.0.0")

    // glide 이미지 로딩
    implementation ("com.github.bumptech.glide:glide:4.12.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.11.0")

    // shimmer 로딩뷰
    implementation("com.facebook.shimmer:shimmer:0.5.0")

    // viewPager2
    implementation("androidx.viewpager2:viewpager2:1.1.0")

    //coroutine 코루틴
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")

    // Retrofit 라이브러리
    implementation("com.squareup.retrofit2:retrofit:2.9.0")

    // Gson 변환기 라이브러리
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // dotsIndicator onBoarding dot
    implementation("com.tbuonomo:dotsindicator:5.0")

    // navigation
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")

    // Rive
    // During initialization, you may need to add a dependency
    // for Jetpack Startup
    implementation("app.rive:rive-android:9.5.5")
    implementation("androidx.startup:startup-runtime:1.1.1")

    // viewModel dependencies
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.8.3")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.3")

    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
}