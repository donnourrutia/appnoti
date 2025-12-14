plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.example.notigoal"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.notigoal"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }
    signingConfigs {
        create("release") {
            storeFile = file("keystore.jks")
            storePassword = "notigoal123"
            keyAlias = "key0"
            keyPassword = "notigoal123"
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            // 2. VINCULA LA FIRMA AQUÍ
            signingConfig = signingConfigs.getByName("release")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
        isCoreLibraryDesugaringEnabled = true
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.8"
    }

    // Configuración para evitar conflictos de licencias en los Tests
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "META-INF/LICENSE.md"
            excludes += "META-INF/LICENSE-notice.md"
        }
    }
}

dependencies {
    // Definición de versiones
    val room_version = "2.6.1"
    val lifecycle_version = "2.8.0"
    val datastore_version = "1.1.1"

    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // Core & UI
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycle_version")
    implementation("androidx.activity:activity-compose:1.9.0")
    implementation(platform("androidx.compose:compose-bom:2024.04.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")

    // ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle_version")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:$lifecycle_version")

    // WorkManager
    implementation("androidx.work:work-runtime-ktx:2.9.0")

    // Navegación
    implementation("androidx.navigation:navigation-compose:2.7.7")

    // Red (Retrofit, Gson, OkHttp)
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")

    // Carga de Imágenes (Coil)
    implementation("io.coil-kt:coil-compose:2.6.0")
    implementation("io.coil-kt:coil-svg:2.6.0")

    // Base de Datos (Room)
    implementation("androidx.room:room-runtime:$room_version")
    implementation("androidx.room:room-ktx:$room_version")
    ksp("androidx.room:room-compiler:$room_version")

    // Librería para manejar permisos fácilmente en Compose
    implementation("com.google.accompanist:accompanist-permissions:0.32.0")

    // Preferencias (DataStore)
    implementation("androidx.datastore:datastore-preferences:$datastore_version")

    // Soporte para java.time en APIs < 26
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")

    // ============================================================
    // TESTING UNITARIO (Lógica - Kotest & MockK)
    // ============================================================
    testImplementation("io.kotest:kotest-runner-junit5:5.8.0")
    testImplementation("io.kotest:kotest-assertions-core:5.8.0")
    testImplementation("io.mockk:mockk:1.13.9")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")

    // ============================================================
    // TESTING UI (Instrumentado - Solución para API 34+)
    // ============================================================
    // Definimos el BOM para versiones compatibles de Compose
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.04.01"))

    // Librerías de prueba de Compose
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    // MockK para Android
    androidTestImplementation("io.mockk:mockk-android:1.13.9")

    // ⚠️ CRÍTICO: Estas versiones actualizadas corrigen el error "InputManager" en emuladores nuevos
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation("androidx.test:runner:1.6.1")
    androidTestImplementation("androidx.test:core:1.6.1")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
}

// Configuración de JUnit 5 para Kotest (Fuera de dependencies)
tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}