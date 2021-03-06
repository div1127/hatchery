package edu.umontreal.hatchery.roslaunch.runconfig

import com.intellij.execution.Executor
import com.intellij.execution.configurations.LocatableConfigurationBase
import com.intellij.execution.configurations.RunProfileState
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.project.Project
import edu.umontreal.hatchery.cli.RosCommandLineState
import edu.umontreal.hatchery.settings.RosConfig

class RosLaunchRunConfig : LocatableConfigurationBase<RunProfileState> {
  constructor(project: Project, name: String) :
    super(project, RosLaunchRunConfigFactory, name)

  internal var remoteAddress = RosConfig.settings.remoteAddress
  internal var remoteRosPath = RosConfig.settings.remoteRosPath

  var rosLaunchPath = ""
  var rosPackagePath = ""

  override fun getConfigurationEditor() =
    RosLaunchSettingsEditor(project, rosPackagePath, rosLaunchPath)

  override fun getState(executor: Executor, environment: ExecutionEnvironment) =
    RosConfig.settings.localRos.run {
      RosCommandLineState(environment, shell.name, "-c", launch(rosPackagePath, rosLaunchPath).toString())

    }
//    RunAnythingRunProfileState(environment, RosConfig.settings.localRos.launch(rosPackagePath, rosLaunchPath).toString())
}