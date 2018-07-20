import de.undercouch.gradle.tasks.download.Download
import org.gradle.api.tasks.JavaExec
import org.gradle.kotlin.dsl.getValue
import org.gradle.kotlin.dsl.getting
import org.gradle.kotlin.dsl.kotlin
import org.gradle.kotlin.dsl.version
import org.gradle.language.base.internal.plugins.CleanRule
import org.jetbrains.grammarkit.GrammarKit
import org.jetbrains.intellij.tasks.RunIdeTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.grammarkit.GrammarKitPluginExtension
import org.jetbrains.grammarkit.tasks.*
import org.jetbrains.kotlin.backend.common.onlyIf

plugins {
  idea apply true
  kotlin("jvm") version "1.2.51" apply true
  // TODO: https://github.com/JetBrains/gradle-python-envs#usage
  id("com.jetbrains.python.envs") version "0.0.25"
  id("org.jetbrains.intellij") version "0.3.4" apply true
  id("de.undercouch.download") version "3.4.3" apply true
  id("org.jetbrains.grammarkit") version "2018.1.6" apply true
}

val rosDistro = "melodic"
val clionVersion = "2018.1.6"
val installPath = "${project.projectDir}/build/clion/clion-$clionVersion"
val downloadURL = "https://download.jetbrains.com/cpp/CLion-$clionVersion.tar.gz"
val projectRoot = properties["roject"] as? String ?: System.getenv()["DUCKIETOWN_ROOT"] ?: ""
val catkinRoot = "$projectRoot/catkin_ws"
val srcRoot = "$catkinRoot/src"
val cmakeFile = "$srcRoot/CMakeLists.txt"
val rosEnvScript = "/opt/ros/$rosDistro/setup.bash"
val rosDevScript = "$catkinRoot/devel/setup.bash"

tasks {
  val downloadClion = "downloadClion"(Download::class) {
    onlyIf { !file("$installPath.tar.gz").exists() }
    src(downloadURL)
    dest(file("$installPath.tar.gz"))
  }

  val unpackClion = "unpackClion"(Copy::class) {
    onlyIf { !file(installPath).exists() }
    from(tarTree("build/clion/clion-$clionVersion.tar.gz"))
    into(file("${project.projectDir}/build/clion"))
    dependsOn(downloadClion)
  }

  val setupRosEnv = "setupRosEnv"(Exec::class) {
    isIgnoreExitValue = true

    if (!File(srcRoot).isDirectory)
      throw GradleException("Project source $srcRoot does not exist!")

    if (!File(rosEnvScript).exists())
      throw GradleException("ROS environment script $rosEnvScript not found!")

    executable = "source $rosEnvScript"
    commandLine("catkin_make", "-C", catkinRoot)
    val pythonPath = System.getenv()["PYTHONPATH"] ?: ""
    environment = mapOf("PYTHONPATH" to "$srcRoot:$pythonPath")

    if (!File(rosDevScript).exists())
      throw GradleException("ROS development script $rosDevScript not found!")

    File(rosDevScript).setExecutable(true)
    commandLine(rosDevScript)

    if (!File(cmakeFile).exists())
      throw GradleException("$cmakeFile was not found. Could not continue.")
  }

  withType<RunIdeTask> {
    dependsOn(unpackClion)
    dependsOn(setupRosEnv)

    println("Project root directory: $projectRoot")
    args = listOf(cmakeFile)
  }

  val generateROSInterfaceLexer = "generateLexer"(GenerateLexer::class) {
    source = "src/main/grammars/ROSInterface.flex"
    targetDir = "src/main/java/edu/umontreal/hatchery/rosinterface"
    targetClass = "ROSInterfaceLexer"
    purgeOldFiles = true
  }

  val generateROSInterfaceParser = "generateParser"(GenerateParser::class) {
    source = "src/main/grammars/ROSInterface.bnf"
    targetRoot = "src/main/java"
    pathToParser = "/edu/umontreal/hatchery/parser/ROSInterfaceParser.java"
    pathToPsiRoot = "/edu/umontreal/hatchery/psi"
    purgeOldFiles = true
  }

  withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
    dependsOn(generateROSInterfaceLexer, generateROSInterfaceParser)
  }
}

intellij {
  pluginName = "hatchery"
  version = clionVersion
  updateSinceUntilBuild = false
  if (hasProperty("roject")) downloadSources = false
  alternativeIdePath = "build/clion/clion-$clionVersion"

  setPlugins("name.kropp.intellij.makefile:1.2.2", // Makefile support
      "org.intellij.plugins.markdown:181.4668.12", // Markdown support
      "net.seesharpsoft.intellij.plugins.csv:1.3", // CSV file support
      "com.intellij.ideolog:181.0.7.0",            // Log file support
      "Pythonid:2018.1.181.4668.68",               // Python   support
      "BashSupport:1.6.13.181",                    // [Ba]sh   support
      "Docker:181.4668.68",                        // Docker   support
      "PsiViewer:2018.1.2",                        // PSI view support
//      "IdeaVIM:0.49",
//      "AceJump:3.5.0",
      "yaml")                                      // YML file support
}

envs {
  bootstrapDirectory = File(buildDir, "pythons")
  envsDirectory = File(buildDir, "envs")

  conda("Miniconda2", "Miniconda2-latest", listOf("numpy", "pillow"))
  // TODO: figure out how to setup conda inside the project structure
}

group = "edu.umontreal"
version = "0.2.1"
