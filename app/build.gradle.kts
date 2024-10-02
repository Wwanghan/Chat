plugins {
    id("com.android.application")
}

android {
    namespace = "com.example.chat"
    compileSdk = 33

    // 启用 buildConfig 功能
    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        applicationId = "com.example.chat"
        minSdk = 33
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"
        // 获取数据库密钥
        buildConfigField("String", "API_KEY", "\"${project.findProperty("SQL_API_KEY") as String}\"")
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

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.8.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation("com.baidubce:qianfan:0.1.0")
    implementation("com.makeramen:roundedimageview:2.3.0")
    implementation("mysql:mysql-connector-java:5.1.30")
}