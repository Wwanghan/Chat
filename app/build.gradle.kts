plugins {
    id("com.android.application")
}

android {
    namespace = "com.example.chat"
    compileSdk = 34

    // 启用 buildConfig 功能
    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        applicationId = "com.example.chat"
        minSdk = 33
        targetSdk = 33
        versionCode = 1
        versionName = "1.2.1"
        // 获取数据库密钥
        buildConfigField("String", "SQL_API_KEY", "\"${project.findProperty("SQL_API_KEY") as String}\"")
        // 获取千帆大模型所需要的密钥
        buildConfigField("String", "API_QIANFAN_AK", "\"${project.findProperty("QIANFAN_AK") as String}\"")
        buildConfigField("String", "API_QIANFAN_SK", "\"${project.findProperty("QIANFAN_SK") as String}\"")

//        短信业务程序ID
        buildConfigField("String", "BMOB_APPLICATION_KEY", "\"${project.findProperty("BMOB_APPLICATION_KEY") as String}\"")
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

    packagingOptions {
        exclude("META-INF/DEPENDENCIES")


    }
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.squareup.okhttp3:okhttp:4.9.3")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    implementation("com.baidubce:qianfan:0.1.0")
    implementation("com.makeramen:roundedimageview:2.3.0")

    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

    implementation("com.github.bumptech.glide:glide:4.12.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.12.0")

    // 短信业务需要依赖的包
    implementation("io.github.bmob:android-sdk:4.0.5")
    implementation("io.reactivex.rxjava3:rxjava:3.1.9")
    implementation("io.reactivex.rxjava3:rxandroid:3.0.2")
    implementation("com.squareup.okhttp3:okhttp:4.8.1")
    implementation("com.squareup.okio:okio:2.2.2")
    implementation("com.google.code.gson:gson:2.8.5")

}