// App "Burraco Timer": widget e notifiche; orari letti da ../web/data/schedule.json come asset (fonte unica).
plugins {
    id("com.android.application")
}

val repoRoot: File = rootDir.parentFile
val keystorePath: String? = System.getenv("BURRACO_KEYSTORE")?.takeIf { it.isNotBlank() && file(it).exists() }

android {
    namespace = "io.github.alessiogalbo.burraco"
    compileSdk = 36
    defaultConfig {
        applicationId = "io.github.alessiogalbo.burraco"
        minSdk = 26
        targetSdk = 36
        // Versione dal tag di rilascio (workflow), altrimenti 1 / 1.0.0 per le build locali.
        versionCode = System.getenv("BURRACO_VERSION_CODE")?.toIntOrNull() ?: 1
        versionName = System.getenv("BURRACO_VERSION_NAME")?.takeIf { it.isNotBlank() } ?: "1.0.0"
    }
    signingConfigs {
        create("release") {
            if (keystorePath != null) {
                storeFile = file(keystorePath)
                storePassword = System.getenv("BURRACO_KEYSTORE_PASSWORD")
                keyAlias = System.getenv("BURRACO_KEY_ALIAS")
                keyPassword = System.getenv("BURRACO_KEY_PASSWORD")
            }
        }
    }
    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            // Senza chiave (fork, prove locali) firma di debug: l'APK si installa comunque.
            signingConfig = signingConfigs.getByName(if (keystorePath != null) "release" else "debug")
        }
    }
    buildFeatures {
        buildConfig = true // VERSION_NAME per il controllo nuova versione
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    sourceSets {
        getByName("main") {
            assets.directories.add(File(repoRoot, "web/data").path)
        }
    }
    testOptions {
        unitTests.all { it.systemProperty("repoRoot", repoRoot.path) }
    }
    dependenciesInfo {
        includeInApk = false
        includeInBundle = false
    }
}

dependencies {
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.json:json:20250517")
}

// Copia l'APK release con il nome pubblicato: app/build/outputs/apk/release/burraco-widget.apk
val renameApk = tasks.register("renameReleaseApk") {
    val dir = layout.buildDirectory.dir("outputs/apk/release")
    doLast {
        val apk = dir.get().asFile.listFiles { f -> f.name.endsWith(".apk") && f.name != "burraco-widget.apk" }
            ?.firstOrNull() ?: throw GradleException("APK release non trovato")
        apk.copyTo(File(apk.parentFile, "burraco-widget.apk"), overwrite = true)
    }
}
tasks.matching { it.name == "assembleRelease" }.configureEach { finalizedBy(renameApk) }
