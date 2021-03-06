package edu.umontreal.hatchery.roslaunch

import com.intellij.lang.xml.XMLLanguage
import com.intellij.openapi.fileTypes.LanguageFileType
import edu.umontreal.hatchery.filesystem.Icons

/*
 *  http://wiki.ros.org/Manifest
 */

object RosLaunchFileType : LanguageFileType(XMLLanguage.INSTANCE) {
  override fun getName() = "roslaunch"

  override fun getDescription() = "roslaunch"

  override fun getDefaultExtension() = "launch"

  override fun getIcon() = Icons.ros_launch
}