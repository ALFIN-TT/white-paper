import java.io.FileInputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    id("com.google.devtools.ksp") version "2.0.0-1.0.24"
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.alfie.whitepaper"
    compileSdk = 35

    val prop = Properties().apply {
        load(FileInputStream(File(rootProject.rootDir, "keystore.properties")))
    }

    signingConfigs {
        create("alfie_sign") {
            storeFile = file(prop.getProperty("storeFile"))
            storePassword = prop.getProperty("storePassword").toString()
            keyPassword = prop.getProperty("keyPassword").toString()
            keyAlias = prop.getProperty("keyAlias").toString()
        }
    }

    defaultConfig {
        applicationId = "com.alfie.whitepaper"
        minSdk = 24
        targetSdk = 34
        versionCode = 5
        versionName = "1.3.1"

        resValue("string", "add_mob_app_id", "ca-app-pub-2034233031425888~5418118640")
        resValue("string", "add_mob_unit_id", "ca-app-pub-2034233031425888/8714365824")
        resValue("string", "ad_mob_unit_id_interstitial", "ca-app-pub-2034233031425888/9992635936")
        // Creates a property for the FileProvider authority.
        val filesAuthorityValue = "$applicationId.fileprovider"
        // Creates a placeholder property to use in the manifest.
        manifestPlaceholders["filesAuthority"] = filesAuthorityValue
        // Adds a new field for the authority to the BuildConfig class.
        buildConfigField("String", "FILES_AUTHORITY", "\"${filesAuthorityValue}\"")


        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        signingConfig = signingConfigs.getByName("alfie_sign")
    }

    buildTypes {

        //buildConfigField("float", "MAP_CAMERA_FOCUS_ZOOM", project.properties["map_camera_focus_zoom"] as String)

        val appName = "White Paper"
        val date = SimpleDateFormat("dd_MM_yyyy_hh_mm_ss", Locale.ENGLISH).format(Date())

        release {
            isMinifyEnabled = true
            isShrinkResources = true
            isDebuggable = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("alfie_sign")
            resValue("string", "app_name", appName)
            setProperty(
                "archivesBaseName",
                appName.replace(' ', '_') + "_V${defaultConfig.versionName}_$date"
            )
        }

        create("staging") {
            isDebuggable = true
            matchingFallbacks += listOf("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("alfie_sign")
            resValue("string", "app_name", "$appName Test")
            setProperty(
                "archivesBaseName",
                appName.replace(' ', '_') + "_V${defaultConfig.versionName}_$date"
            )
        }

        debug {
            isMinifyEnabled = false
            isShrinkResources = false
            isDebuggable = true
            resValue("string", "app_name", "$appName Develop")
            setProperty(
                "archivesBaseName",
                appName.replace(' ', '_') + "_V${defaultConfig.versionName}_$date"
            )
        }
    }
    //use this when you need to implement product flavors.
    /*this.buildOutputs.all {
        val variantOutputImpl = this as com.android.build.gradle.internal.api.BaseVariantOutputImpl
        val variantName: String = variantOutputImpl.name
        val date = SimpleDateFormat("dd_MM_yyyy_hh_mm_ss", Locale.ENGLISH).format(Date())
        val outputFileName =
            "White_Paper".plus("_")
                .plus(variantName)
                .plus("_v_1.0.0")
                .plus("_").plus(date)
                .plus(".apk")
        variantOutputImpl.outputFileName = outputFileName
    }*/
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(libs.androidx.ui.text.google.fonts)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.compose.navigation)
    implementation(libs.androidx.constraintlayout.compose)

    implementation(libs.kotlinx.collections.immutable)

    // Hilt
    implementation(libs.hilt.android)
    implementation(libs.androidx.hilt.navigation.compose)
    ksp(libs.hilt.android.compiler)

    //ROOM DATABASE
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    //DATASTORE
    implementation(libs.androidx.datastore.core)
    implementation(libs.androidx.datastore.preferences)

    //ADMOB
    implementation (libs.play.services.ads)

    //IN APP UPDATE
    implementation(libs.app.update)
    implementation(libs.app.update.ktx)

    // GSON
    implementation(libs.gson)
}