plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.vietflightinventory"
    compileSdk = 35
    buildFeatures {
        buildConfig = true
    }
    defaultConfig {
        applicationId = "com.example.vietflightinventory"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val mongoUser = project.property("MONGO_ATLAS_USER")?.toString() ?: ""
        val mongoPassword = project.property("MONGO_ATLAS_PASSWORD")?.toString() ?: ""
        val mongoClusterUrl = project.property("MONGO_ATLAS_CLUSTER_URL")?.toString() ?: ""
        val mongoDbName = project.property("MONGO_ATLAS_DB_NAME")?.toString() ?: ""

        buildConfigField("String", "MONGO_USER", "\"$mongoUser\"")
        buildConfigField("String", "MONGO_PASSWORD", "\"$mongoPassword\"")
        buildConfigField("String", "MONGO_CLUSTER_URL", "\"$mongoClusterUrl\"")
        buildConfigField("String", "MONGO_DB_NAME", "\"$mongoDbName\"")

        val connectionString = if (mongoUser.isNotEmpty() && mongoPassword.isNotEmpty() && mongoClusterUrl.isNotEmpty() && mongoDbName.isNotEmpty()) {
            "mongodb+srv://$mongoUser:$mongoPassword@$mongoClusterUrl/$mongoDbName?retryWrites=true&w=majority&appName=Cluster0"
        } else {
            ""
        }
        buildConfigField("String", "MONGO_CONNECTION_STRING", "\"$connectionString\"")
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
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(libs.realm)
    implementation(libs.mongodb.driver.sync)


}